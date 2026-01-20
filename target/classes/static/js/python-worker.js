// Python Web Worker für Pyodide-Ausführung mit Real-Time Output
let pyodide = null;
let isReady = false;
let isExecuting = false;
let executionId = null;

importScripts('code-analyzer.js');

// Custom stdout/stderr Handler für Real-Time Output
class RealTimeOutputCapture {
    constructor() {
        this.reset();
    }

    reset() {
        this.buffer = '';
        this.lastFlush = Date.now();
    }

    write(text) {
        // PyodideFuture und PyodideTask Warnungen filtern
        if (text && !text.includes('PyodideFuture') && !text.includes('PyodideTask')) {
            self.postMessage({
                type: 'output',
                data: text,
                timestamp: Date.now()
            });
        }
    }

    flush() {
        // Nicht mehr benötigt, da wir sofort senden
    }
}

const outputCapture = new RealTimeOutputCapture();

// Pyodide initialisieren
async function initializePyodide() {
    try {
        self.postMessage({
            type: 'status',
            data: 'loading',
            message: 'Pyodide wird geladen...'
        });

        // Pyodide laden
        importScripts('https://cdn.jsdelivr.net/pyodide/v0.24.1/full/pyodide.js');
        pyodide = await loadPyodide({
            indexURL: "https://cdn.jsdelivr.net/pyodide/v0.24.1/full/",
            stdout: (text) => outputCapture.write(text),
            stderr: (text) => outputCapture.write(text)
        });

        // Custom print function für sofortige Ausgabe
        pyodide.runPython(`
import sys
import builtins
import time

class RealTimePrinter:
    def __init__(self, original_stdout):
        self.original = original_stdout
        
    def write(self, text):
        self.original.write(text)
        self.flush()
        
    def flush(self):
        self.original.flush()

# print-Funktion überschreiben für sofortige Ausgabe
def real_time_print(*args, sep=' ', end='\\n', file=None, flush=False):
    # Normale print-Funktion mit allen Standard-Parametern verwenden
    builtins.original_print(*args, sep=sep, end=end, file=file, flush=True)

# Originale print-Funktion speichern und überschreiben
builtins.original_print = builtins.print
builtins.print = real_time_print
        `);

        isReady = true;
        self.postMessage({
            type: 'status',
            data: 'ready',
            message: 'Pyodide erfolgreich geladen ✓'
        });

    } catch (error) {
        self.postMessage({
            type: 'error',
            data: 'initialization_failed',
            message: 'Fehler beim Laden von Pyodide: ' + error.message
        });
    }
}

// Globale Variable für input() Synchronisation
let inputPromiseResolve = null;

// Python-Code ausführen
async function executePythonCode(code, id) {
    if (!isReady) {
        self.postMessage({
            type: 'error',
            data: 'not_ready',
            message: 'Pyodide ist noch nicht bereit'
        });
        return;
    }

    if (isExecuting) {
        self.postMessage({
            type: 'error', 
            data: 'already_executing',
            message: 'Code wird bereits ausgeführt'
        });
        return;
    }

    isExecuting = true;
    executionId = id;
    outputCapture.reset();

    // input() Funktion für Python konfigurieren (Promise-basiert)
    pyodide.globals.set('browser_input_impl', async (prompt) => {
        // WICHTIG: Zuerst stdout flushen damit alle bisherigen Ausgaben angezeigt werden
        pyodide.runPython('import sys; sys.stdout.flush(); sys.stderr.flush()');
        
        // Kurze Verzögerung um sicherzustellen, dass die Ausgabe im Browser gerendert wird
        await new Promise(resolve => setTimeout(resolve, 50));
        
        // Anfrage an Hauptthread senden
        self.postMessage({
            type: 'input_request',
            data: prompt || ''
        });
        
        // Promise zurückgeben, die von inputPromiseResolve aufgelöst wird
        return new Promise((resolve) => {
            inputPromiseResolve = resolve;
        });
    });

    pyodide.runPython(`
import builtins
from pyodide.ffi import JsProxy
import asyncio

# Globales Interrupt-Flag
_interrupt_flag = False

def set_interrupt():
    global _interrupt_flag
    _interrupt_flag = True

def clear_interrupt():
    global _interrupt_flag
    _interrupt_flag = False

# Async Input-Funktion
async def async_input(prompt=""):
    result = await browser_input_impl(prompt)
    # JsProxy zu Python-String konvertieren
    if isinstance(result, JsProxy):
        return str(result.to_py())
    return str(result)

# Unterbrechbare Async Sleep-Funktion
async def async_sleep(duration):
    global _interrupt_flag
    elapsed = 0
    step = 0.1  # Prüfe alle 0.1 Sekunden
    
    while elapsed < duration:
        if _interrupt_flag:
            raise KeyboardInterrupt("Ausführung gestoppt")
        
        wait_time = min(step, duration - elapsed)
        await asyncio.sleep(wait_time)
        elapsed += wait_time

# input() und sleep() als async-Funktionen überschreiben
builtins.input = async_input
builtins.sleep = async_sleep
    `);
    
    // Python Interrupt-Funktionen für JavaScript zugänglich machen
    pyodide.runPython('clear_interrupt()');

    self.postMessage({
        type: 'status',
        data: 'executing',
        message: 'Python-Code wird ausgeführt...'
    });

    self.postMessage({ type: 'objects_clear' });

    try {
        const metadata = CodeAnalyzer.extractClassMetadata(code);
        const hasDisplayable = metadata && metadata.length > 0;
        
        const hasInput = code.includes('input(');
        const hasSleep = code.includes('sleep(');
        const needsAsync = hasInput || hasSleep;
        
        let codeToExecute = code;
        
        // WICHTIG: Object Tracker ZUERST einfügen, BEVOR async-Wrapping
        if (hasDisplayable) {
            codeToExecute = injectObjectTracker(codeToExecute, metadata);
        }
        
        if (needsAsync) {
            // Intelligente Code-Transformation für async-Funktionen (input, sleep)
            let modifiedCode = codeToExecute;
            
            // 1. NICHT hier await einfügen - das passiert später im Kontext der async-Funktion
            
            // 2. Finde alle Funktionen, die async-Funktionen verwenden (input, sleep)
            const asyncFunctions = new Set();
            const lines = code.split('\n');
            
            // Schritt 1: Finde alle Funktionen, die direkt input() oder sleep() verwenden
            const functionBodies = new Map(); // functionName -> array of line contents
            for (let i = 0; i < lines.length; i++) {
                const line = lines[i];
                const trimmed = line.trim();
                
                if (trimmed.startsWith('def ')) {
                    const functionMatch = trimmed.match(/^def\s+(\w+)\s*\(/);
                    if (functionMatch) {
                        const functionName = functionMatch[1];
                        const functionIndent = line.search(/\S/);
                        const bodyLines = [];
                        
                        // Sammle alle Zeilen der Funktion
                        for (let j = i + 1; j < lines.length; j++) {
                            const nextLine = lines[j];
                            const nextIndent = nextLine.search(/\S/);
                            
                            if (nextLine.trim() && nextIndent <= functionIndent) {
                                break;
                            }
                            bodyLines.push(nextLine);
                        }
                        
                        functionBodies.set(functionName, bodyLines);
                        
                        // Direkte input() oder sleep() Verwendung prüfen
                        if (bodyLines.some(l => l.includes('input(') || l.includes('sleep('))) {
                            asyncFunctions.add(functionName);
                        }
                    }
                }
            }
            
            // Schritt 2: Rekursiv alle Funktionen finden, die async-Funktionen aufrufen
            let changed = true;
            let iterations = 0;
            while (changed && iterations < 10) { // Max 10 Iterationen zur Sicherheit
                changed = false;
                iterations++;
                
                for (const [funcName, bodyLines] of functionBodies.entries()) {
                    if (!asyncFunctions.has(funcName)) {
                        // Prüfe ob diese Funktion eine async-Funktion aufruft
                        for (const asyncFunc of asyncFunctions) {
                            const callPattern = new RegExp(`\\b${asyncFunc}\\s*\\(`);
                            if (bodyLines.some(l => callPattern.test(l))) {
                                asyncFunctions.add(funcName);
                                changed = true;
                                break;
                            }
                        }
                    }
                }
            }
            
            // 3. Funktionsdefinitionen zu async machen
            const modifiedLines = modifiedCode.split('\n');
            for (let i = 0; i < modifiedLines.length; i++) {
                const line = modifiedLines[i];
                const trimmed = line.trim();
                
                if (trimmed.startsWith('def ') && !trimmed.startsWith('async def ')) {
                    const functionMatch = trimmed.match(/^def\s+(\w+)\s*\(/);
                    if (functionMatch && asyncFunctions.has(functionMatch[1])) {
                        modifiedLines[i] = line.replace(/^(\s*)def /, '$1async def ');
                    }
                }
            }
            modifiedCode = modifiedLines.join('\n');
            
            // 4. Funktionsaufrufe von async-Funktionen mit await versehen
            for (const funcName of asyncFunctions) {
                // Regex um Funktionsaufrufe zu finden (nicht in def-Zeilen, nicht bereits awaited)
                const callRegex = new RegExp(`(?<!await\\s)(?<!def\\s.{0,50})\\b${funcName}\\s*\\(`, 'g');
                modifiedCode = modifiedCode.replace(callRegex, `await ${funcName}(`);
            }
            
            // 5. await für input() und sleep() einfügen VOR dem Wrapping
            modifiedCode = modifiedCode.replace(/(?<!await\s)(\s*)input\(/g, '$1await input(');
            modifiedCode = modifiedCode.replace(/(?<!await\s)(\s*)sleep\(/g, '$1await sleep(');
            
            // 6. Gesamten Code in async-Kontext wrappen mit Exception-Handling
            codeToExecute = `
import asyncio

async def __main__():
    try:
${modifiedCode.split('\n').map(line => '        ' + line).join('\n')}
    except KeyboardInterrupt:
        pass  # Benutzer hat gestoppt, keine Fehlermeldung

asyncio.ensure_future(__main__())
            `.trim();
        }
        
        // Code mit async/await Support ausführen
        await pyodide.runPythonAsync(codeToExecute);

        // Buffer leeren
        outputCapture.flush();

        if (isExecuting && executionId === id) {
            self.postMessage({
                type: 'completed',
                data: 'success',
                message: 'Code erfolgreich ausgeführt'
            });
        }

    } catch (error) {
        outputCapture.flush();
        
        // KeyboardInterrupt (vom Stopp-Button) als normale Beendigung behandeln
        const isInterrupted = error.message && (
            error.message.includes('KeyboardInterrupt') || 
            error.message.includes('Ausführung gestoppt')
        );
        
        if (isExecuting && executionId === id) {
            if (isInterrupted) {
                self.postMessage({
                    type: 'completed',
                    data: 'stopped',
                    message: 'Ausführung durch Benutzer gestoppt'
                });
            } else {
                self.postMessage({
                    type: 'error',
                    data: 'execution_error',
                    message: 'Python-Fehler: ' + error.message
                });
            }
        }
    } finally {
        if (executionId === id) {
            isExecuting = false;
            executionId = null;
        }
    }
}

function injectObjectTracker(code, metadata) {
    pyodide.globals.set('js_notify_object_created', (jsonString) => {
        const data = JSON.parse(jsonString);
        self.postMessage({
            type: 'object_created',
            data: data
        });
    });

    pyodide.globals.set('js_notify_object_updated', (jsonString) => {
        const data = JSON.parse(jsonString);
        self.postMessage({
            type: 'object_updated',
            data: data
        });
    });

    const trackerCode = `import json

class ObjectTracker:
    def __init__(self):
        self.objects = {}
        self.next_id = 0
        
    def _get_mangled_name(self, obj, attr_name):
        """
        Findet den tatsächlichen Attributnamen unter Berücksichtigung von Name Mangling.
        Private Attribute (__attr) werden zu _ClassName__attr.
        """
        # Kein Mangling nötig für public/protected oder dunder Attribute
        if not attr_name.startswith('__') or attr_name.endswith('__'):
            return attr_name
            
        # Durch die MRO gehen und nach dem mangled Namen suchen
        for klass in obj.__class__.__mro__:
            mangled = f"_{klass.__name__}{attr_name}"
            if hasattr(obj, mangled):
                return mangled
        
        # Fallback: Originalname
        return attr_name
        
    def register(self, obj, class_name, image_url, attr_names, labels):
        obj_id = f"obj_{self.next_id}"
        self.next_id += 1
        self.objects[id(obj)] = obj_id
        
        attributes = []
        for attr_name in attr_names:
            # Mangled Namen für private Attribute finden
            actual_name = self._get_mangled_name(obj, attr_name)
            if hasattr(obj, actual_name):
                value = getattr(obj, actual_name)
                label = labels.get(attr_name, attr_name)
                attributes.append({"name": attr_name, "label": label, "value": str(value)})
        
        data = {
            "id": obj_id,
            "className": class_name,
            "imageUrl": image_url or "",
            "attributes": attributes
        }
        js_notify_object_created(json.dumps(data))
        
    def update(self, obj, attr_name, value, labels):
        obj_id = self.objects.get(id(obj))
        if obj_id:
            label = labels.get(attr_name, attr_name)
            data = {
                "id": obj_id,
                "attributes": [{"name": attr_name, "label": label, "value": str(value)}]
            }
            js_notify_object_updated(json.dumps(data))

__object_tracker__ = ObjectTracker()
`;

    const lines = code.split('\n');
    const result = [trackerCode];
    
    const classNames = metadata.map(c => c.name);
    let lastClassEndLine = -1;
    
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmed = line.trim();
        
        const classMatch = trimmed.match(/^class\s+(\w+)/);
        if (classMatch && classNames.includes(classMatch[1])) {
            const className = classMatch[1];
            const classIndent = line.search(/\S/);
            
            let classEndLine = i;
            for (let j = i + 1; j < lines.length; j++) {
                const nextLine = lines[j];
                const nextTrimmed = nextLine.trim();
                const nextIndent = nextLine.search(/\S/);
                
                if (nextTrimmed && nextIndent <= classIndent && !nextTrimmed.startsWith('#')) {
                    classEndLine = j - 1;
                    break;
                }
                if (j === lines.length - 1) {
                    classEndLine = j;
                }
            }
            
            for (let k = i; k <= classEndLine; k++) {
                result.push(lines[k]);
            }
            
            const classInfo = metadata.find(c => c.name === className);
            const attrNames = classInfo.attributes.map(a => a.name);
            const labelsJson = JSON.stringify(classInfo.attributes.reduce((acc, a) => {
                acc[a.name] = a.label;
                return acc;
            }, {}));
            
            const wrapperCode = `
original_${className} = ${className}

class ${className}(original_${className}):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        # Mapping von mangled zu original Namen zur Laufzeit erstellen
        # (berücksichtigt auch private Attribute von Basisklassen)
        self.__mangled_to_original__ = {}
        for attr_name in ${JSON.stringify(attrNames)}:
            mangled = __object_tracker__._get_mangled_name(self, attr_name)
            if mangled != attr_name:
                self.__mangled_to_original__[mangled] = attr_name
        
        __object_tracker__.register(
            self,
            '${className}',
            ${classInfo.imageUrl ? `'${classInfo.imageUrl}'` : 'None'},
            ${JSON.stringify(attrNames)},
            ${labelsJson}
        )
    
    def __setattr__(self, name, value):
        super().__setattr__(name, value)
        
        # Mangled Namen zu original Namen konvertieren
        original_name = None
        
        # Versuche zuerst das Mapping zu verwenden (wenn vorhanden)
        if hasattr(self, '__mangled_to_original__'):
            original_name = self.__mangled_to_original__.get(name)
        
        # Fallback: Name Mangling manuell auflösen
        # (für Attribut-Zuweisungen vor Mapping-Erstellung oder neue private Attribute)
        if original_name is None:
            for attr_name in ${JSON.stringify(attrNames)}:
                mangled = __object_tracker__._get_mangled_name(self, attr_name)
                if mangled == name:
                    original_name = attr_name
                    # Cache für zukünftige Zugriffe (falls Mapping existiert)
                    if hasattr(self, '__mangled_to_original__'):
                        self.__mangled_to_original__[name] = original_name
                    break
            # Kein Mangling nötig
            if original_name is None:
                original_name = name
        
        # Update senden wenn es ein tracked Attribut ist
        if original_name in ${JSON.stringify(attrNames)}:
            __object_tracker__.update(self, original_name, value, ${labelsJson})
`;
            result.push(wrapperCode);
            
            lastClassEndLine = classEndLine;
            i = classEndLine;
        } else if (i > lastClassEndLine || lastClassEndLine === -1) {
            result.push(line);
        }
    }
    
    return result.join('\n');
}

// Ausführung stoppen
function stopExecution() {
    if (isExecuting && pyodide) {
        // Interrupt-Flag in Python setzen
        try {
            pyodide.runPython('set_interrupt()');
        } catch (e) {
            // Falls Python noch nicht bereit ist
        }
        
        isExecuting = false;
        const oldId = executionId;
        executionId = null;
        
        self.postMessage({
            type: 'status',
            data: 'stopped',
            message: 'Ausführung gestoppt'
        });
        
        // Buffer leeren
        outputCapture.flush();
    }
}

// Message-Handler
self.onmessage = function(e) {
    const { type, data } = e.data;
    
    switch (type) {
        case 'init':
            initializePyodide();
            break;
            
        case 'execute':
            executePythonCode(data.code, data.id);
            break;
            
        case 'stop':
            stopExecution();
            break;
            
        case 'input_response':
            // Antwort vom Hauptthread auf input() Anfrage
            if (inputPromiseResolve) {
                inputPromiseResolve(data);
                inputPromiseResolve = null;
            }
            break;
            
        case 'ping':
            self.postMessage({
                type: 'pong',
                data: {
                    ready: isReady,
                    executing: isExecuting
                }
            });
            break;
            
        default:
            self.postMessage({
                type: 'error',
                data: 'unknown_command',
                message: 'Unbekannter Befehl: ' + type
            });
    }
};

// Worker bereit signalisieren
self.postMessage({
    type: 'worker_ready',
    data: true,
    message: 'Worker bereit für Initialisierung'
});