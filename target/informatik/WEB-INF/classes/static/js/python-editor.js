// Globale Variablen
let pythonEditor;
let pythonWorker = null;
let pyodideReady = false;
let isExecuting = false;
let isResizing = false;
let typeCheckInterval = null;
let lastPythonCode = '';
let mypyReady = false;
let currentExecutionId = null;
let mainThreadPyodide = null; // Separate Pyodide-Instanz für MyPy

// Initialisierung beim Laden der Seite
document.addEventListener('DOMContentLoaded', function() {
    initializeEditors();
    initializeTabs();
    initializeOutputTabs();
    initializeResizer();
    initializeControls();
    initializePythonWorker();
    initializeTaskContent();
    initializeTutorialNavigation();
    ObjectViewer.init();

    // Standardcode für Demo-Zwecke mit Object Viewer Beispiel
    const exampleCode = `#@displayable
#@image: https://via.placeholder.com/80/4CAF50/FFFFFF?text=S
class Student:
    #@show(name="name", label="Name")
    #@show(name="age", label="Alter")
    def __init__(self, name, age):
        self.name = name
        self.age = age
        self.internal_id = 123

# Objekte erstellen
student1 = Student("Max Mustermann", 20)
student2 = Student("Lisa Schmidt", 22)
student3 = Student("Tom Weber", 19)

print("Studenten erstellt!")

# Attribut ändern
student1.age = 21
print("Max ist jetzt 21 Jahre alt")
`;
    pythonEditor.setValue(exampleCode);

    // Content laden und Cursor an den Anfang setzen
    loadSavedContent();
    pythonEditor.gotoLine(1);

    // Initialer Status
    updateSaveStatus('saved');

    // Änderungen verfolgen für Status-Updates
    pythonEditor.on('change', function() {
        updateSaveStatus('ready');
    });
});

// ACE Editor initialisieren
function initializeEditors() {
    try {
        // Python Editor
        pythonEditor = ace.edit("pythonEditor");
        pythonEditor.setTheme("ace/theme/monokai");
        pythonEditor.session.setMode("ace/mode/python");
        pythonEditor.setOptions({
            fontSize: 14,
            showPrintMargin: false,
            wrap: true,
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true,
            enableSnippets: true,
            showLineNumbers: true,
            highlightActiveLine: true,
            highlightSelectedWord: true,
            cursorStyle: "ace",
            mergeUndoDeltas: false,
            behavioursEnabled: true,
            wrapBehavioursEnabled: true
        });

        console.log("Editor erfolgreich initialisiert");
    } catch (error) {
        console.error("Fehler bei der Editor-Initialisierung:", error);
    }
}

// Tab-Navigation initialisieren (vereinfacht für nur Python)
function initializeTabs() {
    // Keine Tab-Navigation mehr nötig, da nur ein Tab vorhanden
    setTimeout(() => {
        pythonEditor.resize();
    }, 100);
}

// Output-Tab-Navigation initialisieren
function initializeOutputTabs() {
    const outputTabs = document.querySelectorAll('.output-tab[data-output-tab]');
    const outputContents = document.querySelectorAll('.output-content');
    const clearOutputBtn = document.getElementById('clearOutputBtn');

    outputTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const tabName = this.getAttribute('data-output-tab');

            // Aktive Tab-Klasse entfernen
            outputTabs.forEach(t => t.classList.remove('active'));
            outputContents.forEach(oc => oc.classList.remove('active'));

            // Neue aktive Tab setzen
            this.classList.add('active');
            document.getElementById(tabName + 'Output').classList.add('active');

            // Löschen-Button nur bei "result" (Ausgabe) Tab anzeigen
            if (tabName === 'result') {
                clearOutputBtn.classList.add('show');
            } else {
                clearOutputBtn.classList.remove('show');
            }
        });
    });

    // Initial das Löschen-Symbol anzeigen (da "result" standardmäßig aktiv ist)
    clearOutputBtn.classList.add('show');
}

// Resizable Splitter initialisieren
function initializeResizer() {
    const splitter = document.getElementById('splitter');
    const editorPanel = document.querySelector('.editor-panel');
    const outputPanel = document.querySelector('.output-panel');
    const container = document.querySelector('.main-content');

    // Mouse Events
    splitter.addEventListener('mousedown', startResize);

    // Touch Events für mobile Geräte
    splitter.addEventListener('touchstart', startResize);

    function startResize(e) {
        e.preventDefault();
        isResizing = true;

        if (e.type === 'mousedown') {
            document.addEventListener('mousemove', handleMouseMove);
            document.addEventListener('mouseup', stopResize);
        } else if (e.type === 'touchstart') {
            document.addEventListener('touchmove', handleTouchMove);
            document.addEventListener('touchend', stopResize);
        }

        document.body.style.userSelect = 'none';
        splitter.style.backgroundColor = '#007acc';
    }

    function handleMouseMove(e) {
        if (!isResizing) return;
        resize(e.clientX, e.clientY);
    }

    function handleTouchMove(e) {
        if (!isResizing) return;
        e.preventDefault();
        const touch = e.touches[0];
        resize(touch.clientX, touch.clientY);
    }

    function resize(clientX, clientY) {
        const containerRect = container.getBoundingClientRect();
        const isVertical = window.innerWidth <= 768;

        if (isVertical) {
            // Vertikaler Splitter für mobile Ansicht
            const mouseY = clientY - containerRect.top;
            const containerHeight = containerRect.height;
            const minHeight = 200;
            const maxHeight = containerHeight - 200;

            let newHeight = Math.max(minHeight, Math.min(maxHeight, mouseY));
            let topPercent = (newHeight / containerHeight) * 100;
            let bottomPercent = 100 - topPercent;

            editorPanel.style.height = topPercent + '%';
            outputPanel.style.height = bottomPercent + '%';
        } else {
            // Horizontaler Splitter für Desktop-Ansicht
            const mouseX = clientX - containerRect.left;
            const containerWidth = containerRect.width;
            const minWidth = 300;
            const maxWidth = containerWidth - 300;

            let newWidth = Math.max(minWidth, Math.min(maxWidth, mouseX));
            let leftPercent = (newWidth / containerWidth) * 100;
            let rightPercent = 100 - leftPercent;

            editorPanel.style.width = leftPercent + '%';
            outputPanel.style.width = rightPercent + '%';
        }

        // Editor-Größe anpassen
        setTimeout(() => {
            pythonEditor.resize();
        }, 10);
    }

    function stopResize() {
        isResizing = false;
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', stopResize);
        document.removeEventListener('touchmove', handleTouchMove);
        document.removeEventListener('touchend', stopResize);

        document.body.style.userSelect = '';
        splitter.style.backgroundColor = '';
    }

    // Window resize Handler für responsive Verhalten
    window.addEventListener('resize', function() {
        setTimeout(() => {
            pythonEditor.resize();
        }, 100);
    });
}

// Steuerungselemente initialisieren
function initializeControls() {
    // Schriftgröße-Dropdown
    const fontSizeSelect = document.getElementById('fontSizeSelect');
    fontSizeSelect.addEventListener('change', function() {
        const fontSize = parseInt(this.value);

        // Editor Schriftgröße anpassen
        pythonEditor.setFontSize(fontSize);

        // Ausgabe-Bereich Schriftgröße programmatisch setzen
        const consoleOutput = document.getElementById('consoleOutput');
        const htmlOutput = document.getElementById('htmlOutput');

        if (consoleOutput) {
            consoleOutput.style.fontSize = fontSize + 'px';
        }
        if (htmlOutput) {
            htmlOutput.style.fontSize = fontSize + 'px';
        }

        // Speichere Einstellung in localStorage
        localStorage.setItem('editorFontSize', fontSize);
    });

    // Gespeicherte Schriftgröße laden
    const savedFontSize = localStorage.getItem('editorFontSize');
    if (savedFontSize) {
        fontSizeSelect.value = savedFontSize;
        const fontSize = parseInt(savedFontSize);
        pythonEditor.setFontSize(fontSize);

        const consoleOutput = document.getElementById('consoleOutput');
        const htmlOutput = document.getElementById('htmlOutput');

        // Schriftgröße programmatisch setzen
        if (consoleOutput) {
            consoleOutput.style.fontSize = fontSize + 'px';
        }
        if (htmlOutput) {
            htmlOutput.style.fontSize = fontSize + 'px';
        }
    }

    // Vollbild-Button
    const fullscreenBtn = document.getElementById('fullscreenBtn');
    fullscreenBtn.addEventListener('click', function() {
        if (!document.fullscreenElement) {
            // Vollbild aktivieren
            document.documentElement.requestFullscreen().catch(err => {
                console.error('Fehler beim Aktivieren des Vollbildmodus:', err);
            });
        } else {
            // Vollbild verlassen
            document.exitFullscreen();
        }
    });

    // Fullscreen-Status überwachen und Icon aktualisieren
    document.addEventListener('fullscreenchange', function() {
        const icon = fullscreenBtn.querySelector('i');
        if (document.fullscreenElement) {
            // Im Vollbildmodus: Compress-Icon anzeigen
            icon.classList.remove('fa-expand');
            icon.classList.add('fa-compress');
            fullscreenBtn.title = 'Vollbild verlassen';
        } else {
            // Normal-Modus: Expand-Icon anzeigen
            icon.classList.remove('fa-compress');
            icon.classList.add('fa-expand');
            fullscreenBtn.title = 'Vollbild';
        }
    });

    // Ausgabe löschen Button
    const clearOutputBtn = document.getElementById('clearOutputBtn');
    clearOutputBtn.addEventListener('click', function() {
        document.getElementById('consoleOutput').innerHTML = '';
    });

    // Ausführen/Stoppen-Button
    document.getElementById('runBtn').addEventListener('click', function() {
        if (isExecuting) {
            stopPythonExecution();
        } else if (pyodideReady) {
            runPythonCode();
        } else {
            console.log('Pyodide noch nicht bereit');
        }
    });

    document.getElementById('saveButton').addEventListener('click', function() {
        saveContent();
    });

    document.getElementById('submitButton').addEventListener('click', function() {
        submitTask();
    });
}

// Utility-Funktionen
function getCurrentEditor() {
    return pythonEditor;
}

// Maximale Konsolengröße in Zeichen (kann hier angepasst werden)
const MAX_CONSOLE_SIZE = 50000; // 50.000 Zeichen - großzügig aber verhindert Performance-Probleme

function addToConsole(text, type = 'info') {
    const consoleOutput = document.getElementById('consoleOutput');
    const timestamp = new Date().toLocaleTimeString();
    const prefix = type === 'error' ? '❌' : type === 'warning' ? '⚠️' : 'ℹ️';

    // HTML-sicher escapen und dann mit <br> hinzufügen
    const escapedText = text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
    
    const newContent = `[${timestamp}] ${prefix} ${escapedText}<br>`;

    // Neuen Inhalt hinzufügen - verwende innerHTML für Konsistenz
    consoleOutput.innerHTML += newContent;

    // Bei Überschreitung der maximalen Größe von oben kürzen
    if (consoleOutput.innerHTML.length > MAX_CONSOLE_SIZE) {
        const excessChars = consoleOutput.innerHTML.length - MAX_CONSOLE_SIZE + 2000; // 2000 Zeichen extra entfernen
        consoleOutput.innerHTML = '...[frühere Ausgaben entfernt]...<br>' + consoleOutput.innerHTML.substring(excessChars);
    }

    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

function addToConsoleWithoutTimestamp(text) {
    const consoleOutput = document.getElementById('consoleOutput');

    // Zeilenumbrüche in <br> Tags umwandeln und HTML sicher escapen
    const escapedText = text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>');

    // Neuen Inhalt hinzufügen - immer mit <br> am Ende für Zeilenumbruch
    consoleOutput.innerHTML += escapedText + '<br>';

    // Bei Überschreitung der maximalen Größe von oben kürzen
    if (consoleOutput.innerHTML.length > MAX_CONSOLE_SIZE) {
        const excessChars = consoleOutput.innerHTML.length - MAX_CONSOLE_SIZE + 2000; // 2000 Zeichen extra entfernen
        consoleOutput.innerHTML = '...[frühere Ausgaben entfernt]...<br>' + consoleOutput.innerHTML.substring(excessChars);
    }

    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

// Python Worker initialisieren
function initializePythonWorker() {
    try {
        // Web Worker erstellen
        pythonWorker = new Worker(document.getElementById('default-link') + 'js/python-worker.js');

        // Worker Message Handler
        pythonWorker.onmessage = function(e) {
            handleWorkerMessage(e.data);
        };

        // Worker Error Handler
        pythonWorker.onerror = function(error) {
            addToConsole('Worker Fehler: ' + error.message, 'error');
            pyodideReady = false;
            updateRunButton('error');
        };

        // Worker initialisieren
        pythonWorker.postMessage({ type: 'init' });

    } catch (error) {
        addToConsole('Fehler beim Erstellen des Workers: ' + error.message, 'error');
        pyodideReady = false;
    }
}

// Python-Code ausführen (Web Worker)
function runPythonCode() {
    if (!pyodideReady) {
        addToConsole('Pyodide ist noch nicht bereit. Bitte warten...', 'warning');
        return;
    }

    if (isExecuting) {
        addToConsole('Code wird bereits ausgeführt...', 'warning');
        return;
    }

    const code = pythonEditor.getValue();
    if (!code.trim()) {
        addToConsole('Kein Python-Code zum Ausführen vorhanden.', 'warning');
        return;
    }

    // Execution State setzen
    isExecuting = true;
    currentExecutionId = Date.now();
    updateRunButton('executing');

    // Code an Worker senden
    pythonWorker.postMessage({
        type: 'execute',
        data: {
            code: code,
            id: currentExecutionId
        }
    });
}

// Python-Ausführung stoppen (mit Worker-Terminierung für Infinite Loops)
function stopPythonExecution() {
    if (!isExecuting) {
        return;
    }

    // UI auf "Stopping" setzen (aber isExecuting NICHT zurücksetzen)
    updateRunButton('stopping');
    addToConsole('Stoppe Ausführung...', 'info');

    // Versuche zunächst normales Stoppen
    pythonWorker.postMessage({ type: 'stop' });

    // Nach kurzer Zeit Worker IMMER hart terminieren bei Infinite Loops
    setTimeout(() => {
        // Hart-Terminierung für Infinite Loops - unabhängig vom aktuellen State
        if (isExecuting) {
            addToConsole('Stopp-Button gedrückt, Programm wird angehalten...', 'warning');
            terminateAndRecreateWorker();
        }
    }, 300); // Nur 300ms warten, dann sofort hart stoppen
}

// Worker hart terminieren und neu erstellen (für Infinite Loops)
function terminateAndRecreateWorker() {
    try {
        // Worker hart terminieren
        if (pythonWorker) {
            pythonWorker.terminate();
        }

        // State zurücksetzen
        pyodideReady = false;
        isExecuting = false;
        currentExecutionId = null;

        // UI auf Loading setzen
        updateRunButton('loading');
        addToConsole('Worker wird neu initialisiert...', 'info');

        // Neuen Worker erstellen
        initializePythonWorker();

    } catch (error) {
        addToConsole('Fehler beim Neustart des Workers: ' + error.message, 'error');
        updateRunButton('error');
    }
}

// Input-Anfrage vom Worker behandeln
function handleInputRequest(promptText) {
    // Prompt-Text in Console anzeigen
    if (promptText) {
        addToConsoleWithoutTimestamp(promptText);
    }
    
    // Browser-Popup für Eingabe
    const userInput = window.prompt(promptText || 'Eingabe:');
    
    // Eingabe in Console anzeigen
    if (userInput !== null) {
        addToConsoleWithoutTimestamp(userInput);
    }
    
    // Antwort an Worker senden
    pythonWorker.postMessage({
        type: 'input_response',
        data: userInput || ''
    });
}

// Worker Message Handler
function handleWorkerMessage(message) {
    const { type, data, message: msg } = message;

    switch (type) {
        case 'worker_ready':
            console.log('Worker bereit');
            break;

        case 'status':
            if (data === 'loading') {
                addToConsole(msg, 'info');
            } else if (data === 'ready') {
                pyodideReady = true;
                addToConsole(msg, 'info');
                updateRunButton('ready');
                // MyPy parallel im Hauptthread für Type-Checking laden (nur einmal)
                if (!mypyReady && !mainThreadPyodide) {
                    initializeMyPy().catch(error => {
                        console.error('MyPy Initialisierung fehlgeschlagen:', error);
                        addToConsole('Type-Checking deaktiviert (MyPy-Fehler)', 'warning');
                    });
                }
            } else if (data === 'executing') {
                addToConsole(msg, 'info');
            } else if (data === 'stopped' || data === 'interrupted') {
                isExecuting = false;
                currentExecutionId = null;
                updateRunButton('ready');
                addToConsole(msg, 'info');
            }
            break;

        case 'output':
            // Real-time output ohne Timestamp
            addToConsoleWithoutTimestamp(data);
            break;

        case 'objects_clear':
            ObjectViewer.clear();
            break;

        case 'object_created':
            ObjectViewer.createObject(data);
            break;

        case 'object_updated':
            ObjectViewer.updateObject(data);
            break;

        case 'object_deleted':
            ObjectViewer.deleteObject(data.id);
            break;

        case 'input_request':
            // Python input() Anfrage
            handleInputRequest(data);
            break;

        case 'completed':
            isExecuting = false;
            currentExecutionId = null;
            updateRunButton('ready');
            if (!data || data === 'success') {
                // Keine zusätzliche Nachricht bei erfolgreichem Abschluss
            }
            break;

        case 'error':
            isExecuting = false;
            currentExecutionId = null;
            updateRunButton('ready');

            if (data === 'not_ready') {
                addToConsole('Pyodide ist noch nicht bereit. Bitte warten...', 'warning');
            } else if (data === 'already_executing') {
                addToConsole('Code wird bereits ausgeführt...', 'warning');
            } else if (data === 'execution_error') {
                addToConsole(msg, 'error');
            } else if (data === 'initialization_failed') {
                pyodideReady = false;
                updateRunButton('error');
                addToConsole(msg, 'error');
            } else {
                addToConsole('Unbekannter Fehler: ' + msg, 'error');
            }
            break;

        default:
            console.log('Unbekannte Worker-Nachricht:', type, data);
    }
}

// Run Button Status aktualisieren
function updateRunButton(state) {
    const runBtn = document.getElementById('runBtn');

    switch (state) {
        case 'ready':
            runBtn.disabled = false;
            runBtn.style.opacity = '1';
            runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
            runBtn.className = 'btn btn-primary';
            break;

        case 'executing':
            runBtn.disabled = false;
            runBtn.style.opacity = '1';
            runBtn.innerHTML = '<i class="fas fa-stop"></i> Stoppen';
            runBtn.className = 'btn btn-danger';
            break;

        case 'stopping':
            runBtn.disabled = true;
            runBtn.style.opacity = '0.8';
            runBtn.innerHTML = '<i class="fas fa-pause"></i> Stoppt...';
            runBtn.className = 'btn btn-warning';
            break;

        case 'loading':
            runBtn.disabled = true;
            runBtn.style.opacity = '0.6';
            runBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Lädt...';
            runBtn.className = 'btn btn-secondary';
            break;

        case 'error':
            runBtn.disabled = true;
            runBtn.style.opacity = '0.6';
            runBtn.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Fehler';
            runBtn.className = 'btn btn-secondary';
            break;

        default:
            runBtn.disabled = true;
            runBtn.style.opacity = '0.6';
            runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
            runBtn.className = 'btn btn-secondary';
    }
}



// MyPy initialisieren (läuft parallel im Hauptthread für Type-Checking)
async function initializeMyPy() {
    try {
        addToConsole('MyPy wird installiert...', 'info');

        // Schritt 1: Eigene Pyodide-Instanz für MyPy laden
        mainThreadPyodide = await loadPyodide({
            indexURL: "https://cdn.jsdelivr.net/pyodide/v0.24.1/full/"
        });

        // Schritt 2: Micropip laden für zusätzliche Pakete
        await mainThreadPyodide.loadPackage(['micropip']);
        addToConsole('micropip geladen ✓', 'info');

        // Schritt 3: Erforderliche Pakete installieren
        await mainThreadPyodide.runPythonAsync(`
import micropip
# typing-extensions und mypy_extensions über micropip installieren
await micropip.install(['typing-extensions', 'mypy-extensions'])
        `);
        addToConsole('typing-extensions und mypy-extensions installiert ✓', 'info');

        // Schritt 4: MyPy installieren
        await mainThreadPyodide.loadPackage(['mypy']);
        addToConsole('MyPy-Paket geladen ✓', 'info');

        // Schritt 5: MyPy-Umgebung konfigurieren
        mainThreadPyodide.runPython(`
import sys
import os
import tempfile

# MyPy imports
try:
    import mypy.api
    import mypy.main
    from mypy import build
    from mypy.options import Options
    from mypy.fscache import FileSystemCache
    print("MyPy Module erfolgreich importiert")
except ImportError as e:
    print(f"MyPy Import-Fehler: {e}")
    raise

class RobustTypeChecker:
    def __init__(self):
        self.temp_dir = tempfile.mkdtemp()
        self.cache = FileSystemCache()
        print(f"TypeChecker initialisiert mit temp_dir: {self.temp_dir}")

    def check_code(self, code):
        """
        Überprüft Python-Code mit MyPy
        """
        errors = []
        temp_file = None

        try:
            # Temporäre Datei erstellen
            temp_file = os.path.join(self.temp_dir, 'check_code.py')

            with open(temp_file, 'w', encoding='utf-8') as f:
                f.write(code)

            print(f"Code in temporäre Datei geschrieben: {temp_file}")

            # MyPy mit API ausführen
            try:
                result = mypy.api.run([
                    temp_file,
                    '--no-error-summary',
                    '--no-color-output',
                    '--ignore-missing-imports',
                    '--follow-imports=silent'
                ])

                stdout, stderr, exit_code = result
                print(f"MyPy Ergebnis - Exit Code: {exit_code}")
                print(f"MyPy Stdout: {stdout}")
                print(f"MyPy Stderr: {stderr}")

                # Stdout-Fehler parsen
                if stdout and stdout.strip():
                    for line in stdout.strip().split('\\n'):
                        if line and temp_file in line and ':' in line:
                            # Format: filename:line:column: level: message
                            parts = line.split(':', 4)
                            if len(parts) >= 4:
                                try:
                                    line_num = int(parts[1])
                                    error_level = parts[3].strip() if len(parts) > 3 else 'error'
                                    message = parts[4].strip() if len(parts) > 4 else line

                                    # Bereinige error_level
                                    if 'error' in error_level.lower():
                                        error_type = 'error'
                                    elif 'warning' in error_level.lower():
                                        error_type = 'warning'
                                    else:
                                        error_type = 'info'

                                    errors.append((line_num, error_type, message))
                                    print(f"Fehler gefunden: Zeile {line_num}, Typ {error_type}, Nachricht: {message}")
                                except (ValueError, IndexError) as e:
                                    print(f"Fehler beim Parsen der Zeile '{line}': {e}")
                                    continue

            except Exception as api_error:
                print(f"MyPy API Fehler: {api_error}")
                errors.append((1, 'error', f'MyPy API-Fehler: {str(api_error)}'))

        except Exception as e:
            print(f"Allgemeiner Fehler im Type-Checker: {e}")
            errors.append((1, 'error', f'Type-Checker-Fehler: {str(e)}'))

        finally:
            # Temporäre Datei aufräumen
            if temp_file and os.path.exists(temp_file):
                try:
                    os.remove(temp_file)
                except:
                    pass

        print(f"Type-Checking abgeschlossen. {len(errors)} Fehler gefunden.")
        return errors

# TypeChecker-Instanz erstellen
try:
    type_checker = RobustTypeChecker()
    print("RobustTypeChecker erfolgreich erstellt")
except Exception as e:
    print(f"Fehler beim Erstellen des TypeCheckers: {e}")
    raise
        `);

        mypyReady = true;
        addToConsole('MyPy erfolgreich konfiguriert ✓', 'info');

        // Type-Checking-Intervall starten
        startTypeChecking();

    } catch (error) {
        addToConsole('Fehler beim Initialisieren von MyPy: ' + error.message, 'error');
        console.error('MyPy Initialisierungsfehler:', error);
        mypyReady = false;

        // Fallback: Einfacher Syntax-Checker ohne MyPy
        addToConsole('Fallback: Verwende einfachen Syntax-Checker', 'warning');
        initializeFallbackChecker();
    }
}

// Fallback-Checker ohne MyPy
function initializeFallbackChecker() {
    mainThreadPyodide.runPython(`
import ast
import sys

class FallbackTypeChecker:
    def check_code(self, code):
        errors = []
        try:
            # Syntax-Check mit AST
            ast.parse(code)
            print("Syntax-Check erfolgreich")
        except SyntaxError as e:
            line_num = e.lineno or 1
            message = f"Syntax-Fehler: {e.msg}"
            errors.append((line_num, 'error', message))
            print(f"Syntax-Fehler gefunden: {message}")
        except Exception as e:
            errors.append((1, 'error', f"Code-Fehler: {str(e)}"))
            print(f"Allgemeiner Code-Fehler: {e}")

        return errors

type_checker = FallbackTypeChecker()
    `);

    mypyReady = true;
    addToConsole('Fallback Type-Checker aktiviert', 'info');
    startTypeChecking();
}

// Type-Checking starten
function startTypeChecking() {
    if (typeCheckInterval) {
        clearInterval(typeCheckInterval);
    }

    typeCheckInterval = setInterval(() => {
        if (mypyReady) {
            checkPythonTypes();
        }
    }, 800); // Alle 800ms
}

// Python-Code Type-Checking (async für UI-Performance)
async function checkPythonTypes() {
    const currentCode = pythonEditor.getValue();

    // Nur prüfen wenn sich der Code geändert hat
    if (currentCode === lastPythonCode || !currentCode.trim()) {
        return;
    }

    lastPythonCode = currentCode;

    try {
        // Alte Marker entfernen
        clearTypeErrors();

        // Stub-Definitionen für Runtime-Funktionen hinzufügen (nur für MyPy)
        const stubDefinitions = `# Type stubs für Runtime-Funktionen
def sleep(duration: float) -> None:
    """Wartet für die angegebene Anzahl von Sekunden."""
    pass

`;
        const codeToCheck = stubDefinitions + currentCode;

        // Anzahl der Stub-Zeilen berechnen (nur wenn Stubs vorhanden)
        const trimmedStubs = stubDefinitions.trim();
        const stubLineCount = trimmedStubs ? trimmedStubs.split('\n').length : 0;

        // MyPy ausführen (async für UI-Responsiveness)
        const result = await mainThreadPyodide.runPythonAsync(`
errors = type_checker.check_code('''${codeToCheck.replace(/'/g, "\\'")}''')
# Zeilennummern korrigieren (Stub-Zeilen abziehen, wenn vorhanden)
stub_lines = ${stubLineCount}
corrected_errors = []
for line, error_type, message in errors:
    # Fehler in Stub-Zeilen ignorieren
    if stub_lines > 0 and line <= stub_lines:
        continue
    # Offset anwenden: ursprüngliche Zeile minus Stub-Zeilen
    # MyPy liefert 1-basierte Zeilennummern, die wir so beibehalten
    adjusted_line = line - stub_lines
    corrected_errors.append((adjusted_line, error_type, message))
corrected_errors
        `);
        const errors = result.toJs();
        result.destroy(); // PyProxy cleanup

        // Fehler im Editor markieren
        if (errors && errors.length > 0) {
            markTypeErrors(errors);
        }

    } catch (error) {
        console.error('Type-Checking-Fehler:', error);
    }
}

// Type-Errors im Editor markieren
function markTypeErrors(errors) {
    const session = pythonEditor.getSession();

    errors.forEach(([lineNum, errorType, message]) => {
        // lineNum ist nach Stub-Korrektur noch 1-basiert (MyPy-Format)
        // ACE Editor ist 0-basiert, daher -1
        // Zusätzlich -1 für korrekte Zeilenanzeige
        const line = lineNum - 2;

        // Annotation hinzufügen
        session.setAnnotations(session.getAnnotations().concat([{
            row: line,
            column: 0,
            text: message,
            type: errorType === 'error' ? 'error' : 'warning'
        }]));

        // Marker für rote Unterstreichung
        const Range = ace.require('ace/range').Range;
        const range = new Range(line, 0, line, pythonEditor.getSession().getLine(line).length);

        session.addMarker(range, errorType === 'error' ? 'type-error' : 'type-warning', 'text');
    });
}

// Type-Errors löschen
function clearTypeErrors() {
    const session = pythonEditor.getSession();

    // Annotations löschen
    session.clearAnnotations();

    // Marker löschen
    const markers = session.getMarkers();
    if (markers) {
        Object.keys(markers).forEach(markerId => {
            const marker = markers[markerId];
            if (marker.clazz === 'type-error' || marker.clazz === 'type-warning') {
                session.removeMarker(markerId);
            }
        });
    }
}

// Type-Checking aktualisieren
function updateTypeCheckingForTab() {
    if (mypyReady) {
        // Sofortiges Type-Checking für Python
        setTimeout(checkPythonTypes, 100);
    }
}

// Lade gespeicherte Inhalte beim Start
function loadSavedContent() {
    const contentElement = document.getElementById('currentContent');
    if (contentElement) {
        const savedContent = contentElement.textContent.trim();
        loadContentToView(savedContent);
    }
}

// Markdown-zu-HTML Parser
function renderMarkdown(markdownText) {
    if (typeof marked !== 'undefined') {
        return marked.parse(markdownText);
    } else {
        // Fallback für einfaches Markdown-Rendering
        return markdownText
            .replace(/^# (.*$)/gm, '<h1>$1</h1>')
            .replace(/^## (.*$)/gm, '<h2>$1</h2>')
            .replace(/^### (.*$)/gm, '<h3>$1</h3>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/`(.*?)`/g, '<code>$1</code>')
            .replace(/^\- (.*$)/gm, '<ul><li>$1</li></ul>')
            .replace(/^\d+\. (.*$)/gm, '<ol><li>$1</li></ol>')
            .replace(/\n/g, '<br>');
    }
}

// Tutorial-Inhalte Array
const tutorialText = document.getElementById('tutorial').textContent.trim();

let tutorialContents;
if (tutorialText) {
  // Inhalt vorhanden → als JS-Literal ausführen
  tutorialContents = new Function(`return (${tutorialText});`)();
} 

console.log(tutorialContents);

let currentTutorialIndex = 0;

// Task-Content initialisieren
function initializeTaskContent() {
    updateTaskTab(document.getElementById("description").textContent);
}

// Tutorial Navigation initialisieren
function initializeTutorialNavigation() {
    const tutorialOutput = document.getElementById('tutorialOutput');

    const tutorialTab = document.querySelector('.output-tab[data-output-tab="tutorial"]'); // Referenz auf das Tutorial-Tab
    // Überprüfen, ob tutorialContents leer ist
    if (!tutorialContents || tutorialContents.length === 0) {
        // Tab ausblenden, wenn kein Inhalt vorhanden ist
        if (tutorialTab) {
            tutorialTab.style.display = 'none'; // Tab ausblenden
        }
        return; // Funktion beenden
    }

    // Navigation HTML erstellen
    const navigationHTML = `
        <div class="tutorial-navigation">
            <button id="tutorialPrev" class="nav-arrow">←</button>
            <div class="tutorial-dots">
                ${tutorialContents.map((_, index) =>
        `<span class="tutorial-dot ${index === 0 ? 'active' : ''}" data-index="${index}"></span>`
    ).join('')}
            </div>
            <button id="tutorialNext" class="nav-arrow">→</button>
        </div>
        <div class="tutorial-content">
            <iframe id="tutorialFrame" src="" class="tutorial-iframe"></iframe>
        </div>
    `;

    tutorialOutput.innerHTML = navigationHTML;

    // Event Listeners hinzufügen
    document.getElementById('tutorialPrev').addEventListener('click', () => {
        if (currentTutorialIndex > 0) {
            currentTutorialIndex--;
            updateTutorialDisplay();
        }
    });

    document.getElementById('tutorialNext').addEventListener('click', () => {
        if (currentTutorialIndex < tutorialContents.length - 1) {
            currentTutorialIndex++;
            updateTutorialDisplay();
        }
    });

    // Dot Navigation
    document.querySelectorAll('.tutorial-dot').forEach(dot => {
        dot.addEventListener('click', (e) => {
            currentTutorialIndex = parseInt(e.target.dataset.index);
            updateTutorialDisplay();
        });
    });

    updateTutorialDisplay();
}

// Tutorial Display aktualisieren
function updateTutorialDisplay() {
    // Dots aktualisieren
    document.querySelectorAll('.tutorial-dot').forEach((dot, index) => {
        dot.classList.toggle('active', index === currentTutorialIndex);
    });

    // Button States
    document.getElementById('tutorialPrev').disabled = currentTutorialIndex === 0;
    document.getElementById('tutorialNext').disabled = currentTutorialIndex === tutorialContents.length - 1;

    // Content aktualisieren - zeige Markdown statt iframe
    const tutorialFrame = document.getElementById('tutorialFrame');
    const currentContent = tutorialContents[currentTutorialIndex].content;

    // Erstelle HTML für Markdown-Content
    const htmlContent = `
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { 
                    font-family: 'Segoe UI', sans-serif; 
                    line-height: 1.6; 
                    margin: 20px; 
                    background: #1e1e1e; 
                    color: #cccccc; 
                }
                h1, h2, h3 { color: #ffffff; }
                h1 { border-bottom: 2px solid #007acc; padding-bottom: 10px; }
                h2 { border-bottom: 1px solid #404040; padding-bottom: 5px; }
                code { 
                    background: #2d2d2d; 
                    padding: 2px 6px; 
                    border-radius: 3px; 
                    font-family: 'Consolas', monospace;
                }
                pre { 
                    background: #2d2d2d; 
                    padding: 15px; 
                    border-radius: 5px; 
                    overflow-x: auto; 
                }
                pre code { background: transparent; padding: 0; }
                ul { margin-left: 20px; }
                li { margin-bottom: 5px; }
                .example { background: #2a4a2a; padding: 10px; border-radius: 5px; margin: 10px 0; }
            </style>
        </head>
        <body>
            ${renderMarkdown(currentContent)}
        </body>
        </html>
    `;

    const blob = new Blob([htmlContent], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    tutorialFrame.src = url;

    // URL nach dem Laden wieder freigeben
    tutorialFrame.onload = () => {
        URL.revokeObjectURL(url);
    };
}

// Task-Tab aktualisieren
function updateTaskTab(markdownText) {
    const taskOutput = document.getElementById('taskOutput');
    const taskTab = document.querySelector('.output-tab[data-output-tab="task"]');

    if (taskOutput) {
        // Überprüfen, ob markdownText nicht leer ist
        if (markdownText && markdownText.trim() !== '') {
            taskOutput.innerHTML = renderMarkdown(markdownText);
            taskTab.style.display = 'block'; // Tab anzeigen
        } else {
            taskOutput.innerHTML = ''; // Inhalte löschen
            taskTab.style.display = 'none'; // Tab ausblenden
        }
    }
}

// TaskView-konforme Funktionen
function getContentFromView() {
    // Sammle alle relevanten Daten (ohne Schriftgröße)
    const content = {
        version: "1.0",
        type: "python-code-editor",
        pythonCode: pythonEditor.getValue(),
        currentTutorialIndex: currentTutorialIndex,
        metadata: {
            lastModified: new Date().toISOString(),
            codeLength: pythonEditor.getValue().length
        }
    };

    return JSON.stringify(content);
}

function loadContentToView(content) {
    try {
        if (!content || content.trim() === '' || content === '{}') {
            console.log('Kein Inhalt zum Laden vorhanden, verwende Standardcode');
            return;
        }

        const data = JSON.parse(content);

        if (data.pythonCode) {
            pythonEditor.setValue(data.pythonCode);
            console.log('Python-Code erfolgreich geladen');
        }

        if (data.currentTutorialIndex !== undefined && data.currentTutorialIndex >= 0 && data.currentTutorialIndex < tutorialContents.length) {
            currentTutorialIndex = data.currentTutorialIndex;
            updateTutorialDisplay();
        }

        updateSaveStatus('saved');

    } catch (error) {
        console.error('Fehler beim Laden des Inhalts:', error);
        updateSaveStatus('error');
    }
}

function saveContent(isSubmission = false) {
    console.log('Speichere Inhalt...', isSubmission ? '(Abgabe)' : '(Normal)');
    updateSaveStatus('saving');

    const content = getContentFromView();
    const urlElement = document.getElementById(isSubmission ? 'task-submit-url' : 'task-save-url');
    const url = urlElement ? document.getElementById('default-link') + urlElement.getAttribute('data-url') : '';

    if (!url) {
        console.error('Keine URL für Speicherung gefunden');
        updateSaveStatus('error');
        return;
    }

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: content })
    })
        .then(response => {
            if (response.ok) {
                updateSaveStatus(isSubmission ? 'submitted' : 'saved');
                console.log('Inhalt erfolgreich gespeichert');

                // Benachrichtigung an Parent-Window für iFrame-Integration
                if (window.parent && window.parent !== window) {
                    window.parent.postMessage('content-saved', '*');
                }
            } else {
                updateSaveStatus('error');
                console.error('Speicherfehler:', response.status);
            }
        })
        .catch(error => {
            console.error('Speicherfehler:', error);
            updateSaveStatus('error');
        });
}

function submitTask() {
    if (confirm('Möchten Sie diese Aufgabe wirklich abgeben? Nach der Abgabe können Sie keine Änderungen mehr vornehmen.')) {
        saveContent(true);
    }
}

function updateSaveStatus(status) {
    const statusElement = document.getElementById('save-status');
    if (!statusElement) {
        console.error('Status-Element nicht gefunden');
        return;
    }

    console.log('Status wird aktualisiert auf:', status);

    // Entferne alle Status-Klassen
    statusElement.className = '';

    switch (status) {
        case 'saved':
            statusElement.className = 'fas fa-circle text-success';
            statusElement.setAttribute('title', 'Änderungen gespeichert');
            statusElement.style.color = '#28a745';
            break;
        case 'saving':
            statusElement.className = 'fas fa-spinner fa-spin text-primary';
            statusElement.setAttribute('title', 'Speichere...');
            statusElement.style.color = '#007bff';
            break;
        case 'error':
            statusElement.className = 'fas fa-circle text-danger';
            statusElement.setAttribute('title', 'Fehler beim Speichern');
            statusElement.style.color = '#dc3545';
            break;
        case 'ready':
            statusElement.className = 'fas fa-circle text-warning';
            statusElement.setAttribute('title', 'Ungespeicherte Änderungen');
            statusElement.style.color = '#ffc107';
            break;
        case 'submitted':
            statusElement.className = 'fas fa-circle text-success';
            statusElement.setAttribute('title', 'Aufgabe abgegeben');
            statusElement.style.color = '#28a745';
            break;
        default:
            statusElement.className = 'fas fa-circle text-muted';
            statusElement.setAttribute('title', 'Bereit zum Speichern');
            statusElement.style.color = '#6c757d';
            break;
    }
}

// Globale Funktionen für spätere Phasen
window.editorAPI = {
    getPythonCode: () => pythonEditor.getValue(),
    setPythonCode: (code) => pythonEditor.setValue(code),
    getCurrentEditor: getCurrentEditor,
    addToConsole: addToConsole,
    runPythonCode: runPythonCode,
    updateTaskTab: updateTaskTab,
    renderMarkdown: renderMarkdown,
    saveContent: saveContent,
    submitTask: submitTask,
    getContentFromView: getContentFromView,
    loadContentToView: loadContentToView
};