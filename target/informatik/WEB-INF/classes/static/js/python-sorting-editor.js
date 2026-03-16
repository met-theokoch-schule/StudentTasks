// Globale Variablen
let pythonEditor;
let pyodide = null;
let pyodideReady = false;
let isResizing = false;
let typeCheckInterval = null;
let lastPythonCode = '';
let mypyReady = false;

// Globale Variablen für Array-Visualisierung
let sortarray = null;
let pythonExecutionController = null; // Controller für Python-Ausführung
let isPythonRunning = false; // Flag ob Python gerade läuft
let interruptFlag = false; // Flag für das Stoppen des Programms

// Tracking für letzte Array-Operationen (für persistente Highlights)
let lastReadIndices = []; // Array der letzten 2 gelesenen Indizes
let lastWriteIndices = []; // Array der letzten 2 geschriebenen Indizes

let currentConfigIndex = 0; // Für die Auswahl der Weltkonfiguration
let animationSpeed = 0; // Geschwindigkeit der Animation in ms

// Konfigurationen für die Array-Visualisierung (ehemals Hamster-Welten)
const worldConfigurations = [];

// Tracking-Funktionen für Array-Operationen
function trackReadOperation(index) {
    lastReadIndices.push(index);
    if (lastReadIndices.length > 2) {
        lastReadIndices.shift(); // Entferne ältesten Eintrag
    }
}

function trackWriteOperation(index) {
    lastWriteIndices.push(index);
    if (lastWriteIndices.length > 2) {
        lastWriteIndices.shift(); // Entferne ältesten Eintrag
    }
}

function clearOperationTracking() {
    lastReadIndices = [];
    lastWriteIndices = [];
}

// Initialisierung beim Laden der Seite
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔍 DEBUG: ===== DOMContentLoaded Event Start =====');

    console.log('🔍 DEBUG: Initialisiere Editoren...');
    initializeEditors();

    console.log('🔍 DEBUG: Initialisiere Tabs...');
    initializeTabs();
    initializeOutputTabs();

    console.log('🔍 DEBUG: Initialisiere Resizer...');
    initializeResizer();

    console.log('🔍 DEBUG: Initialisiere Controls...');
    initializeControls();

    console.log('🔍 DEBUG: Bestimme Content-Quelle...');
    const contentElement = document.getElementById('currentContent');
    const defaultSubmissionElement = document.getElementById('defaultSubmission');

    let contentSource = 'none';
    let contentLoaded = false;

    if (contentElement && contentElement.textContent.trim()) {
        contentSource = 'currentContent';
    } else if (defaultSubmissionElement && defaultSubmissionElement.textContent.trim()) {
        contentSource = 'defaultSubmission';
    }

    console.log('🔍 DEBUG: Content-Quelle bestimmt:', contentSource);

    // Einmalige Initialisierung basierend auf Content-Quelle
    if (contentSource === 'currentContent') {
        console.log('🔍 DEBUG: Lade currentContent...');
        loadContentToView(contentElement.textContent.trim());
        contentLoaded = true;
    } else if (contentSource === 'defaultSubmission') {
        console.log('🔍 DEBUG: Lade defaultSubmission...');
        pythonEditor.setValue(defaultSubmissionElement.textContent.trim());
        updateSaveStatus('saved');
        // Standard Array-Config laden da kein JSON-Content
        initializeArrayConfig();
        contentLoaded = true;
    } else {
        console.log('🔍 DEBUG: Kein Content, lade Standard-Config...');
        initializeArrayConfig();
        contentLoaded = true;
    }

    console.log('🔍 DEBUG: Content geladen:', contentLoaded);

    console.log('🔍 DEBUG: Initialisiere Pyodide...');
    initializePyodide();

    console.log('🔍 DEBUG: Initialisiere Task Content...');
    initializeTaskContent();

    console.log('🔍 DEBUG: Initialisiere Tutorial Navigation...');
    initializeTutorialNavigation();

    // Cursor an den Anfang setzen (Content wurde bereits früher geladen)
    pythonEditor.gotoLine(1);

    // Initialer Status
    updateSaveStatus('saved');

    // Änderungen verfolgen für Status-Updates
    pythonEditor.on('change', function() {
        updateSaveStatus('ready');
    });

    console.log('🔍 DEBUG: ===== DOMContentLoaded Event Ende =====');
    console.log('🔍 DEBUG: Finaler sortarray Status:', sortarray ? `existiert (Größe: ${sortarray.size})` : 'nicht vorhanden');
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
        setEditorBottomPadding(10);

        console.log("Editor erfolgreich initialisiert");
    } catch (error) {
        console.error("Fehler bei der Editor-Initialisierung:", error);
    }
}

function setEditorBottomPadding(lines) {
    if (!pythonEditor || !pythonEditor.renderer) {
        return;
    }

    const lineHeight = pythonEditor.renderer.lineHeight || 0;
    const bottomPadding = Math.max(0, Math.round(lineHeight * lines));
    pythonEditor.renderer.setScrollMargin(0, bottomPadding, 0, 0);
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
            // Array-Visualisierung bei Splitter-Bewegung neu berechnen
            if (sortarray) {
                updateArrayDisplay();
            }
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
            // Array-Visualisierung bei Größenänderung neu berechnen
            if (sortarray) {
                updateArrayDisplay();
            }
        }, 50);
    });

    // ResizeObserver für bessere Responsive-Unterstützung
    if (window.ResizeObserver) {
        const resizeObserver = new ResizeObserver(() => {
            if (sortarray) {
                updateArrayDisplay();
            }
        });

        // Beobachte das Output-Panel für Größenänderungen
        setTimeout(() => {
            const outputPanel = document.querySelector('.output-panel');
            if (outputPanel) {
                resizeObserver.observe(outputPanel);
            }
        }, 100);
    }
}

// Steuerungselemente initialisieren
function initializeControls() {
    // Schriftgröße-Dropdown
    const fontSizeSelect = document.getElementById('fontSizeSelect');
    fontSizeSelect.addEventListener('change', function() {
        const fontSize = parseInt(this.value);

        // Editor Schriftgröße anpassen
        pythonEditor.setFontSize(fontSize);
        setEditorBottomPadding(10);

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
        setEditorBottomPadding(10);

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

    // Ausgabe löschen Button
    const clearOutputBtn = document.getElementById('clearOutputBtn');
    clearOutputBtn.addEventListener('click', function() {
        clearConsoleOutput();
    });

    // Ausführen/Stop/Zurücksetzen-Button
    document.getElementById('runBtn').addEventListener('click', function() {
        if (pyodideReady) {
            // Logik für Array-Sortierung anstelle von Hamster-Steuerung
            if (this.textContent.includes('Zurücksetzen')) {
                resetArraySimulation();
            } else if (this.textContent.includes('Stop')) {
                stopArrayProgram();
            } else {
                runPythonCode();
            }
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

    // Array-Größen-Selektor initialisieren
    const configSelector = document.getElementById('configSelector');
    if (configSelector) {
        // Initial den Button mit der aktuellen Array-Größe aktualisieren
        updateConfigSelector();

        configSelector.addEventListener('click', function() {
            // Zyklisch durch Array-Größen wechseln: 10 → 20 → 100 → 200 → 10
            if (sortarray) {
                const sizes = [10, 20, 100, 200];
                const currentIndex = sizes.indexOf(sortarray.size);
                const nextIndex = (currentIndex + 1) % sizes.length;
                changeArraySize(sizes[nextIndex]);
            }
        });
    }

    // Geschwindigkeitsselektor initialisieren (bleibt für Animation/Schritt-Timing)
    const speedSelector = document.getElementById('speedSelector');
    if (speedSelector) {
        speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
        speedSelector.onclick = () => {
            // Zyklisch durch verschiedene Geschwindigkeiten schalten
            const speeds = [0, 10, 20, 50, 100]; // Von schnell zu langsam
            const currentIndex = speeds.indexOf(animationSpeed);
            animationSpeed = speeds[(currentIndex + 1) % speeds.length];
            speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
            updateHamsterTransition(); // Transition aktualisieren (wird jetzt für Array-Visualisierung verwendet)
        };
    }
}

// Utility-Funktionen
function getCurrentEditor() {
    return pythonEditor;
}

function addToConsole(text, type = 'info') {
    const consoleOutput = document.getElementById('consoleOutput');
    const timestamp = new Date().toLocaleTimeString();
    const prefix = type === 'error' ? '❌' : type === 'warning' ? '⚠️' : 'ℹ️';

    consoleOutput.textContent += `[${timestamp}] ${prefix} ${text}\n`;
    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

function addToConsoleWithoutTimestamp(text) {
    const consoleOutput = document.getElementById('consoleOutput');
    consoleOutput.textContent += `${text}\n`;
    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

function clearConsoleOutput() {
    const consoleOutput = document.getElementById('consoleOutput');
    consoleOutput.textContent = '';
}

// Pyodide initialisieren
async function initializePyodide() {
    addToConsole('Array-Visualisierung initialisiert.', 'info');

    addToConsole('Pyodide wird geladen...', 'info');
    try {
        console.log('Lade Pyodide...');
        pyodide = await loadPyodide({
            stdout: msg => addToConsole(msg, 'info'),
            stderr: msg => addToConsole(msg, 'error')
        });

        pyodideReady = true;
        addToConsole('Pyodide erfolgreich geladen ✓', 'info');

        console.log('🔍 DEBUG: Pyodide geladen, sortarray Status vor setupArrayCommands:', sortarray ? `existiert (Größe: ${sortarray.size}, Daten: [${sortarray.data.slice(0, 5).join(', ')}...])` : 'nicht vorhanden');

        // Array-spezifische Funktionen registrieren (ohne Array-Neuerstellung)
        await setupArrayCommands();

        console.log('🔍 DEBUG: Nach setupArrayCommands, sortarray Status:', sortarray ? `existiert (Größe: ${sortarray.size}, Daten: [${sortarray.data.slice(0, 5).join(', ')}...])` : 'nicht vorhanden');

        document.getElementById('runBtn').disabled = false;
        document.getElementById('runBtn').style.opacity = '1';

        // MyPy initialisieren
        await initializeMyPy();

    } catch (error) {
        addToConsole('Fehler beim Laden von Pyodide: ' + error.message, 'error');
        console.error('Pyodide Fehler:', error);
    }
}

// Array für Python-Kontext initialisieren
async function initializeArrayForPython() {
    if (!sortarray) {
        console.error('sortarray ist nicht initialisiert');
        return;
    }

    // sortarray am window-Objekt verfügbar machen für Python-Zugriff
    window.sortarray = sortarray;

    // Synchronisationsfunktion für Python-zu-JS Updates
    window.updateArrayFromPython = function(index, value) {
        if (sortarray && index >= 0 && index < sortarray.size) {
            const oldValue = sortarray.data[index];
            sortarray.data[index] = value;
            sortarray.writeCount++;

            addToConsoleWithoutTimestamp(`✏️  Array[${index}]: ${oldValue} → ${value} (Schreibvorgang #${sortarray.writeCount})`);
            updateArrayDisplay();
        }
    };

    console.log('🔧 Array für Python-Kontext initialisiert');
}

// Array-spezifische Funktionen für Pyodide registrieren
async function setupArrayCommands() {
    console.log("🔍 DEBUG: setupArrayCommands() gestartet");
    console.log('🔍 DEBUG: sortarray Status bei setupArrayCommands Beginn:', sortarray ? `existiert (Größe: ${sortarray.size}, Daten: [${sortarray.data.slice(0, 5).join(', ')}...])` : 'nicht vorhanden');
    console.log("Richte Array-spezifische Funktionen ein...");

    // JavaScript-Funktionen für Array-Zugriff am window-Objekt verfügbar machen
    // Optimierte Array-Funktionen für 0ms Geschwindigkeit (nur Zählung)
    window.arraySetItemFast = function(index, value) {
        if (!sortarray) return;
        const oldValue = sortarray.data[index];
        sortarray.data[index] = value;
        sortarray.writeCount++;

        // Tracking für Schreibvorgänge
        trackWriteOperation(index);

        // Debug-Logging alle 50 Operationen
        if (sortarray.writeCount % 50 === 0) {
            console.log(`Fast Write #${sortarray.writeCount}: array[${index}] = ${value}`);
        }
    };

    window.arrayGetItemFast = function(index) {
        if (!sortarray) return 0;
        sortarray.readCount++;

        // Tracking für Leseoperationen
        trackReadOperation(index);

        const value = sortarray.data[index];
        // Debug-Logging alle 100 Operationen
        if (sortarray.readCount % 100 === 0) {
            console.log(`Fast Read #${sortarray.readCount}: array[${index}] = ${value}`);
        }
        return value;
    };

    window.arraySwapFast = function(index1, index2) {
        if (!sortarray) return;

        // Erst lesen (2 Lesevorgänge) - genau wie im normalen Modus
        const value1 = sortarray.data[index1];
        const value2 = sortarray.data[index2];
        sortarray.readCount += 2;   // 2 Lesevorgänge zählen

        // Tracking für Leseoperationen
        trackReadOperation(index1);
        trackReadOperation(index2);

        // Dann schreiben (2 Schreibvorgänge)
        sortarray.data[index1] = value2;
        sortarray.data[index2] = value1;
        sortarray.writeCount += 2;  // 2 Schreibvorgänge zählen

        // Tracking für Schreibvorgänge
        trackWriteOperation(index1);
        trackWriteOperation(index2);

        // Debug-Logging
        if (sortarray.writeCount % 50 === 0) {
            console.log(`Fast Swap: array[${index1}] ↔ array[${index2}] (Write #${sortarray.writeCount}, Read #${sortarray.readCount})`);
        }
    };

    // Standard Array-Funktionen für alle anderen Geschwindigkeiten (mit visuellen Updates)
    window.arraySetItem = async function(index, value) {
        // Prüfe ob Programm gestoppt werden soll
        if (interruptFlag) {
            throw new Error("KeyboardInterrupt: Programm wurde gestoppt");
        }

        // Prüfe Python-seitiges Interrupt-Flag sicher
        try {
            const pythonInterrupt = pyodide.runPython(`
try:
    interrupt_requested
except NameError:
    interrupt_requested = False
interrupt_requested
            `);
            if (pythonInterrupt) {
                throw new Error("KeyboardInterrupt: Programm wurde gestoppt");
            }
        } catch (e) {
            if (e.message && e.message.includes("KeyboardInterrupt")) {
                throw e;
            }
            // Andere Fehler ignorieren (z.B. NameError für interruptFlag)
        }

        if (!sortarray) {
            throw new Error("Array ist nicht initialisiert.");
        }
        if (index < 0 || index >= sortarray.size) {
            throw new Error(`Array-Index ${index} außerhalb der Grenzen (0-${sortarray.size-1}).`);
        }

        const oldValue = sortarray.data[index];
        sortarray.data[index] = value;
        sortarray.writeCount++;

        // Tracking für Schreibvorgänge
        trackWriteOperation(index);

        // Visuelles Update mit persistenter Farbe
        updateArrayDisplay(); // Visuelle Aktualisierung mit neuen Farben

        // Warte für Animation/Geschwindigkeit und prüfe erneut auf Interrupt
        await new Promise((resolve, reject) => {
            setTimeout(() => {
                if (interruptFlag) {
                    reject(new Error("KeyboardInterrupt: Programm wurde gestoppt"));
                } else {
                    resolve();
                }
            }, animationSpeed);
        });
    };

    window.arrayGetItem = async function(index) {
        // Prüfe ob Programm gestoppt werden soll
        if (interruptFlag) {
            throw new Error("KeyboardInterrupt: Programm wurde gestoppt");
        }

        // Prüfe Python-seitiges Interrupt-Flag sicher
        try {
            const pythonInterrupt = pyodide.runPython(`
try:
    interrupt_requested
except NameError:
    interrupt_requested = False
interrupt_requested
            `);
            if (pythonInterrupt) {
                throw new Error("KeyboardInterrupt: Programm wurde gestoppt");
            }
        } catch (e) {
            if (e.message && e.message.includes("KeyboardInterrupt")) {
                throw e;
            }
            // Andere Fehler ignorieren (z.B. NameError für interruptFlag)
        }

        if (!sortarray) {
            throw new Error("Array ist nicht initialisiert.");
        }
        if (index < 0 || index >= sortarray.size) {
            throw new Error(`Array-Index ${index} außerhalb der Grenzen (0-${sortarray.size-1}).`);
        }

        sortarray.readCount++;
        // Tracking für Leseoperationen
        trackReadOperation(index);

        const value = sortarray.data[index];

        // Visuelles Update mit persistenter Farbe
        updateArrayDisplay(); // Visuelle Aktualisierung mit neuen Farben

        // Warte für Animation/Geschwindigkeit auch nach Leseoperationen und prüfe erneut auf Interrupt
        await new Promise((resolve, reject) => {
            setTimeout(() => {
                if (interruptFlag) {
                    reject(new Error("KeyboardInterrupt: Programm wurde gestoppt"));
                } else {
                    resolve();
                }
            }, animationSpeed);
        });

        return value;
    };

    window.arrayLen = async function() {
        return sortarray ? sortarray.size : 0;
    };

    window.arraySwap = async function(index1, index2) {
        // Prüfe ob Programm gestoppt werden soll
        if (interruptFlag) {
            throw new Error("KeyboardInterrupt: Programm wurde gestoppt");
        }

        // Prüfe Python-seitiges Interrupt-Flag sicher
        try {
            const pythonInterrupt = pyodide.runPython(`
try:
    interrupt_requested
except NameError:
    interrupt_requested = False
interrupt_requested
            `);
            if (pythonInterrupt) {
                throw new Error("KeyboardInterrupt: Programm wurde gestoppt");
            }
        } catch (e) {
            if (e.message && e.message.includes("KeyboardInterrupt")) {
                throw e;
            }
            // Andere Fehler ignorieren (z.B. NameError für interruptFlag)
        }

        if (!sortarray) {
            throw new Error("Array ist nicht initialisiert.");
        }
        if (index1 < 0 || index1 >= sortarray.size || index2 < 0 || index2 >= sortarray.size) {
            throw new Error(`Array-Index außerhalb der Grenzen. Index1: ${index1}, Index2: ${index2}, Array-Größe: ${sortarray.size}`);
        }

        // Erst lesen (2 Lesevorgänge)
        const value1 = sortarray.data[index1];
        const value2 = sortarray.data[index2];
        sortarray.readCount += 2;   // 2 Lesevorgänge

        // Tracking für Leseoperationen
        trackReadOperation(index1);
        trackReadOperation(index2);

        // Dann schreiben (2 Schreibvorgänge)
        sortarray.data[index1] = value2;
        sortarray.data[index2] = value1;
        sortarray.writeCount += 2;  // 2 Schreibvorgänge

        // Tracking für Schreibvorgänge (überschreibt die Leseoperationen mit roter Farbe)
        trackWriteOperation(index1);
        trackWriteOperation(index2);

        // Visuelles Update mit persistenter Farbe (rot hat Priorität)
        updateArrayDisplay(); // Visuelle Aktualisierung mit neuen Farben

        // Warte für Animation/Geschwindigkeit und prüfe erneut auf Interrupt
        await new Promise((resolve, reject) => {
            setTimeout(() => {
                if (interruptFlag) {
                    reject(new Error("KeyboardInterrupt: Programm wurde gestoppt"));
                } else {
                    resolve();
                }
            }, animationSpeed);
        });
    };

    console.log('🔍 DEBUG: Vor initializeArrayVisualization(), sortarray Status:', sortarray ? `existiert (Größe: ${sortarray.size}, Daten: [${sortarray.data.slice(0, 5).join(', ')}...])` : 'nicht vorhanden');

    // Array-Visualisierung initialisieren NUR wenn sortarray noch nicht existiert
    if (!sortarray) {
        console.log('🔍 DEBUG: sortarray existiert nicht, erstelle Standard-Array');
        initializeArrayVisualization();
    } else {
        console.log('🔍 DEBUG: sortarray bereits vorhanden, überspringe Neuinitialisierung');
        // Nur UI aktualisieren ohne Array-Daten zu überschreiben
        updateArrayDisplay();
        updateArrayStats();
        updateConfigSelector();
    }

    console.log('🔍 DEBUG: Nach Visualisierung-Check, sortarray Status:', sortarray ? `existiert (Größe: ${sortarray.size}, Daten: [${sortarray.data.slice(0, 5).join(', ')}...])` : 'nicht vorhanden');

    // Python-Code mit integrierten JavaScript-Aufrufen für Array-Zugriff
    const pythonCode = `
import js
import asyncio

# Array-spezifische Funktionen, die von Python aufgerufen werden können
# Diese werden basierend auf der animationSpeed dynamisch ausgewählt

# Hilfsfunktionen für häufige Sortieralgorithmus-Patterns
def print_array():
    """Zeigt das aktuelle Array in der Konsole an"""
    if hasattr(js, 'sortarray') and js.sortarray:
        data = list(js.sortarray.data.to_py())
        print(f"Array: {data}")

def get_array_copy():
    """Gibt eine Kopie des aktuellen Arrays zurück"""
    if hasattr(js, 'sortarray') and js.sortarray:
        return list(js.sortarray.data.to_py())
    return []

# Dummy-Initialisierung für sortarray, damit Python es erkennt
sortarray = None
`;

    try {
        await pyodide.runPythonAsync(pythonCode);
        console.log("Array-spezifische Funktionen erfolgreich registriert und JavaScript-Funktionen verknüpft");
    } catch (e) {
        console.error("Fehler beim Registrieren der Array-spezifischen Funktionen:", e);
        addToConsole("Fehler beim Registrieren der Array-spezifischen Funktionen.", "error");
    }
}

// Programm stoppen (für Array-Sortierung)
function stopArrayProgram() {
    if (isPythonRunning) { // Nur stoppen wenn Programm läuft
        interruptFlag = true; // Setze das Interrupt-Flag
        addToConsole('⏹ Programm wird gestoppt', 'warning');

        // Pyodide direkt unterbrechen
        if (pyodide && pyodide.runPython) {
            try {
                // Setze globales Interrupt-Flag in Python
                pyodide.runPython(`
import sys
# Setze globales Interrupt-Flag
interrupt_requested = True
# Erzwinge KeyboardInterrupt
raise KeyboardInterrupt("Programm wurde gestoppt")
                `);
            } catch (e) {
                console.log('Python-Interrupt ausgelöst');
            }
        }

        // Flags zurücksetzen
        isPythonRunning = false;

        // Button auf "Zurücksetzen" setzen
        updateRunButton('reset');

        // Statistiken anzeigen
        if (sortarray) {
            addToConsole(`📊 Statistiken beim Stopp: Lesevorgänge: ${sortarray.readCount}, Schreibvorgänge: ${sortarray.writeCount}`, 'info');
        }
    } else {
        addToConsole('⚠️ Kein laufendes Programm zum Stoppen gefunden', 'warning');
        updateRunButton('reset');
    }
}

// Array-Simulation zurücksetzen
function resetArraySimulation() {
    clearConsoleOutput();
    clearOperationTracking(); // Tracking zurücksetzen
    addToConsole('🔄 Setze Array-Simulation zurück...', 'info');

    // Array mit aktueller Größe neu generieren, anstatt Standard-Konfiguration
    if (sortarray) {
        const currentSize = sortarray.size;
        const currentMaxValue = sortarray.maxValue;

        // Array-Daten mit aktueller Größe neu generieren
        sortarray.data = generateRandomArray(currentSize);
        sortarray.writeCount = 0;
        sortarray.readCount = 0;

        addToConsole(`🎲 Neues Array 'sortarray' mit ${currentSize} Elementen generiert`, 'info');

        // Visualisierung aktualisieren
        updateArrayDisplay();
        updateArrayStats();
    } else {
        // Fallback: Initialisiere mit Standard-Array falls sortarray nicht existiert
        initializeArrayVisualization();
    }

    // Button auf "Ausführen" setzen
    updateRunButton('execute');
}

// Code-Transformation für Array-Zugriffe
function transformArrayCode(code) {
    let transformedCode = code;
    let hasTransformations = false;

    // Prüfe ob 0ms Geschwindigkeit (nutze Fast-Funktionen ohne await)
    const useFastFunctions = (animationSpeed === 0);

    // Counter für Zeilennummern bei mehrzeiligen Transformationen
    let lines = transformedCode.split('\n');

    for (let i = 0; i < lines.length; i++) {
        let line = lines[i];
        let originalLine = line;

        // 1. Tuple Unpacking für Array-Swaps erkennen: a, b = b, a
        // Erweiterte Regex für verschiedene Swap-Patterns
        const tupleSwapRegex = /^(\s*)([a-zA-Z_][a-zA-Z0-9_]*)\s*,\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*=\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*,\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*$/;
        const tupleMatch = line.match(tupleSwapRegex);

        if (tupleMatch) {
            const [, indent, var1, var2, val1, val2] = tupleMatch;
            // Prüfe ob es ein direkter Swap ist (a, b = b, a)
            if (var1 === val2 && var2 === val1) {
                // Ersetze durch Swap-Kommentar und direkte Array-Swaps falls verfügbar
                line = `${indent}# Swap: ${var1}, ${var2} = ${val1}, ${val2}`;
                line += `\n${indent}temp_swap_val = ${var1}`;
                line += `\n${indent}${var1} = ${var2}`;
                line += `\n${indent}${var2} = temp_swap_val`;
                hasTransformations = true;
                console.log(`Tuple-Swap erkannt: ${var1} ↔ ${var2}`);
            }
        }

        // 2. Direkte Array-Swaps: sortarray[i], sortarray[j] = sortarray[j], sortarray[i]
        const arraySwapRegex = /^(\s*)sortarray\s*\[\s*([^,\]]+)\s*\]\s*,\s*sortarray\s*\[\s*([^,\]]+)\s*\]\s*=\s*sortarray\s*\[\s*([^,\]]+)\s*\]\s*,\s*sortarray\s*\[\s*([^,\]]+)\s*\]\s*$/;
        const arraySwapMatch = line.match(arraySwapRegex);

        if (arraySwapMatch) {
            const [, indent, idx1, idx2, val_idx1, val_idx2] = arraySwapMatch;
            // Prüfe ob es ein direkter Array-Swap ist
            if (idx1.trim() === val_idx2.trim() && idx2.trim() === val_idx1.trim()) {
                if (useFastFunctions) {
                    line = `${indent}swap_elements(${idx1.trim()}, ${idx2.trim()})`;
                } else {
                    line = `${indent}await swap_elements(${idx1.trim()}, ${idx2.trim()})`;
                }
                hasTransformations = true;
                console.log(`Array-Swap erkannt: sortarray[${idx1.trim()}] ↔ sortarray[${idx2.trim()}]`);
            }
        }

        // 3. Array-Zuweisungen: sortarray[index] = value
        const arraySetRegex = /^(\s*)sortarray\s*\[\s*([^\]]+)\s*\]\s*=\s*(.+)$/;
        const setMatch = line.match(arraySetRegex);

        if (setMatch && !arraySwapMatch) { // Nicht wenn es bereits ein Swap ist
            const [, indent, index, value] = setMatch;
            // Transformiere auch Array-Zugriffe im Wert-Ausdruck
            let transformedValue = value.trim();
            if (useFastFunctions) {
                transformedValue = transformedValue.replace(/sortarray\s*\[\s*([^\]]+)\s*\]/g, 'get_array_item($1)');
                line = `${indent}set_array_item(${index.trim()}, ${transformedValue})`;
            } else {
                transformedValue = transformedValue.replace(/sortarray\s*\[\s*([^\]]+)\s*\]/g, 'await get_array_item($1)');
                line = `${indent}await set_array_item(${index.trim()}, ${transformedValue})`;
            }
            hasTransformations = true;
            console.log(`Array-Zuweisung erkannt: sortarray[${index.trim()}] = ${value.trim()}`);
        }

        // 4. Array-Lesezugriffe: value = sortarray[index]
        // Aber nur bei direkten Zuweisungen, nicht in komplexeren Ausdrücken
        const arrayGetRegex = /^(\s*)([a-zA-Z_][a-zA-Z0-9_]*)\s*=\s*sortarray\s*\[\s*([^\]]+)\s*\]\s*$/;
        const getMatch = line.match(arrayGetRegex);

        if (getMatch) {
            const [, indent, variable, index] = getMatch;
            if (useFastFunctions) {
                line = `${indent}${variable} = get_array_item(${index.trim()})`;
            } else {
                line = `${indent}${variable} = await get_array_item(${index.trim()})`;
            }
            hasTransformations = true;
            console.log(`Array-Lesezugriff erkannt: ${variable} = sortarray[${index.trim()}]`);
        }

        // 5. Array-Lesezugriffe in Ausdrücken (komplexer): sortarray[index] in anderen Kontexten
        // Ersetze sortarray[index] durch get_array_item(index) in Ausdrücken
        const expressionArrayGetRegex = /sortarray\s*\[\s*([^\]]+)\s*\]/g;
        if (!setMatch && !getMatch && !arraySwapMatch && !tupleMatch) {
            // Nur transformieren wenn es nicht bereits eine der obigen Transformationen war
            const matches = line.match(expressionArrayGetRegex);
            if (matches) {
                // Zähle die Anzahl der Array-Zugriffe für besseres Logging
                const accessCount = matches.length;

                // Ersetze alle Array-Zugriffe in der Zeile
                if (useFastFunctions) {
                    line = line.replace(expressionArrayGetRegex, 'get_array_item($1)');
                } else {
                    line = line.replace(expressionArrayGetRegex, 'await get_array_item($1)');
                }
                hasTransformations = true;
                console.log(`${accessCount} Array-Lesezugriffe in Ausdruck transformiert`);
            }
        }

        lines[i] = line;
    }

    transformedCode = lines.join('\n');

    // Zusätzliche Transformationen für Array-Funktionen

    // len(sortarray) → array_length() oder await array_length()
    if (transformedCode.includes('len(sortarray)')) {
        if (useFastFunctions) {
            transformedCode = transformedCode.replace(/len\s*\(\s*sortarray\s*\)/g, 'array_length()');
        } else {
            transformedCode = transformedCode.replace(/len\s*\(\s*sortarray\s*\)/g, 'await array_length()');
        }
        hasTransformations = true;
        console.log('len(sortarray) zu Funktionsaufruf transformiert');
    }

    // range(len(sortarray)) → range(array_length()) oder range(await array_length())
    // range() kann nicht mit await arbeiten, also müssen wir es vorab berechnen
    if (useFastFunctions) {
        const rangeSyncRegex = /range\s*\(\s*array_length\(\s*\)\s*\)/g;
        if (transformedCode.match(rangeSyncRegex)) {
            transformedCode = transformedCode.replace(rangeSyncRegex, 'range(sortarray_len)');
            // Füge sortarray_len Berechnung am Anfang hinzu wenn noch nicht vorhanden
            if (!transformedCode.includes('sortarray_len = array_length()')) {
                transformedCode = 'sortarray_len = array_length()\n' + transformedCode;
            }
            hasTransformations = true;
            console.log('range(len(sortarray)) für Schleifen optimiert (sync)');
        }
    } else {
        const rangeAsyncRegex = /range\s*\(\s*await array_length\(\s*\)\s*\)/g;
        if (transformedCode.match(rangeAsyncRegex)) {
            transformedCode = transformedCode.replace(rangeAsyncRegex, 'range(sortarray_len)');
            // Füge sortarray_len Berechnung am Anfang hinzu wenn noch nicht vorhanden
            if (!transformedCode.includes('sortarray_len = await array_length()')) {
                transformedCode = 'sortarray_len = await array_length()\n' + transformedCode;
            }
            hasTransformations = true;
            console.log('range(len(sortarray)) für Schleifen optimiert (async)');
        }
    }

    // Zusätzliche Behandlung für verschachtelte range() Aufrufe
    if (useFastFunctions) {
        const nestedRangeSyncRegex = /range\s*\(\s*(\d+)\s*,\s*array_length\(\s*\)\s*([+-]\s*\d+\s*[+-]\s*\d+|[+-]\s*\w+\s*[+-]\s*\d+)?\s*\)/g;
        if (transformedCode.match(nestedRangeSyncRegex)) {
            transformedCode = transformedCode.replace(nestedRangeSyncRegex, (match, start, modifier) => {
                if (modifier) {
                    return `range(${start}, sortarray_len${modifier})`;
                } else {
                    return `range(${start}, sortarray_len)`;
                }
            });
            // Füge sortarray_len Berechnung am Anfang hinzu wenn noch nicht vorhanden
            if (!transformedCode.includes('sortarray_len = array_length()')) {
                transformedCode = 'sortarray_len = array_length()\n' + transformedCode;
            }
            hasTransformations = true;
            console.log('Verschachtelte range() Aufrufe für Schleifen optimiert (sync)');
        }
    } else {
        const nestedRangeAsyncRegex = /range\s*\(\s*(\d+)\s*,\s*await array_length\(\s*\)\s*([+-]\s*\d+\s*[+-]\s*\d+|[+-]\s*\w+\s*[+-]\s*\d+)?\s*\)/g;
        if (transformedCode.match(nestedRangeAsyncRegex)) {
            transformedCode = transformedCode.replace(nestedRangeAsyncRegex, (match, start, modifier) => {
                if (modifier) {
                    return `range(${start}, sortarray_len${modifier})`;
                } else {
                    return `range(${start}, sortarray_len)`;
                }
            });
            // Füge sortarray_len Berechnung am Anfang hinzu wenn noch nicht vorhanden
            if (!transformedCode.includes('sortarray_len = await array_length()')) {
                transformedCode = 'sortarray_len = await array_length()\n' + transformedCode;
            }
            hasTransformations = true;
            console.log('Verschachtelte range() Aufrufe für Schleifen optimiert (async)');
        }
    }

    if (hasTransformations) {
        console.log(`Code-Transformation abgeschlossen (${useFastFunctions ? 'Hochgeschwindigkeit' : 'Animation'} Modus)`);
    }

    return transformedCode;
}

// Python-Code ausführen
async function runPythonCode() {
    if (!pyodideReady) {
        addToConsole('Python wird noch geladen...', 'warning');
        return;
    }

    const originalCode = pythonEditor.getValue();
    if (!originalCode.trim()) {
        addToConsole('Kein Code zum Ausführen.', 'warning');
        return;
    }

    // Stoppe vorherige Ausführung falls vorhanden
    if (isPythonRunning) {
        stopArrayProgram(); // Stoppe die vorherige Ausführung zuerst
    }

    // Neuen AbortController für diese Ausführung erstellen
    // pythonExecutionController = new AbortController(); // Nicht mehr direkt verwendet
    interruptFlag = false; // Setze das Interrupt-Flag zurück
    isPythonRunning = true;

    try {
        // Console Output löschen
        clearConsoleOutput();
        clearOperationTracking(); // Operationen Tracking zurücksetzen

        // Array-Counters zurücksetzen
        if (sortarray) {
            sortarray.writeCount = 0;
            sortarray.readCount = 0;
            updateArrayDisplay();
        }

        // Button zu "Stop" ändern (falls nötig, sonst "Zurücksetzen" nach Ausführung)
        updateRunButton('running');

        addToConsole('▶️ Starte Array-Sortierung...', 'info');

        // Array-Initialisierung vor Code-Ausführung
        await initializeArrayForPython();

        console.log('🔄 Transformiere Python-Code...');

        // Code-Transformation durchführen
        const transformedCode = transformArrayCode(originalCode);

        // Debug: Zeige transformierten Code (optional, für Entwicklung)
        if (transformedCode !== originalCode) {
            console.log('✅ Code erfolgreich transformiert');
            console.log('Originaler Code:', originalCode);
            console.log('Transformierter Code:', transformedCode);
        }

        // Setup-Code für sortarray in Python-Kontext
        const setupCode = `
import asyncio
import js

# Globale sortarray Variable für Python-Code verfügbar machen
sortarray = js.sortarray

# Globales Interrupt-Flag
interrupt_requested = False

# Geschwindigkeitsabhängige Array-Funktionen auswählen
current_speed = ${animationSpeed}

if current_speed == 0:
    # Optimierte Funktionen für 0ms (nur Zählung, keine Animationen)
    def set_array_item(index, value):
        js.arraySetItemFast(index, value)

    def get_array_item(index):
        return js.arrayGetItemFast(index)

    def swap_elements(index1, index2):
        js.arraySwapFast(index1, index2)

    def array_length():
        return js.sortarray.size

    print(f"🚀 Hochgeschwindigkeitsmodus aktiviert (0ms) - nur Statistiken, keine Visualisierung")
else:
    # Standard async Funktionen für alle anderen Geschwindigkeiten
    async def set_array_item(index, value):
        await js.arraySetItem(index, value)

    async def get_array_item(index):
        return await js.arrayGetItem(index)

    async def swap_elements(index1, index2):
        await js.arraySwap(index1, index2)

    async def array_length():
        return await js.arrayLen()

    print(f"🎬 Animationsmodus aktiviert ({current_speed}ms)")
        `;

        // Setup-Code ausführen
        await pyodide.runPythonAsync(setupCode);

        // Transformierten Python-Code in async-Kontext ausführen
        // Prüfe ob der Code nach Transformation nicht leer ist
        const codeLines = transformedCode.split('\n').filter(line => line.trim() && !line.trim().startsWith('#'));
        const hasExecutableCode = codeLines.length > 0;

        const asyncWrapper = hasExecutableCode ? `
async def run_user_code():
    try:
        global interrupt_requested
        # Führe den transformierten Code aus
${transformedCode.split('\n').map(line => '        ' + line).join('\n')}

        # Final check nach Ausführung
        try:
            js_interrupt = js.eval("window.interruptFlag || false")
            if interrupt_requested or js_interrupt:
                raise KeyboardInterrupt("Programm wurde gestoppt")
        except:
            if interrupt_requested:
                raise KeyboardInterrupt("Programm wurde gestoppt")

    except KeyboardInterrupt as e:
        print(f"⏹ {e}")
        raise
    except Exception as e:
        print(f"Fehler im Benutzer-Code: {e}")
        raise

# Code ausführen
await run_user_code()
        ` : `
# Kein ausführbarer Code vorhanden
print("Programm erfolgreich ausgeführt (kein Code zum Ausführen)")
        `;

        // Ausführung mit Error-Handling
        await pyodide.runPythonAsync(asyncWrapper);

        // Finale Aktualisierung der Visualisierung (wichtig bei 0ms Geschwindigkeit)
        updateArrayDisplay();
        updateArrayStats();

        // Prüfe ob das Array korrekt sortiert ist
        const isSorted = checkArraySorted(sortarray.data);
        if (isSorted.sorted) {
            addToConsole(`✅ Array-Sortierung erfolgreich ausgeführt! (${isSorted.direction} sortiert)`, 'info');
        } else {
            addToConsole('❌ Array ist nach der Ausführung nicht korrekt sortiert!', 'warning');
        }
        addToConsole(`📊 Statistiken: Lesevorgänge: ${sortarray.readCount}, Schreibvorgänge: ${sortarray.writeCount}`, 'info');

        // Button zu "Zurücksetzen" ändern
        updateRunButton('reset');

    } catch (error) {
        const errorMessage = error.message || error.toString();

        // Unterscheide zwischen Stopp und echten Fehlern
        if (errorMessage.includes('gestoppt') || errorMessage.includes('aborted') || errorMessage.includes('KeyboardInterrupt')) {
            // Nur Ausgabe wenn noch nicht von stopArrayProgram() ausgegeben wurde
            if (!interruptFlag) {
                addToConsole('⏹ Programm wurde gestoppt', 'warning');
                if (sortarray) {
                    addToConsole(`📊 Statistiken beim Stopp: Lesevorgänge: ${sortarray.readCount}, Schreibvorgänge: ${sortarray.writeCount}`, 'info');
                }
            }
        } else {
            addToConsole('❌ Fehler beim Ausführen des Array-Programms:', 'error');

            // Verbesserte Fehlermeldung
            if (errorMessage.includes('TypeError')) {
                addToConsole('Tipp: Verwende "await" vor Array-Operationen oder nutze normale Array-Syntax', 'warning');
            }

            addToConsole(errorMessage, 'error');
            console.error('Array-Programm Fehler:', error);
        }

        // Auch bei Fehlern Button auf "Zurücksetzen" setzen
        updateRunButton('reset');
    } finally {
        // Immer Flags zurücksetzen
        isPythonRunning = false;
        interruptFlag = false; // Wichtig: Interrupt-Flag zurücksetzen
        // pythonExecutionController = null; // Nicht mehr benötigt
    }
}

// Button-Status aktualisieren
function updateRunButton(mode) {
    const runBtn = document.getElementById('runBtn');
    if (!runBtn) return;

    if (mode === 'reset') {
        runBtn.innerHTML = '<i class="fas fa-undo"></i>  Zurücksetzen';
        runBtn.className = 'btn btn-secondary';
    } else if (mode === 'running') {
        runBtn.innerHTML = '<i class="fas fa-stop"></i> Stop';
        runBtn.className = 'btn btn-danger';
    } else {
        runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
        runBtn.className = 'btn btn-primary';
    }
}


// MyPy initialisieren
async function initializeMyPy() {
    try {
        addToConsole('MyPy wird installiert...', 'info');

        // Schritt 1: Micropip laden für zusätzliche Pakete
        await pyodide.loadPackage(['micropip']);
        addToConsole('micropip geladen ✓', 'info');

        // Schritt 2: Erforderliche Pakete installieren
        await pyodide.runPythonAsync(`
import micropip
# typing-extensions und mypy_extensions über micropip installieren
await micropip.install(['typing-extensions', 'mypy-extensions'])
        `);
        addToConsole('typing-extensions und mypy-extensions installiert ✓', 'info');

        // Schritt 3: MyPy installieren
        await pyodide.loadPackage(['mypy']);
        addToConsole('MyPy-Paket geladen ✓', 'info');

        // Schritt 3: MyPy-Umgebung konfigurieren
        pyodide.runPython(`
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
                                except (ValueError, IndexError):
                                    continue

            except Exception as api_error:
                errors.append((1, 'error', f'MyPy API-Fehler: {str(api_error)}'))

        except Exception as e:
            errors.append((1, 'error', f'Type-Checker-Fehler: {str(e)}'))

        finally:
            # Temporäre Datei aufräumen
            if temp_file and os.path.exists(temp_file):
                try:
                    os.remove(temp_file)
                except:
                    pass

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
        console.log('✅ MyPy Type-Checking aktiviert und bereit');

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
    pyodide.runPython(`
import ast
import sys

class FallbackTypeChecker:
    def check_code(self, code):
        errors = []
        try:
            # Syntax-Check mit AST
            ast.parse(code)
        except SyntaxError as e:
            line_num = e.lineno or 1
            message = f"Syntax-Fehler: {e.msg}"
            errors.append((line_num, 'error', message))
        except Exception as e:
            errors.append((1, 'error', f"Code-Fehler: {str(e)}"))

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

// Python-Code Type-Checking
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

        // Array-spezifische Dummy-Funktionen für MyPy
        const pythonTypeCheckCode = `
# Array-spezifische Dummy-Funktionen für MyPy
from typing import List

class ArrayType:
    def __setitem__(self, index: int, value: int) -> None: pass
    def __getitem__(self, index: int) -> int: return 0
    def __len__(self) -> int: return 0

# Globale Dummy-Variablen
sortarray = ArrayType()

# Async Array-Operationen (werden von Code-Transformation verwendet)
async def set_array_item(index: int, value: int) -> None:
    """Setzt einen Wert im Array"""
    pass

async def get_array_item(index: int) -> int:
    """Liest einen Wert aus dem Array"""
    return 0

async def array_length() -> int:
    """Gibt die Array-Länge zurück"""
    return 0

async def swap_elements(index1: int, index2: int) -> None:
    """Tauscht zwei Array-Elemente"""
    pass
`;

        // Code mit Dummy-Funktionen für Type-Checking kombinieren
        const codeForTypeChecking = pythonTypeCheckCode + currentCode;

        // MyPy ausführen
        const errors = pyodide.runPython(`
errors = type_checker.check_code(r'''${codeForTypeChecking.replace(/'/g, "\\'")}''')
# Zeilennummern um die Anzahl der Dummy-Zeilen korrigieren
dummy_lines = ${pythonTypeCheckCode.split('\n').length}
corrected_errors = []
for line_num, error_type, message in errors:
    if line_num > dummy_lines:
        corrected_errors.append((line_num - dummy_lines + 1, error_type, message))
    # Fehler in den Dummy-Funktionen ignorieren wir
corrected_errors
        `).toJs();

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
        const line = lineNum - 1; // ACE Editor ist 0-basiert

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
    console.log('🔍 DEBUG: loadSavedContent() gestartet');

    const contentElement = document.getElementById('currentContent');
    const defaultSubmissionElement = document.getElementById('defaultSubmission');

    // 1. Prüfe currentContent
    if (contentElement && contentElement.textContent.trim()) {
        console.log('🔍 DEBUG: currentContent gefunden, lade...');
        const savedContent = contentElement.textContent.trim();
        loadContentToView(savedContent);
        return true;
    }

    // 2. Prüfe defaultSubmission
    if (defaultSubmissionElement && defaultSubmissionElement.textContent.trim()) {
        console.log('🔍 DEBUG: defaultSubmission gefunden, lade...');
        const defaultContent = defaultSubmissionElement.textContent.trim();
        pythonEditor.setValue(defaultContent);
        updateSaveStatus('saved');
        return true;
    }

    // 3. Nichts gefunden
    console.log('🔍 DEBUG: Kein Content gefunden');
    return false;
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
    try {
        tutorialContents = new Function(`return (${tutorialText});`)();
        console.log('Tutorial-Inhalte erfolgreich geladen:', tutorialContents);
    } catch (e) {
        console.error('Fehler beim Parsen der Tutorial-Inhalte:', e);
        tutorialContents = []; // Leeres Array im Fehlerfall
    }
} else {
    console.log('Keine Tutorial-Inhalte gefunden.');
    tutorialContents = []; // Sicherstellen, dass es ein Array ist
}


let currentTutorialIndex = 0;

// Task-Content initialisieren
function initializeTaskContent() {
    const descriptionElement = document.getElementById("description");
    if (descriptionElement) {
        updateTaskTab(descriptionElement.textContent);
    } else {
        console.warn("Beschreibungselement nicht gefunden.");
    }
}

// Tutorial Navigation initialisieren
function initializeTutorialNavigation() {
    const tutorialOutput = document.getElementById('tutorialOutput');

    // Überprüfen, ob tutorialContents leer ist oder nicht existiert
    if (!tutorialContents || tutorialContents.length === 0) {
        console.log('Kein oder leeres Tutorial-Inhalt, überspringe Initialisierung.');
        // Stelle sicher, dass das Tutorial-Tab ausgeblendet wird, falls es existiert
        const tutorialTab = document.querySelector('.output-tab[data-output-tab="tutorial"]');
        if (tutorialTab) {
            tutorialTab.style.display = 'none';
        }
        return; // Funktion beenden
    }

    // Stelle sicher, dass das Tutorial-Tab sichtbar ist, wenn Inhalt vorhanden ist
    const tutorialTab = document.querySelector('.output-tab[data-output-tab="tutorial"]');
    if (tutorialTab) {
        tutorialTab.style.display = 'block';
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
    const prevButton = document.getElementById('tutorialPrev');
    const nextButton = document.getElementById('tutorialNext');

    if (prevButton) {
        prevButton.addEventListener('click', () => {
            if (currentTutorialIndex > 0) {
                currentTutorialIndex--;
                updateTutorialDisplay();
            }
        });
    }

    if (nextButton) {
        nextButton.addEventListener('click', () => {
            if (currentTutorialIndex < tutorialContents.length - 1) {
                currentTutorialIndex++;
                updateTutorialDisplay();
            }
        });
    }

    // Dot Navigation
    document.querySelectorAll('.tutorial-dot').forEach(dot => {
        dot.addEventListener('click', (e) => {
            const index = parseInt(e.target.dataset.index);
            if (!isNaN(index) && index >= 0 && index < tutorialContents.length) {
                currentTutorialIndex = index;
                updateTutorialDisplay();
            }
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
    const prevButton = document.getElementById('tutorialPrev');
    const nextButton = document.getElementById('tutorialNext');

    if (prevButton) {
        prevButton.disabled = currentTutorialIndex === 0;
    }
    if (nextButton) {
        nextButton.disabled = currentTutorialIndex === tutorialContents.length - 1;
    }

    // Content aktualisieren - zeige Markdown statt iframe
    const tutorialFrame = document.getElementById('tutorialFrame');
    if (!tutorialFrame) {
        console.error("Tutorial Frame nicht gefunden.");
        return;
    }

    // Sicherstellen, dass tutorialContents und currentTutorialIndex gültig sind
    if (!tutorialContents || tutorialContents.length === 0 ||
        currentTutorialIndex < 0 || currentTutorialIndex >= tutorialContents.length) {
        console.warn("Ungültiger Zustand für Tutorial-Display.");
        tutorialFrame.src = ""; // Leere Quelle setzen
        return;
    }

    const currentContentItem = tutorialContents[currentTutorialIndex];
    const currentContent = currentContentItem.content;
    const contentTitle = currentContentItem.title || `Schritt ${currentTutorialIndex + 1}`;

    // Erstelle HTML für Markdown-Content
    const htmlContent = `
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>${contentTitle}</title>
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
            <h1>${contentTitle}</h1>
            ${renderMarkdown(currentContent)}
        </body>
        </html>
    `;

    const blob = new Blob([htmlContent], { type: 'text/html' });
    const url = URL.createObjectURL(blob);

    // Setze die src des iframes und bereinige die URL nach dem Laden
    tutorialFrame.onload = () => {
        URL.revokeObjectURL(url);
    };
    tutorialFrame.src = url;
}

// Task-Tab aktualisieren
function updateTaskTab(markdownText) {
    const taskOutput = document.getElementById('taskOutput');
    const taskTab = document.querySelector('.output-tab[data-output-tab="task"]');

    if (taskOutput) {
        // Überprüfen, ob markdownText nicht leer ist
        if (markdownText && markdownText.trim() !== '') {
            taskOutput.innerHTML = renderMarkdown(markdownText);
            if (taskTab) taskTab.style.display = 'block'; // Tab anzeigen
        } else {
            taskOutput.innerHTML = ''; // Inhalte löschen
            if (taskTab) taskTab.style.display = 'none'; // Tab ausblenden
        }
    } else {
        console.warn("taskOutput Element nicht gefunden.");
    }
}

// Array-Konfiguration initialisieren (ersetzt Hamster-Konfiguration)
function initializeArrayConfig() {
    console.log('🔍 DEBUG: initializeArrayConfig() aufgerufen - lade Standard 20er Array');

    // Erstelle Standard-Array (20 Elemente) nur wenn sortarray noch nicht existiert
    if (!sortarray) {
        console.log('🔍 DEBUG: Erstelle Standard-Array (20 Elemente)');
        loadConfiguration(0); // Lädt automatisch 20er Array
        initializeArrayVisualization();
    } else {
        console.log('🔍 DEBUG: sortarray bereits vorhanden, aktualisiere nur UI');
        updateArrayDisplay();
        updateArrayStats();
        updateConfigSelector();
    }
}

// Hilfsfunktion zum Parsen einer Array-Konfiguration (muss ggf. angepasst werden)
function parseArrayConfig(config) {
    // Dieses Beispiel geht davon aus, dass die Konfiguration grundlegende Array-Einstellungen enthält
    return {
        name: config.name || 'Standard-Array',
        size: config.arraySize || 20, // Standardgröße 20
        maxValue: config.maxValue || 100, // Maximalwert 100
        initialData: config.initialData || null, // Optionale Initialdaten
        // Weitere spezifische Einstellungen für die Visualisierung könnten hier hinzugefügt werden
    };
}

// Konfiguration laden (mit DOM-Neuerstellung oder Aktualisierung)
function loadConfiguration(index) {
    console.log('🔍 DEBUG: loadConfiguration() aufgerufen mit Index:', index);

    // Stelle sicher, dass mindestens eine Standard-Konfiguration existiert
    if (worldConfigurations.length === 0) {
        worldConfigurations.push({
            name: 'Standard-Array',
            size: 20,
            maxValue: 100,
            initialData: null
        });
    }

    // Index-Validierung
    if (index < 0 || index >= worldConfigurations.length) {
        index = 0;
    }

    const config = worldConfigurations[index];
    console.log('🔍 DEBUG: Erstelle Array mit Konfiguration:', config);

    // Array-Objekt erstellen
    sortarray = {
        data: generateRandomArray(config.size),
        size: config.size,
        maxValue: config.maxValue,
        writeCount: 0,
        readCount: 0,
        callbacks: []
    };

    currentConfigIndex = index;
    console.log('🔍 DEBUG: Array erstellt, Größe:', sortarray.size);

    // UI aktualisieren
    updateArrayVisualization();
    updateConfigSelector();
    updateRunButton('execute');
}

// Konfigurationsselektor aktualisieren (jetzt Array-Größen-Selektor)
function updateConfigSelector() {
    const configSelector = document.getElementById('configSelector');
    if (configSelector && sortarray) {
        configSelector.innerHTML = `<i class="fas fa-th-list"></i> ${sortarray.size}`;
        configSelector.title = 'Array-Größe ändern (aktuell: ' + sortarray.size + ' Elemente)';
        configSelector.style.cursor = 'pointer';
    } else if (configSelector) {
        // Fallback wenn sortarray nicht verfügbar ist
        configSelector.innerHTML = `<i class="fas fa-th-list"></i> 20`;
        configSelector.title = 'Array-Größe ändern';
        configSelector.style.cursor = 'pointer';
    }
}

// CSS-Transition für Array-Elemente aktualisieren
function updateHamsterTransition() {
    const arrayElements = document.querySelectorAll('.array-element');
    arrayElements.forEach(element => {
        if (animationSpeed > 0) {
            element.style.transition = `transform ${animationSpeed}ms ease, background-color ${animationSpeed}ms ease`;
        } else {
            element.style.transition = 'none';
        }
    });
}

// Hilfsfunktion zum Generieren eines zufälligen Arrays
function generateRandomArray(size) {
    const array = [];
    const maxValue = sortarray ? sortarray.maxValue : 100; // Nutze sortarray.maxValue wenn verfügbar
    for (let i = 0; i < size; i++) {
        array.push(Math.floor(Math.random() * maxValue) + 1); // Werte 1-maxValue
    }
    return array;
}

// Array-Statistiken aktualisieren
function updateArrayStats() {
    const statsPanel = document.getElementById('arrayStats');
    if (!statsPanel || !sortarray) return;

    statsPanel.innerHTML = `
        <div style="text-align: center;">
            <div style="color: #007bff; font-size: 16px; font-weight: bold;">${sortarray.readCount}</div>
            <div style="color: #888; font-size: 11px;">👁️ Lesevorgänge</div>
        </div>
        <div style="text-align: center;">
            <div style="color: #28a745; font-size: 16px; font-weight: bold;">${sortarray.writeCount}</div>
            <div style="color: #888; font-size: 11px;">✏️ Schreibvorgänge</div>
        </div>
        <div style="text-align: center;">
            <div style="color: #17a2b8; font-size: 16px; font-weight: bold;">${sortarray.size}</div>
            <div style="color: #888; font-size: 11px;">📏 Array-Größe</div>
        </div>
    `;
}

// Array-Größe ändern
function changeArraySize(newSize) {
    if (!sortarray) return;

    addToConsole(`🔧 Ändere Array-Größe von ${sortarray.size} auf ${newSize}`, 'info');

    // Neue Konfiguration erstellen
    const newConfig = {
        size: newSize,
        maxValue: sortarray.maxValue,
        initialData: null // Neues zufälliges Array generieren
    };

    // Array neu initialisieren
    sortarray.data = generateRandomArray(newSize);
    sortarray.size = newSize;
    sortarray.writeCount = 0;
    sortarray.readCount = 0;

    // Visualisierung aktualisieren
    updateArrayDisplay();
    updateArrayStats();
    updateConfigSelector();

    // Button-Status zurücksetzen
    updateRunButton('execute');
}


// Array-Visualisierung initialisieren und aktualisieren
function initializeArrayVisualization(config = null) {
    console.log('🔍 DEBUG: initializeArrayVisualization() aufgerufen mit config:', config);
    console.log('🔍 DEBUG: sortarray Status bei initializeArrayVisualization Beginn:', sortarray ? `existiert (Größe: ${sortarray.size}, Daten: [${sortarray.data.slice(0, 5).join(', ')}...])` : 'nicht vorhanden');
    
    // Erstelle Array-Container wenn nicht vorhanden
    let arrayContainer = document.getElementById('arrayVisualization');
    if (!arrayContainer) {
        // Array-Container als Ersatz für hamsterTerritory erstellen
        const hamsterTerritory = document.getElementById('hamsterTerritory');
        if (hamsterTerritory) {
            // Erstelle Haupt-Container
            const mainContainer = document.createElement('div');
            mainContainer.id = 'arrayVisualizationMain';
            mainContainer.style.cssText = `
                background: #1e1e1e;
                border: 1px solid #404040;
                border-radius: 8px;
                padding: 15px;
                margin: 10px 0;
                color: #cccccc;
                font-family: 'Segoe UI', sans-serif;
            `;

            // Statistik-Panel
            const statsPanel = document.createElement('div');
            statsPanel.id = 'arrayStats';
            statsPanel.style.cssText = `
                display: flex;
                justify-content: space-around;
                background: #2d2d2d;
                border-radius: 6px;
                padding: 10px;
                margin-bottom: 15px;
                font-size: 14px;
                font-weight: 500;
            `;

            // Array-Container
            arrayContainer = document.createElement('div');
            arrayContainer.id = 'arrayVisualization';
            arrayContainer.style.cssText = `
                display: flex;
                align-items: flex-end;
                justify-content: flex-start;
                min-height: 220px;
                padding: 10px;
                background: #2a2a2a;
                border-radius: 6px;
                border: 1px solid #404040;
                overflow: hidden;
                width: 100%;
            `;

            mainContainer.appendChild(statsPanel);
            mainContainer.appendChild(arrayContainer);

            hamsterTerritory.parentNode.insertBefore(mainContainer, hamsterTerritory);
            hamsterTerritory.style.display = 'none';
        } else {
            console.error('Kein Container für Array-Visualisierung gefunden');
            return;
        }
    }

    // Standardkonfiguration laden, falls keine übergeben wird
    if (!config) {
        config = {
            size: 20,
            maxValue: 100,
            initialData: null
        };
    }

    // Array-Objekt erstellen, falls noch nicht geschehen
    if (!sortarray) {
        console.log('🔍 DEBUG: Erstelle NEUES sortarray in initializeArrayVisualization mit Größe:', config.size);
        const newData = config.initialData ? config.initialData : generateRandomArray(config.size);
        console.log('🔍 DEBUG: Neue Array-Daten generiert:', newData.slice(0, 10));
        
        sortarray = {
            data: newData,
            size: config.size,
            maxValue: config.maxValue,
            writeCount: 0,
            readCount: 0,
            callbacks: []
        };
        console.log('🔍 DEBUG: Neues sortarray erstellt mit Größe:', sortarray.size);
    } else {
        console.log('🔍 DEBUG: AKTUALISIERE bestehendes sortarray von Größe', sortarray.size, 'auf', config.size);
        const oldData = sortarray.data.slice(0, 5);
        
        // Wenn sortarray bereits existiert, Daten und Größe aktualisieren
        sortarray.data = config.initialData ? config.initialData : generateRandomArray(config.size);
        sortarray.size = config.size;
        sortarray.maxValue = config.maxValue;
        // Zähler zurücksetzen bei Neukonfiguration
        sortarray.writeCount = 0;
        sortarray.readCount = 0;
        
        console.log('🔍 DEBUG: Array aktualisiert - Alte Daten:', oldData, '- Neue Daten:', sortarray.data.slice(0, 5));
        console.log('🔍 DEBUG: Neue Array-Größe:', sortarray.size);
    }

    // Aktualisiere die Array-Darstellung
    updateArrayDisplay();
    updateArrayStats();

    const currentSize = sortarray.size;
    addToConsole(`🎲 Neues Array 'sortarray' mit ${currentSize} Elementen generiert`, 'info');
    
    // Transistionen aktualisieren
    updateHamsterTransition(); // Benutzt nun die Geschwindigkeit für Array-Elemente
}

// Alias für updateArrayVisualization (wird in verschiedenen Teilen des Codes aufgerufen)
function updateArrayVisualization() {
    updateArrayDisplay();
}

// Array-Display aktualisieren
function updateArrayDisplay(enableAnimation = false) {
    const arrayContainer = document.getElementById('arrayVisualization');
    if (!arrayContainer || !sortarray) {
        console.error('Array-Container oder sortarray nicht verfügbar');
        return;
    }

    // Verhindere mehrfache gleichzeitige Ausführung
    if (arrayContainer.dataset.updating === 'true') {
        return;
    }
    arrayContainer.dataset.updating = 'true';

    // Immer komplett neu erstellen um Flackern zu vermeiden
    arrayContainer.innerHTML = '';

    // Bestimme verfügbare Breite (nutze die komplette Breite)
    const containerRect = arrayContainer.getBoundingClientRect();
    const containerWidth = Math.max(containerRect.width - 20, 200); // Mindestbreite 200px
    const maxHeight = 180; // Maximale Höhe in Pixel

    // Verbesserte dynamische Abstände basierend auf Array-Größe und Fensterbreite
    let gapWidth = 0; // Standard: keine Abstände

    // Nur bei wenigen Elementen und ausreichend Platz Abstände hinzufügen
    if (sortarray.size <= 20 && containerWidth > 600) {
        gapWidth = 3;
    } else if (sortarray.size <= 50 && containerWidth > 400) {
        gapWidth = 2;
    } else if (sortarray.size <= 100 && containerWidth > 300) {
        gapWidth = 1;
    }
    // Bei >100 Elementen oder schmalen Fenstern: keine Abstände

    // Berechne dynamische Balken-Breite für optimale Breitennutzung
    const totalGaps = Math.max(0, (sortarray.size - 1) * gapWidth);
    const availableWidthForBars = containerWidth - totalGaps;
    let barWidth = Math.floor(availableWidthForBars / sortarray.size);

    // Mindestbreite garantieren und bei Bedarf Abstände entfernen
    if (barWidth < 1) {
        // Wenn Balken zu schmal werden, entferne alle Abstände
        gapWidth = 0;
        barWidth = Math.floor(containerWidth / sortarray.size);
    }

    // Absolute Mindestbreite von 1px garantieren
    let finalBarWidth = Math.max(barWidth, 1);

    // Bei sehr vielen Elementen (>150) zusätzliche Optimierungen
    if (sortarray.size > 150) {
        // Nutze jeden verfügbaren Pixel
        finalBarWidth = Math.max(Math.floor(containerWidth / sortarray.size), 1);
        gapWidth = 0; // Garantiert keine Abstände
    }

    // Bestimmt welche Indizes Labels bekommen (abhängig von verfügbarem Platz)
    const shouldShowIndexLabels = finalBarWidth >= 12; // Nur bei ausreichender Breite (gleich wie Säulen-Zahlen)
    let indicesToShow = [];
    if (shouldShowIndexLabels) {
        const maxLabels = Math.min(Math.floor(containerWidth / 60), 15); // Max Labels basierend auf Breite
        if (sortarray.size <= maxLabels) {
            // Alle Indizes anzeigen wenn Array klein genug
            indicesToShow = Array.from({length: sortarray.size}, (_, i) => i);
        } else if (maxLabels > 2) {
            // Berechne gleichmäßig verteilte Indizes ohne Duplikate
            indicesToShow = [];

            // Immer Index 0 (erster) hinzufügen
            indicesToShow.push(0);

            // Immer Index size-1 (letzter) hinzufügen, außer wenn Array nur 1 Element hat
            if (sortarray.size > 1) {
                indicesToShow.push(sortarray.size - 1);
            }

            // Mittlere Indizes berechnen, falls Platz für mehr als 2 Labels
            if (maxLabels > 2 && sortarray.size > 2) {
                const middleLabels = maxLabels - 2; // Abzüglich erstem und letztem

                if (middleLabels > 0) {
                    // Berechne Abstand zwischen den Labels
                    const totalRange = sortarray.size - 1; // Von 0 bis size-1
                    const step = totalRange / (maxLabels - 1);

                    // Füge mittlere Indizes hinzu (ohne erste und letzte)
                    for (let i = 1; i < maxLabels - 1; i++) {
                        const index = Math.round(i * step);
                        // Nur hinzufügen wenn es nicht bereits 0 oder size-1 ist
                        if (index > 0 && index < sortarray.size - 1 && !indicesToShow.includes(index)) {
                            indicesToShow.push(index);
                        }
                    }
                }
            }

            // Sortiere die Indizes und entferne eventuelle Duplikate
            indicesToShow = [...new Set(indicesToShow)].sort((a, b) => a - b);

            // Reduziere auf maxLabels falls zu viele
            if (indicesToShow.length > maxLabels) {
                const reduced = [];
                const reduceStep = (indicesToShow.length - 1) / (maxLabels - 1);

                for (let i = 0; i < maxLabels; i++) {
                    const index = Math.round(i * reduceStep);
                    if (index < indicesToShow.length) {
                        reduced.push(indicesToShow[index]);
                    }
                }
                indicesToShow = reduced;
            }
        }
    }

    // Erstelle Balken für jedes Array-Element
    sortarray.data.forEach((value, index) => {
        createArrayBar(index, value, finalBarWidth, maxHeight, gapWidth, indicesToShow, shouldShowIndexLabels);
    });

    // Statistiken aktualisieren
    updateArrayStats();

    // Update-Flag zurücksetzen
    arrayContainer.dataset.updating = 'false';
}

// TaskView-konforme Funktionen
function getContentFromView() {
    // Sammle alle relevanten Daten für Array-Visualisierung
    const content = {
        version: "1.1",
        type: "array-visualization",
        pythonCode: pythonEditor.getValue(),
        currentTutorialIndex: currentTutorialIndex,
        arrayConfiguration: {
            size: sortarray ? sortarray.size : 20,
            maxValue: sortarray ? sortarray.maxValue : 100
            // currentData entfernt - wird zufällig neu generiert
        },
        animationSpeed: animationSpeed,
        statistics: {
            readCount: sortarray ? sortarray.readCount : 0,
            writeCount: sortarray ? sortarray.writeCount : 0
        },
        metadata: {
            lastModified: new Date().toISOString(),
            codeLength: pythonEditor.getValue().length,
            editorCursorPosition: pythonEditor.getCursorPosition()
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

        // Python-Code laden
        if (data.pythonCode) {
            pythonEditor.setValue(data.pythonCode);
            console.log('Python-Code erfolgreich geladen');

            // Cursor-Position wiederherstellen wenn gespeichert
            if (data.metadata && data.metadata.editorCursorPosition) {
                pythonEditor.moveCursorToPosition(data.metadata.editorCursorPosition);
            }
        }

        // Tutorial-Index laden
        if (data.currentTutorialIndex !== undefined && tutorialContents &&
            data.currentTutorialIndex >= 0 && data.currentTutorialIndex < tutorialContents.length) {
            currentTutorialIndex = data.currentTutorialIndex;
            updateTutorialDisplay();
        }

        // Array-Konfiguration laden
        if (data.arrayConfiguration) {
            const config = data.arrayConfiguration;
            console.log('🔍 DEBUG: Array-Konfiguration wiederhergestellt:', config);
            console.log('🔍 DEBUG: sortarray Status vor Wiederherstellung:', sortarray ? `existiert (Größe: ${sortarray.size})` : 'nicht vorhanden');

            // Array direkt mit der korrekten Größe initialisieren
            if (!sortarray) {
                console.log('🔍 DEBUG: Erstelle neues sortarray mit Größe:', config.size || 20);
                const newData = generateRandomArray(config.size || 20);
                console.log('🔍 DEBUG: Neue Daten generiert:', newData.slice(0, 10));

                sortarray = {
                    data: newData,
                    size: config.size || 20,
                    maxValue: config.maxValue || 100,
                    writeCount: 0,
                    readCount: 0,
                    callbacks: []
                };
            } else {
                console.log('🔍 DEBUG: Aktualisiere bestehendes sortarray von Größe', sortarray.size, 'auf', config.size || 20);
                // Bestehende Array-Größe und Daten aktualisieren
                sortarray.size = config.size || 20;
                sortarray.maxValue = config.maxValue || 100;
                const newData = generateRandomArray(sortarray.size);
                console.log('🔍 DEBUG: Neue Daten für Update generiert:', newData.slice(0, 10));
                sortarray.data = newData;
                sortarray.writeCount = 0;
                sortarray.readCount = 0;
            }

            console.log('🔍 DEBUG: sortarray nach Wiederherstellung:', {
                size: sortarray.size,
                maxValue: sortarray.maxValue,
                dataPreview: sortarray.data.slice(0, 10)
            });

            // Sicherstellen dass Array-Visualisierung existiert
            console.log('🔍 DEBUG: Initialisiere Array-Visualisierung');
            initializeArrayVisualization({
                size: sortarray.size,
                maxValue: sortarray.maxValue,
                initialData: null
            });

            // Config-Index für UI-Update setzen
            const sizeToIndex = { 10: 0, 20: 1, 100: 2, 200: 3 };
            currentConfigIndex = sizeToIndex[config.size] || 1;
            console.log('🔍 DEBUG: Config-Index gesetzt auf:', currentConfigIndex);
            updateConfigSelector();
        } else {
            console.log('🔍 DEBUG: Keine Array-Konfiguration in Data gefunden, lade Standard-Konfiguration');
            // Fallback: Standard-Konfiguration laden
            loadConfiguration(currentConfigIndex);
        }

        // Animation Speed wiederherstellen
        if (data.animationSpeed !== undefined) {
            animationSpeed = data.animationSpeed;
            const speedSelector = document.getElementById('speedSelector');
            if (speedSelector) {
                speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
            }
            updateHamsterTransition();
        }

        // Tutorial Index wiederherstellen
        if (data.currentTutorialIndex !== undefined) {
            currentTutorialIndex = data.currentTutorialIndex;
        }

        updateSaveStatus('saved');
        console.log('Content-Version 1.1 erfolgreich geladen');

    } catch (error) {
        console.error('Fehler beim Laden des Inhalts:', error);
        updateSaveStatus('error');

        // Fallback: Versuche als reiner Text zu laden
        if (typeof content === 'string' && content.trim()) {
            pythonEditor.setValue(content);
            console.log('Inhalt als reiner Text geladen (Fallback)');
            updateSaveStatus('saved');
        }
    }
}

function saveContent(isSubmission = false) {
    console.log('Speichere Inhalt...', isSubmission ? '(Abgabe)' : '(Normal)');
    updateSaveStatus('saving');

    try {
        const content = getContentFromView();
        const urlElement = document.getElementById(isSubmission ? 'task-submit-url' : 'task-save-url');
        const url = urlElement ? document.getElementById('default-link') + urlElement.getAttribute('data-url') : '';

        if (!url) {
            console.error('Keine URL für Speicherung gefunden');
            updateSaveStatus('error');
            return Promise.reject(new Error('Keine Speicher-URL gefunden'));
        }

        if (isSubmission && !url.trim()) {
            console.error('Abgabe-URL ist leer - möglicherweise im Lehrer-Modus');
            updateSaveStatus('error');
            return Promise.reject(new Error('Abgabe nicht möglich'));
        }

        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ content: content })
        })
        .then(response => {
            if (response.ok) {
                updateSaveStatus(isSubmission ? 'submitted' : 'saved');
                console.log(`${isSubmission ? 'Abgabe' : 'Speicherung'} erfolgreich (${response.status})`);

                // Benachrichtigung an Parent-Window für iFrame-Integration
                if (window.parent && window.parent !== window) {
                    window.parent.postMessage(isSubmission ? 'content-submitted' : 'content-saved', '*');
                }

                return response;
            } else {
                updateSaveStatus('error');
                console.error(`${isSubmission ? 'Abgabe' : 'Speicher'}fehler:`, response.status, response.statusText);
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
        })
        .catch(error => {
            console.error(`${isSubmission ? 'Abgabe' : 'Speicher'}fehler:`, error);
            updateSaveStatus('error');
            throw error;
        });

    } catch (error) {
        console.error('Fehler beim Vorbereiten der Speicherung:', error);
        updateSaveStatus('error');
        return Promise.reject(error);
    }
}

function submitTask() {
    // Prüfe ob Code vorhanden ist
    const code = pythonEditor.getValue().trim();
    if (!code) {
        alert('Bitte geben Sie zunächst Code ein, bevor Sie die Aufgabe abgeben.');
        return;
    }

    // Bestätigungsdialog mit mehr Informationen
    const confirmMessage = `Möchten Sie diese Aufgabe wirklich abgeben?

Ihr aktueller Code:
- ${code.split('\n').length} Zeilen Code
- Array-Größe: ${sortarray ? sortarray.size : 'unbekannt'}
- Animationsgeschwindigkeit: ${animationSpeed}ms

Nach der Abgabe können Sie keine Änderungen mehr vornehmen.`;

    if (confirm(confirmMessage)) {
        saveContent(true).then(() => {
            console.log('Aufgabe erfolgreich abgegeben');
            alert('Aufgabe wurde erfolgreich abgegeben!');
        }).catch(error => {
            console.error('Fehler bei der Abgabe:', error);
            alert('Fehler bei der Abgabe: ' + error.message);
        });
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
    loadContentToView: loadContentToView,
    // Array API
    set_array_item: async (index, value) => { await pyodide.globals.get('arraySetItem')(index, value); },
    get_array_item: async (index) => { return await pyodide.globals.get('arrayGetItem')(index); },
    get_array_length: async () => { return await pyodide.globals.get('arrayLen')(); },
    swap_elements: async (index1, index2) => { await pyodide.globals.get('arraySwap')(index1, index2); },
    // Zugriff auf das sortarray-Objekt selbst (falls benötigt)
    get_sortarray_data: () => {
        if (sortarray) {
            return [...sortarray.data]; // Rückgabe einer Kopie
        }
        return [];
    },
    get_sortarray_stats: () => {
        if (sortarray) {
            return {
                size: sortarray.size,
                reads: sortarray.readCount,
                writes: sortarray.writeCount
            };
        }
        return null;
    },
    // Funktion zum Registrieren von Callbacks für Array-Änderungen
    register_array_callback: (callback) => {
        if (sortarray && typeof callback === 'function') {
            sortarray.callbacks.push(callback);
        }
    },
    // Funktion zum Entfernen von Callbacks
    unregister_array_callback: (callback) => {
        if (sortarray && sortarray.callbacks) {
            sortarray.callbacks = sortarray.callbacks.filter(cb => cb !== callback);
        }
    },
    // Funktion zum Aktualisieren der Array-Visualisierung
    update_array_visualization: () => {
        updateArrayDisplay();
    },
    // Funktion zum Ändern der Animationsgeschwindigkeit
    set_animation_speed: (speed) => {
        animationSpeed = speed;
        const speedSelector = document.getElementById('speedSelector');
        if (speedSelector) {
            speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
        }
        updateHamsterTransition(); // Transition aktualisieren
    },
    set_current_config: (index) => {
        if (index >= 0 && index < worldConfigurations.length) {
            currentConfigIndex = index;
            loadConfiguration(currentConfigIndex);
            updateConfigSelector();
        }
    }
};

// Hilfsfunktion zum Auslösen von Callbacks (z.B. bei Änderungen am Array)
function triggerCallbacks() {
    if (sortarray && sortarray.callbacks) {
        sortarray.callbacks.forEach(callback => callback());
    }
}

// Hilfsfunktion zur Überprüfung ob Array sortiert ist
function checkArraySorted(array) {
    if (!array || array.length <= 1) {
        return { sorted: true, direction: 'trivial' };
    }

    let ascending = true;
    let descending = true;

    for (let i = 1; i < array.length; i++) {
        if (array[i] < array[i - 1]) {
            ascending = false;
        }
        if (array[i] > array[i - 1]) {
            descending = false;
        }

        // Wenn weder aufsteigend noch absteigend, ist es nicht sortiert
        if (!ascending && !descending) {
            return { sorted: false, direction: 'unsorted' };
        }
    }

    if (ascending) {
        return { sorted: true, direction: 'aufsteigend' };
    } else if (descending) {
        return { sorted: true, direction: 'absteigend' };
    } else {
        return { sorted: false, direction: 'unsorted' };
    }
}

// Hilfsfunktion zum Erstellen eines neuen Array-Balkens
function createArrayBar(index, value, finalBarWidth, maxHeight, gapWidth, indicesToShow, shouldShowIndexLabels) {
    const arrayContainer = document.getElementById('arrayVisualization');
    if (!arrayContainer || !sortarray) return;

    // Spezielle Behandlung für Flexbox-Layout bei sehr vielen Elementen
    const useFlexLayout = (finalBarWidth === 'flex');

    // Balken-Container
    const barContainer = document.createElement('div');

    if (useFlexLayout) {
        // Flexbox-Layout für gleichmäßige Verteilung
        barContainer.style.cssText = `
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
            flex: 1 1 0;
            min-width: 1px;
            max-width: 4px;
        `;
    } else {
        // Normales Layout
        barContainer.style.cssText = `
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-right: ${index < sortarray.size - 1 ? gapWidth : 0}px;
            position: relative;
            flex: 0 0 ${finalBarWidth}px;
        `;
    }

    // Balken-Element
    const barElement = document.createElement('div');
    barElement.className = 'array-element';
    barElement.id = `array-element-${index}`;

    const barHeight = Math.max((value / sortarray.maxValue) * maxHeight, 2);

    // Dynamische Schriftgröße basierend auf Balken-Breite oder Layout
    let fontSize;
    const effectiveWidth = useFlexLayout ? 2 : finalBarWidth;

    if (effectiveWidth >= 25) {
        fontSize = 12;
    } else if (effectiveWidth >= 15) {
        fontSize = 10;
    } else if (effectiveWidth >= 8) {
        fontSize = 8;
    } else if (effectiveWidth >= 4) {
        fontSize = 6;
    } else {
        fontSize = 0; // Keine Schrift bei sehr schmalen Balken
    }

    // Bestimme Farbe basierend auf letzten Operationen (nur bei langsamer Ausführung)
    let backgroundColor = 'linear-gradient(135deg, #007bff 0%, #0056b3 100%)'; // Standard blau
    let borderColor = '#0056b3';
    let boxShadow = finalBarWidth >= 3 ? '0 1px 3px rgba(0,0,0,0.2)' : 'none';

    // Nur bei langsamer Ausführung (animationSpeed > 0) Farben anwenden
    if (animationSpeed > 0) {
        const isLastWrite = lastWriteIndices.includes(index);
        const isLastRead = lastReadIndices.includes(index);

        if (isLastWrite) {
            // Rot hat Priorität - Schreibvorgänge
            backgroundColor = 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)';
            borderColor = '#c82333';
            boxShadow = finalBarWidth >= 3 ? '0 2px 6px rgba(220, 53, 69, 0.4)' : 'none';
        } else if (isLastRead) {
            // Grün für Lesevorgänge (nur wenn nicht geschrieben)
            backgroundColor = 'linear-gradient(135deg, #28a745 0%, #1e7e34 100%)';
            borderColor = '#1e7e34';
            boxShadow = finalBarWidth >= 3 ? '0 2px 6px rgba(40, 167, 69, 0.4)' : 'none';
        }
    }

    if (useFlexLayout) {
        // Flexbox-Balken
        barElement.style.cssText = `
            width: 100%;
            height: ${barHeight}px;
            background: ${backgroundColor};
            border: none;
            border-radius: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: ${fontSize}px;
            font-weight: 600;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            min-width: 1px;
            box-shadow: ${boxShadow};
        `;
    } else {
        // Normale Balken
        barElement.style.cssText = `
            width: ${finalBarWidth}px;
            height: ${barHeight}px;
            background: ${backgroundColor};
            border: ${finalBarWidth >= 3 ? '1px' : '0px'} solid ${borderColor};
            border-radius: ${finalBarWidth >= 4 ? '3px 3px 0 0' : '0'};
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: ${fontSize}px;
            font-weight: 600;
            text-shadow: 0 1px 2px rgba(0,0,0,0.5);
            cursor: pointer;
            box-shadow: ${boxShadow};
            position: relative;
            overflow: hidden;
        `;
    }

    // Wert-Anzeige (nur bei ausreichender Breite)
    if (effectiveWidth >= 12 && fontSize > 0) {
        barElement.textContent = value;
    }

    // Index-Label nur für ausgewählte Indizes und bei ausreichend Platz
    const indexLabel = document.createElement('div');
    const showLabels = shouldShowIndexLabels && !useFlexLayout && effectiveWidth >= 12;
    const labelFontSize = showLabels ? Math.min(Math.max(effectiveWidth / 3, 8), 11) : 0;

    indexLabel.style.cssText = `
        font-size: ${labelFontSize}px;
        color: ${(indicesToShow.includes(index) && showLabels) ? '#888888' : 'transparent'};
        margin-top: 2px;
        font-weight: 500;
        text-align: center;
        min-height: ${showLabels ? '14px' : '2px'};
        line-height: 1;
    `;

    if (indicesToShow.includes(index) && showLabels) {
        indexLabel.textContent = `[${index}]`;
    }

    barContainer.appendChild(barElement);
    barContainer.appendChild(indexLabel);
    arrayContainer.appendChild(barContainer);

    // Event Listeners hinzufügen nachdem Element im DOM ist
    addBarEventListeners(barElement, index, value, effectiveWidth);
}



// Hilfsfunktion für Event Listeners (ausgelagert um Duplikation zu vermeiden)
function addBarEventListeners(barElement, index, value, finalBarWidth) {
    barElement.addEventListener('mouseenter', (e) => {
        // Keine Tooltips während der Ausführung anzeigen
        if (isPythonRunning) {
            return;
        }

        // Visuelle Hover-Effekte nur bei ausreichender Breite
        if (finalBarWidth >= 4) {
            barElement.style.background = 'linear-gradient(135deg, #28a745 0%, #1e7e34 100%)';
            barElement.style.transform = 'translateY(-1px) scale(1.05)';
        } else {
            // Bei sehr schmalen Balken nur Farbe ändern
            barElement.style.background = 'linear-gradient(135deg, #28a745 0%, #1e7e34 100%)';
        }
        barElement.style.zIndex = '100';

        // Tooltip für Wert und Index (immer anzeigen bei hover, auch bei schmalen Balken)
        const tooltip = document.createElement('div');
        tooltip.id = `tooltip-${index}`;

        // Berechne Position relativ zum Viewport
        const rect = barElement.getBoundingClientRect();
        const tooltipX = rect.left + rect.width / 2;
        const tooltipY = rect.top;

        tooltip.style.cssText = `
            position: fixed;
            top: ${tooltipY - 30}px;
            left: ${tooltipX}px;
            transform: translateX(-50%);
            background: #333;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            white-space: nowrap;
            z-index: 10000;
            border: 1px solid #555;
            pointer-events: none;
            box-shadow: 0 2px 8px rgba(0,0,0,0.3);
        `;
        tooltip.textContent = `[${index}]: ${value}`;

        // Tooltip am body anhängen statt am barContainer
        document.body.appendChild(tooltip);

        // Prüfe ob Tooltip außerhalb des Viewports ist und korrigiere Position
        const tooltipRect = tooltip.getBoundingClientRect();
        if (tooltipRect.left < 0) {
            tooltip.style.left = '5px';
            tooltip.style.transform = 'none';
        } else if (tooltipRect.right > window.innerWidth) {
            tooltip.style.left = (window.innerWidth - tooltipRect.width - 5) + 'px';
            tooltip.style.transform = 'none';
        }

        if (tooltipRect.top < 0) {
            tooltip.style.top = (rect.bottom + 5) + 'px';
        }
    });

    barElement.addEventListener('mouseleave', () => {
        // Nur Hover-Effekte entfernen wenn nicht gerade ausgeführt wird
        if (!isPythonRunning) {
            barElement.style.background = 'linear-gradient(135deg, #007bff 0%, #0056b3 100%)';
            if (finalBarWidth >= 4) {
                barElement.style.transform = 'translateY(0) scale(1)';
            }
            barElement.style.zIndex = '1';
        }

        // Tooltip entfernen
        const tooltip = document.getElementById(`tooltip-${index}`);
        if (tooltip) tooltip.remove();
    });
}
