// Python Web Worker für Pyodide-Ausführung mit Real-Time Output
let pyodide = null;
let isReady = false;
let isExecuting = false;
let executionId = null;

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
        // Text direkt senden, um Zeilenumbrüche zu erhalten
        if (text) {
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

# input()-Funktion für Browser-Eingaben über Message-Passing
def browser_input(prompt=""):
    # Für jetzt einfach leeren String zurückgeben da js.prompt in Worker nicht verfügbar
    # Echte Implementation würde Message-Passing benötigen
    return ""

builtins.input = browser_input
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

    self.postMessage({
        type: 'status',
        data: 'executing',
        message: 'Python-Code wird ausgeführt...'
    });

    try {
        // Code direkt ausführen (Worker-Terminierung ist die echte Unterbrechung)
        pyodide.runPython(code);

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
        
        if (isExecuting && executionId === id) {
            self.postMessage({
                type: 'error',
                data: 'execution_error',
                message: 'Python-Fehler: ' + error.message
            });
        }
    } finally {
        if (executionId === id) {
            isExecuting = false;
            executionId = null;
        }
    }
}

// Ausführung stoppen
function stopExecution() {
    if (isExecuting) {
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