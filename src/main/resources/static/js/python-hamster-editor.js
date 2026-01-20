// Globale Variablen
let pythonEditor;
let pyodide = null;
let pyodideReady = false;
let isResizing = false;
let typeCheckInterval = null;
let lastPythonCode = '';
let mypyReady = false;

// Globale Variablen für Hamster-Simulation
let hamsterState = {
    grains: 0,
    direction: 'east', // north, east, south, west
    position: { x: 0, y: 0 },
    running: false,
    world: null // Wird später initialisiert
};

let currentConfigIndex = 0; // Für die Auswahl der Weltkonfiguration
let animationSpeed = 1000; // Geschwindigkeit der Animation in ms

// Konfigurationen für die Hamster-Welt
const worldConfigurations = [];

// Initialisierung beim Laden der Seite
document.addEventListener('DOMContentLoaded', function() {
    initializeEditors();
    initializeTabs();
    initializeOutputTabs();
    initializeResizer();
    initializeControls();
    initializeHamsterConfig();
    initializePyodide();
    initializeTaskContent();
    initializeTutorialNavigation();

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

    // Ausgabe löschen Button
    const clearOutputBtn = document.getElementById('clearOutputBtn');
    clearOutputBtn.addEventListener('click', function() {
        clearConsoleOutput();
    });

    // Ausführen/Stop/Zurücksetzen-Button
    document.getElementById('runBtn').addEventListener('click', function() {
        if (pyodideReady) {
            if (this.textContent.includes('Zurücksetzen')) {
                resetHamsterWorld();
            } else if (this.textContent.includes('Stop')) {
                stopHamsterProgram();
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

    // Weltkonfiguration auswählen
    const configSelect = document.getElementById('worldConfigSelect');
    if (configSelect && worldConfigurations.length > 0) {
        worldConfigurations.forEach((_, index) => {
            const option = document.createElement('option');
            option.value = index;
            option.textContent = `Welt ${index + 1}`;
            configSelect.appendChild(option);
        });

        configSelect.addEventListener('change', function() {
            currentConfigIndex = parseInt(this.value);
            loadConfiguration(currentConfigIndex);
        });
        loadConfiguration(currentConfigIndex); // Standardkonfiguration laden
    }

    // Geschwindigkeitsselektor initialisieren
    const speedSelector = document.getElementById('speedSelector');
    if (speedSelector) {
        speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
        speedSelector.onclick = () => {
            // Zyklisch durch verschiedene Geschwindigkeiten schalten (umgekehrte Reihenfolge)
            const speeds = [1000, 500, 200, 100, 0]; // Von langsam zu schnell
            const currentIndex = speeds.indexOf(animationSpeed);
            animationSpeed = speeds[(currentIndex + 1) % speeds.length];
            speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
            updateHamsterTransition(); // Transition aktualisieren
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
    addToConsole('Hamster icon made by Freepik from www.flaticon.com', 'info');

    addToConsole('Pyodide wird geladen...', 'info');
    try {
        console.log('Lade Pyodide...');
        pyodide = await loadPyodide({
            stdout: msg => addToConsole(msg, 'info'),
            stderr: msg => addToConsole(msg, 'error')
        });

        pyodideReady = true;
        addToConsole('Pyodide erfolgreich geladen ✓', 'info');

        // Hamster-Befehle registrieren
        await setupHamsterCommands();

        document.getElementById('runBtn').disabled = false;
        document.getElementById('runBtn').style.opacity = '1';

        // MyPy initialisieren
        await initializeMyPy();

    } catch (error) {
        addToConsole('Fehler beim Laden von Pyodide: ' + error.message, 'error');
        console.error('Pyodide Fehler:', error);
    }
}

// Hamster-Befehle für Pyodide registrieren
async function setupHamsterCommands() {
    console.log("Richte Hamster-Befehle ein...");

    // Python-Code mit integrierten JavaScript-Aufrufen
    const pythonCode = `
import js
import asyncio

# Hamster-Zustandsverfolgung in Python
hamster_running = True

async def vor():
    """Hamster bewegt sich ein Feld vorwärts"""
    if not hamster_running:
        return

    # JavaScript-Funktion direkt aufrufen (async)
    await js.moveHamsterForward()

async def linksUm():
    """Hamster dreht sich nach links"""
    if not hamster_running:
        return

    await js.turnHamsterLeft()

async def nimm():
    """Hamster nimmt ein Korn auf"""
    if not hamster_running:
        return

    await js.pickUpGrain()

async def gib():
    """Hamster legt ein Korn ab"""
    if not hamster_running:
        return

    await js.putDownGrain()

def vornFrei():
    """Prüft ob der Weg vor dem Hamster frei ist"""
    try:
        result = js.isFrontClear()
        return result
    except Exception as e:
        print(f"Fehler bei vornFrei(): {e}")
        return False

def kornDa():
    """Prüft ob an der aktuellen Position ein Korn liegt"""
    try:
        result = js.isGrainAvailable()
        return result
    except Exception as e:
        print(f"Fehler bei kornDa(): {e}")
        return False

def maulLeer():
    """Prüft ob das Hamstermaul leer ist"""
    try:
        result = js.isMouthEmpty()
        return result
    except Exception as e:
        print(f"Fehler bei maulLeer(): {e}")
        return True

def input(prompt=""):
    """Ersetzt die standardmäßige input()-Funktion mit einer Browser-basierten Version"""
    try:
        # Verwende Browser-Prompt für Eingabe
        result = js.getInputFromUser(prompt)
        return result if result is not None else ""
    except Exception as e:
        print(f"Fehler bei input(): {e}")
        return ""

print("Hamster-Befehle erfolgreich registriert: vor, linksUm, nimm, gib, vornFrei, kornDa, maulLeer, input")
`;

    // JavaScript-Funktionen direkt am window-Objekt verfügbar machen (mit Animation)
    window.moveHamsterForward = async function() {
        addToConsole('🐹 vor()', 'info');

        if (!hamsterState.world) {
            throw new Error("Hamster-Welt ist nicht geladen.");
        }

        if (!hamsterState.running) {
            throw new Error("Programm wurde angehalten.");
        }

        const { x, y } = hamsterState.position;
        let newX = x, newY = y;

        switch (hamsterState.direction) {
            case 'east': newX++; break;
            case 'west': newX--; break;
            case 'north': newY--; break;
            case 'south': newY++; break;
        }

        // Kollisionsprüfung
        if (newX < 0 || newX >= hamsterState.world.size.x || newY < 0 || newY >= hamsterState.world.size.y ||
            hamsterState.world.walls.some(wall => wall.x === newX && wall.y === newY)) {
            throw new Error("Hamster kann sich nicht bewegen (Wand oder Grenze erreicht).");
        }

        hamsterState.position = { x: newX, y: newY };
        console.log(`vor() ausgeführt: Hamster bewegte sich zu Position x=${newX}, y=${newY}`);
        updateHamsterDisplay();

        // Warte für Animation/Geschwindigkeit
        if (animationSpeed > 0) {
            await new Promise(resolve => setTimeout(resolve, animationSpeed));
        }
    };

    window.turnHamsterLeft = async function() {
        addToConsole('🐹 linksUm()', 'info');

        if (!hamsterState.world) {
            throw new Error("Hamster-Welt ist nicht geladen.");
        }

        if (!hamsterState.running) {
            throw new Error("Programm wurde angehalten.");
        }

        const directions = ['north', 'west', 'south', 'east'];
        const currentIndex = directions.indexOf(hamsterState.direction);
        hamsterState.direction = directions[(currentIndex + 1) % 4];
        console.log(`linksUm() ausgeführt: Hamster drehte sich nach links, neue Richtung: ${hamsterState.direction}`);
        updateHamsterDisplay();

        // Warte für Animation/Geschwindigkeit
        if (animationSpeed > 0) {
            await new Promise(resolve => setTimeout(resolve, animationSpeed));
        }
    };

    window.pickUpGrain = async function() {
        addToConsole('🐹 nimm()', 'info');

        if (!hamsterState.world) {
            throw new Error("Hamster-Welt ist nicht geladen.");
        }

        if (!hamsterState.running) {
            throw new Error("Programm wurde angehalten.");
        }

        const { x, y } = hamsterState.position;
        const grainLocation = hamsterState.world.grains.find(g => g.x === x && g.y === y);

        if (!grainLocation || grainLocation.count <= 0) {
            throw new Error("Kein Korn zum Aufnehmen vorhanden.");
        }

        grainLocation.count--;
        hamsterState.grains++;
        console.log(`nimm() ausgeführt: Hamster nahm ein Korn auf (Körner im Maul: ${hamsterState.grains})`);
        updateHamsterDisplay();

        // Warte für Animation/Geschwindigkeit
        if (animationSpeed > 0) {
            await new Promise(resolve => setTimeout(resolve, animationSpeed));
        }
    };

    window.putDownGrain = async function() {
        addToConsole('🐹 gib()', 'info');

        if (!hamsterState.world) {
            throw new Error("Hamster-Welt ist nicht geladen.");
        }

        if (!hamsterState.running) {
            throw new Error("Programm wurde angehalten.");
        }

        if (hamsterState.grains <= 0) {
            throw new Error("Hamster hat kein Korn im Maul.");
        }

        const { x, y } = hamsterState.position;
        let grainLocation = hamsterState.world.grains.find(g => g.x === x && g.y === y);

        if (!grainLocation) {
            grainLocation = { x, y, count: 0 };
            hamsterState.world.grains.push(grainLocation);
        }

        grainLocation.count++;
        hamsterState.grains--;
        console.log(`gib() ausgeführt: Hamster legte ein Korn ab (Körner im Maul: ${hamsterState.grains})`);
        updateHamsterDisplay();

        // Warte für Animation/Geschwindigkeit
        if (animationSpeed > 0) {
            await new Promise(resolve => setTimeout(resolve, animationSpeed));
        }
    };

    window.isFrontClear = function() {

        if (!hamsterState.world) return false;

        const { x, y } = hamsterState.position;
        let checkX = x, checkY = y;

        switch (hamsterState.direction) {
            case 'east': checkX++; break;
            case 'west': checkX--; break;
            case 'north': checkY--; break;
            case 'south': checkY++; break;
        }

        const isClear = checkX >= 0 && checkX < hamsterState.world.size.x &&
            checkY >= 0 && checkY < hamsterState.world.size.y &&
            !hamsterState.world.walls.some(wall => wall.x === checkX && wall.y === checkY);

        console.log(`vornFrei() ausgeführt: Position x=${checkX}, y=${checkY} ist ${isClear ? 'frei' : 'blockiert'}`);
        addToConsole(`🐹 vornFrei(): ${isClear ? 'frei' : 'blockiert'}`, 'info');
        return isClear;
    };

    window.isGrainAvailable = function() {

        if (!hamsterState.world) return false;

        const { x, y } = hamsterState.position;
        const grainLocation = hamsterState.world.grains.find(g => g.x === x && g.y === y);
        const hasGrain = grainLocation ? grainLocation.count > 0 : false;
        console.log(`kornDa() ausgeführt: Position x=${x}, y=${y} hat ${hasGrain ? 'Körner' : 'keine Körner'}`);
        addToConsole(`🐹 kornDa(): ${hasGrain ? 'hat Körner' : 'keine Körner'}`, 'info');
        return hasGrain;
    };

    window.isMouthEmpty = function() {

        const isEmpty = hamsterState.grains === 0;
        console.log(`maulLeer() ausgeführt: Maul ist ${isEmpty ? 'leer' : 'nicht leer'} (${hamsterState.grains} Körner)`);
        addToConsole(`🐹 maulLeer(): ${isEmpty ? 'leer' : 'nicht leer'} (${hamsterState.grains} Körner)`, 'info');
        return isEmpty;
    };

    window.getInputFromUser = function(prompt) {
        // Verwende Browser-Prompt für Benutzereingabe
        const userInput = window.prompt(prompt || "Eingabe:");

        if (userInput !== null) {
            addToConsole(`💬 input("${prompt}"): "${userInput}"`, 'info');
            console.log(`input() ausgeführt: Prompt="${prompt}", Eingabe="${userInput}"`);
            return userInput;
        } else {
            // Benutzer hat Cancel gedrückt
            addToConsole(`💬 input("${prompt}"): (abgebrochen)`, 'info');
            console.log(`input() ausgeführt: Prompt="${prompt}", Eingabe abgebrochen`);
            return "";
        }
    };

    try {
        await pyodide.runPythonAsync(pythonCode);
        console.log("Hamster-Befehle erfolgreich registriert und JavaScript-Funktionen verknüpft");
    } catch (e) {
        console.error("Fehler beim Registrieren der Hamster-Befehle:", e);
        addToConsole("Fehler beim Registrieren der Hamster-Befehle.", "error");
    }
}

// Hamster-Programm stoppen
function stopHamsterProgram() {
    hamsterState.running = false;
    addToConsole('⏹ Programm wurde angehalten', 'warning');

    // Button auf "Zurücksetzen" setzen
    updateRunButton('reset');
}

// Hamster-Welt zurücksetzen
function resetHamsterWorld() {
    if (!hamsterState.world) return;

    // Console-Output löschen
    clearConsoleOutput();

    addToConsole('🔄 Setze Hamster-Welt zurück...', 'info');

    // Lade nur die Daten der aktuellen Konfiguration ohne DOM-Neuerstellung
    resetConfigurationData(currentConfigIndex);

    // Button wieder auf "Ausführen" setzen
    updateRunButton('execute');
}

// Python-Code ausführen
async function runPythonCode() {
    if (!pyodideReady) {
        addToConsole('Python wird noch geladen...', 'warning');
        return;
    }

    const code = pythonEditor.getValue();
    if (!code.trim()) {
        addToConsole('Kein Code zum Ausführen.', 'warning');
        return;
    }

    try {
        // Console Output löschen
        clearConsoleOutput();

        // Hamster-Simulation starten
        hamsterState.running = true;

        // Button zu "Stop" ändern
        updateRunButton('running');

        addToConsole('🐹 Starte Hamster-Simulation...', 'info');

        // Python-Code in async Umgebung ausführen und automatisch await für Hamster-Befehle hinzufügen
        const processedCode = code.split('\n').map(line => {
            if (line.trim()) {
                // Automatisch await für Hamster-Befehle hinzufügen
                let processedLine = line;
                const hamsterCommands = ['vor()', 'linksUm()', 'nimm()', 'gib()'];
                hamsterCommands.forEach(cmd => {
                    const regex = new RegExp(`\\b${cmd.replace('()', '\\(\\)')}`, 'g');
                    if (processedLine.match(regex) && !processedLine.includes('await')) {
                        processedLine = processedLine.replace(regex, `await ${cmd}`);
                    }
                });
                return '    ' + processedLine;
            }
            return '';
        }).join('\n');

        const wrappedCode = `
import asyncio

async def main():
${processedCode}

# Führe das Hauptprogramm aus
await main()
        `;

        await pyodide.runPythonAsync(wrappedCode);

        if (hamsterState.running) {
            addToConsole('✅ Hamster-Programm erfolgreich ausgeführt!', 'info');
            addToConsole(`🌾 Körner im Maul: ${hamsterState.grains}`, 'info');
        }

        // Button zu "Zurücksetzen" ändern
        updateRunButton('reset');

    } catch (error) {
        // Prüfe ob es sich um einen Stop-Befehl handelt
        const isStopError = error.message && error.message.includes('Programm wurde angehalten');

        if (isStopError) {
            // Keine weitere Fehlermeldung bei Stop
            updateRunButton('reset');
        } else {
            // Prüfe ob es sich um einen anderen Hamster-spezifischen Fehler handelt
            const isHamsterError = error.message && (
                error.message.includes('Hamster kann sich nicht bewegen') ||
                error.message.includes('Kein Korn zum Aufnehmen vorhanden') ||
                error.message.includes('Hamster hat kein Korn im Maul') ||
                error.message.includes('Hamster-Welt ist nicht geladen') ||
                error.message.includes('Wand oder Grenze erreicht') ||
                error.message.includes('pyodide.ffi.JsException: Error: Kein Korn zum Aufnehmen vorhanden') ||
                error.message.includes('pyodide.ffi.JsException: Error: Hamster kann sich nicht bewegen') ||
                error.message.includes('pyodide.ffi.JsException: Error: Hamster hat kein Korn im Maul') ||
                error.message.includes('pyodide.ffi.JsException: Error:')
            );

            if (isHamsterError) {
                // Extrahiere nur die eigentliche Fehlermeldung aus pyodide.ffi.JsException
                let cleanMessage = error.message;
                if (error.message.includes('pyodide.ffi.JsException: Error:')) {
                    const match = error.message.match(/pyodide\.ffi\.JsException: Error: (.+?)(?:\n|$)/);
                    if (match) {
                        cleanMessage = match[1];
                    }
                }
                addToConsole('❌ ' + cleanMessage, 'error');
            } else {
                // Für andere Fehler den vollständigen Traceback anzeigen
                addToConsole('❌ Fehler beim Ausführen des Hamster-Programms:', 'error');
                addToConsole(error.message, 'error');
            }
            console.error('Hamster Fehler:', error);

            // Auch bei Fehlern Button auf "Zurücksetzen" setzen, da sich der Zustand geändert haben könnte
            updateRunButton('reset');
        }
    } finally {
        hamsterState.running = false;
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

        // Dummy-Hamster-Funktionen für Type-Checking hinzufügen
        const hamsterDummyFunctions = `
# Dummy Hamster-Funktionen für Type-Checking
from typing import Union

def vor() -> None:
    """Hamster bewegt sich ein Feld vorwärts"""
    pass

def linksUm() -> None:
    """Hamster dreht sich nach links"""
    pass

def nimm() -> None:
    """Hamster nimmt ein Korn auf"""
    pass

def gib() -> None:
    """Hamster legt ein Korn ab"""
    pass

def vornFrei() -> bool:
    """Prüft ob der Weg vor dem Hamster frei ist"""
    return True

def kornDa() -> bool:
    """Prüft ob an der aktuellen Position ein Korn liegt"""
    return False

def maulLeer() -> bool:
    """Prüft ob das Hamstermaul leer ist"""
    return True

# Benutzercode:
`;

        // Code mit Dummy-Funktionen für Type-Checking kombinieren
        const codeForTypeChecking = hamsterDummyFunctions + currentCode;

        // MyPy ausführen
        const errors = pyodide.runPython(`
errors = type_checker.check_code(r'''${codeForTypeChecking.replace(/'/g, "\\'")}''')
# Zeilennummern um die Anzahl der Dummy-Zeilen korrigieren
dummy_lines = ${hamsterDummyFunctions.split('\n').length}
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

// Hamster-Konfiguration initialisieren
function initializeHamsterConfig() {
    try {
        const configElement = document.getElementById('hamsterConfig');
        if (configElement && configElement.textContent.trim()) {
            const configData = JSON.parse(configElement.textContent);

            if (configData.configurations && configData.configurations.length > 0) {
                // Konvertiere Konfigurationen
                worldConfigurations.length = 0; // Array leeren
                configData.configurations.forEach((config, index) => {
                    const worldConfig = parseHamsterConfig(config);
                    worldConfigurations.push(worldConfig);
                });

                // Erste Konfiguration laden
                loadConfiguration(0);
                updateConfigSelector();

                // Standard-Code setzen wenn vorhanden
                if (configData.defaultContent && !pythonEditor.getValue().trim()) {
                    pythonEditor.setValue(configData.defaultContent);
                }
            }
        } else {
            // Fallback: Lade Standardkonfiguration wenn keine vorhanden
            console.log('Keine Hamster-Konfiguration gefunden, lade Standardkonfiguration');
            loadConfiguration(0);
        }

        // Hamster als aktiv markieren
        hamsterState.running = false; // Wird bei Code-Ausführung auf true gesetzt

    } catch (error) {
        console.error('Fehler beim Laden der Hamster-Konfiguration:', error);
        // Fallback: Lade Standardkonfiguration
        loadConfiguration(0);
    }
}

// Hamster-Konfiguration parsen
function parseHamsterConfig(config) {
    const worldConfig = {
        name: config.name || 'Unbekannt',
        size: { x: 0, y: 0 },
        walls: [],
        grains: [],
        hamsterStart: {
            x: config.hamsterX || 0,
            y: config.hamsterY || 0,
            direction: config.hamsterDirection || 'east'
        }
    };

    // Territory parsen
    if (config.territory && config.territory.length > 0) {
        worldConfig.size.y = config.territory.length;
        worldConfig.size.x = config.territory[0].split('|').length;

        config.territory.forEach((row, y) => {
            const cells = row.split('|').map(cell => cell.trim());
            cells.forEach((cell, x) => {
                if (cell === 'w') {
                    worldConfig.walls.push({ x, y });
                } else if (!isNaN(parseInt(cell)) && parseInt(cell) > 0) {
                    worldConfig.grains.push({ x, y, count: parseInt(cell) });
                }
            });
        });
    }

    return worldConfig;
}

// Konfiguration laden (mit DOM-Neuerstellung)
function loadConfiguration(index) {
    // Index-Validierung: Falls ungültig, verwende Index 0 (erste Welt)
    if (index < 0 || index >= worldConfigurations.length || worldConfigurations.length === 0) {
        console.warn(`Ungültiger Weltkonfiguration-Index: ${index}. Verwende Index 0 als Fallback.`);
        index = 0;
        currentConfigIndex = 0;
    }

    // Stelle sicher, dass mindestens eine Konfiguration existiert
    if (worldConfigurations.length === 0) {
        console.error('Keine Weltkonfigurationen verfügbar. Erstelle Standard-Fallback-Welt.');
        // Erstelle eine Minimal-Fallback-Konfiguration
        worldConfigurations.push({
            name: 'Standard-Welt',
            size: { x: 3, y: 3 },
            walls: [],
            grains: [],
            hamsterStart: { x: 0, y: 0, direction: 'east' }
        });
    }

    const config = worldConfigurations[index];
    hamsterState.world = {
        size: { ...config.size },
        walls: [...config.walls],
        grains: config.grains.map(g => ({ ...g }))
    };
    hamsterState.position = { ...config.hamsterStart };
    hamsterState.direction = config.hamsterStart.direction;
    hamsterState.grains = 0;
    hamsterState.running = false;

    // Aktualisiere den aktuellen Index
    currentConfigIndex = index;

    // Erstelle neue Territorium-Struktur nur bei Konfigurationswechsel
    createTerritoryStructure();

    // Hamster-Display ohne Animation aktualisieren (Konfigurationswechsel)
    updateHamsterDisplay(false);

    console.log(`Weltkonfiguration ${index + 1} geladen.`);

    // Button auf "Ausführen" zurücksetzen
    updateRunButton('execute');
}

// Konfigurationsdaten zurücksetzen (ohne DOM-Neuerstellung)
function resetConfigurationData(index) {
    // Index-Validierung: Falls ungültig, verwende Index 0 (erste Welt)
    if (index < 0 || index >= worldConfigurations.length || worldConfigurations.length === 0) {
        console.warn(`Ungültiger Weltkonfiguration-Index beim Reset: ${index}. Verwende Index 0 als Fallback.`);
        index = 0;
        currentConfigIndex = 0;
    }

    // Stelle sicher, dass mindestens eine Konfiguration existiert
    if (worldConfigurations.length === 0) {
        console.error('Keine Weltkonfigurationen beim Reset verfügbar.');
        return;
    }

    const config = worldConfigurations[index];
    hamsterState.world = {
        size: { ...config.size },
        walls: [...config.walls],
        grains: config.grains.map(g => ({ ...g }))
    };
    hamsterState.position = { ...config.hamsterStart };
    hamsterState.direction = config.hamsterStart.direction;
    hamsterState.grains = 0;
    hamsterState.running = false;

    // Aktualisiere den aktuellen Index
    currentConfigIndex = index;

    // Hamster-Display ohne Animation aktualisieren (Reset)
    updateHamsterDisplay(false);
    console.log(`Weltkonfiguration ${index + 1} Daten zurückgesetzt.`);
}

// Konfigurationsselektor aktualisieren
function updateConfigSelector() {
    const configSelector = document.getElementById('configSelector');
    if (configSelector) {
        configSelector.innerHTML = `<i class="fas fa-map"></i> ${currentConfigIndex + 1}/${worldConfigurations.length}`;

        configSelector.onclick = () => {
            currentConfigIndex = (currentConfigIndex + 1) % worldConfigurations.length;
            loadConfiguration(currentConfigIndex);
            updateConfigSelector();
        };
    }
}

// CSS-Transition für Hamster aktualisieren
function updateHamsterTransition() {
    const hamsterSprite = document.getElementById('hamster-sprite');
    if (hamsterSprite) {
        if (animationSpeed > 0) {
            hamsterSprite.style.transition = `left ${animationSpeed}ms ease, top ${animationSpeed}ms ease, transform ${animationSpeed}ms ease`;
        } else {
            hamsterSprite.style.transition = 'none';
        }
    }
}

// Territorium einmalig erstellen (wird nur bei Konfigurationswechsel aufgerufen)
function createTerritoryStructure() {
    const territoryDiv = document.getElementById('hamsterTerritory');
    if (!territoryDiv || !hamsterState.world) return;

    // Leere das Territory nur wenn es neu erstellt wird
    territoryDiv.innerHTML = '';

    const { size, walls } = hamsterState.world;

    // Hilfsfunktion für zufällige Wandvariantenauswahl
    function getRandomWallVariant(type) {
        const variants = {
            corner: ['wall-corner-variant1', 'wall-corner-variant2', 'wall-corner-variant3'],
            side: ['wall-side-variant1', 'wall-side-variant2'],
            top_bottom: ['wall-top-bottom-variant1', 'wall-top-bottom-variant2', 'wall-top-bottom-variant3']
        };
        const variantArray = variants[type];
        const selectedVariant = variantArray[Math.floor(Math.random() * variantArray.length)];
        return `var(--hamster-${selectedVariant})`;
    }

    // Erstelle Container für das gesamte Territorium mit Wänden
    const territoryContainer = document.createElement('div');
    territoryContainer.className = 'territory-container';
    territoryContainer.style.display = 'grid';
    territoryContainer.style.gridTemplateColumns = `17px repeat(${size.x}, 40px) 17px`;
    territoryContainer.style.gridTemplateRows = `14px repeat(${size.y}, 40px) 14px`;
    territoryContainer.style.gap = '0';
    territoryContainer.style.position = 'relative';

    // Erstelle alle Zellen einschließlich Randwände
    for (let y = -1; y <= size.y; y++) {
        for (let x = -1; x <= size.x; x++) {
            const cell = document.createElement('div');

            // Bestimme Zelltyp und Größe
            if ((x === -1 || x === size.x) && (y === -1 || y === size.y)) {
                // Eckzellen
                cell.className = 'territory-border-corner';
                cell.style.backgroundImage = getRandomWallVariant('corner');
            } else if (x === -1 || x === size.x) {
                // Seitliche Randwände
                cell.className = 'territory-border-side';
                cell.style.backgroundImage = getRandomWallVariant('side');
            } else if (y === -1 || y === size.y) {
                // Obere/untere Randwände
                cell.className = 'territory-border-top-bottom';
                cell.style.backgroundImage = getRandomWallVariant('top_bottom');
            } else {
                // Spielfeld-Zellen
                cell.className = 'territory-cell';
                cell.setAttribute('data-x', x);
                cell.setAttribute('data-y', y);

                // Prüfe ob Wand im Spielfeld
                const isWall = walls.some(wall => wall.x === x && wall.y === y);
                if (isWall) {
                    cell.style.backgroundImage = 'var(--hamster-wall)';
                    cell.style.backgroundSize = 'cover';
                    cell.style.backgroundPosition = 'center';
                }

                // Erstelle Körner-Container für jede Zelle (auch wenn aktuell leer)
                const cornSprite = document.createElement('div');
                cornSprite.className = 'corn-sprite';
                cornSprite.id = `corn-${x}-${y}`;
                cornSprite.style.display = 'none'; // Initial versteckt
                cell.appendChild(cornSprite);
            }

            // Füge Zelle zum Container hinzu
            territoryContainer.appendChild(cell);
        }
    }

    // Erstelle Hamster-Sprite separat als absolut positioniertes Element
    const hamsterSprite = document.createElement('div');
    hamsterSprite.className = 'hamster-sprite';
    hamsterSprite.id = 'hamster-sprite';
    hamsterSprite.style.backgroundImage = 'var(--hamster-hamster)';
    hamsterSprite.style.position = 'absolute';
    hamsterSprite.style.width = '40px';
    hamsterSprite.style.height = '40px';
    hamsterSprite.style.backgroundSize = '80%';
    hamsterSprite.style.backgroundPosition = 'center';
    hamsterSprite.style.backgroundRepeat = 'no-repeat';
    hamsterSprite.style.zIndex = '10';
    hamsterSprite.style.opacity = '0.9';
    territoryContainer.appendChild(hamsterSprite);

    territoryDiv.appendChild(territoryContainer);

    // Initial Körner und Hamster positionieren (ohne Animation)
    updateHamsterDisplay(false);

    // Zoom-Berechnung auch bei Fenstergröße-Änderungen
    const resizeObserver = new ResizeObserver(() => {
        updateHamsterDisplay(false); // Keine Animation bei Resize
    });
    resizeObserver.observe(territoryDiv);
}

// Hamster-Display aktualisieren (nur Position, Rotation und Körner)
function updateHamsterDisplay(enableAnimation = true) {
    const territoryDiv = document.getElementById('hamsterTerritory');
    if (!territoryDiv || !hamsterState.world) return;

    const { size, grains } = hamsterState.world;

    // Automatische Zoom-Berechnung für Territory-Container
    const territoryContainer = territoryDiv.querySelector('.territory-container');
    if (territoryContainer) {
        // Berechne die natürliche Größe des Territory-Containers
        const borderWidth = 17; // Seitliche Randbreite
        const borderHeight = 14; // Obere/untere Randbreite
        const cellSize = 40; // Größe einer Spielfeld-Zelle

        const naturalWidth = (borderWidth * 2) + (size.x * cellSize);
        const naturalHeight = (borderHeight * 2) + (size.y * cellSize);

        // Berechne verfügbaren Platz im hamsterTerritory Div
        const availableWidth = territoryDiv.clientWidth - 20; // 10px Padding links und rechts
        const availableHeight = territoryDiv.clientHeight - 20; // 10px Padding oben und unten

        // Berechne Zoom-Faktoren für beide Richtungen
        const zoomFactorX = availableWidth / naturalWidth;
        const zoomFactorY = availableHeight / naturalHeight;

        // Verwende das Minimum der beiden Faktoren, aber maximal 1.0 (nicht größer als natürliche Größe)
        const optimalZoom = Math.min(zoomFactorX, zoomFactorY, 1.0);

        // Nur zoomen wenn nötig (wenn Container zu groß ist)
        if (optimalZoom < 1.0) {
            territoryContainer.style.zoom = optimalZoom.toString();
        } else {
            territoryContainer.style.zoom = '1';
        }

        console.log(`Territory-Zoom berechnet: verfügbar=${availableWidth}x${availableHeight}, natürlich=${naturalWidth}x${naturalHeight}, zoom=${optimalZoom.toFixed(3)}`);
    }

    // Aktualisiere alle Körner-Anzeigen
    for (let y = 0; y < size.y; y++) {
        for (let x = 0; x < size.x; x++) {
            const cornSprite = document.getElementById(`corn-${x}-${y}`);
            if (cornSprite) {
                const grainLocation = grains.find(g => g.x === x && g.y === y);
                if (grainLocation && grainLocation.count > 0) {
                    const cornVariant = grainLocation.count >= 4 ? 'corn4' : `corn${grainLocation.count}`;
                    cornSprite.style.backgroundImage = `var(--hamster-${cornVariant})`;
                    cornSprite.style.display = 'block';
                } else {
                    cornSprite.style.display = 'none';
                }
            }
        }
    }

    // Aktualisiere Hamster-Position und -Rotation
    const hamsterSprite = document.getElementById('hamster-sprite');
    if (hamsterSprite) {
        // Stelle sicher, dass Transition korrekt gesetzt ist
        if (enableAnimation && animationSpeed > 0) {
            hamsterSprite.style.transition = `left ${animationSpeed}ms ease, top ${animationSpeed}ms ease, transform ${animationSpeed}ms ease`;
        } else {
            hamsterSprite.style.transition = 'none';
        }

        // Berechne Position basierend auf Grid
        const cellSize = 40;
        const borderSize = 17;
        const topBorderSize = 14;

        const leftPos = borderSize + (hamsterState.position.x * cellSize);
        const topPos = topBorderSize + (hamsterState.position.y * cellSize);

        hamsterSprite.style.left = leftPos + 'px';
        hamsterSprite.style.top = topPos + 'px';

        if (enableAnimation) {
            // Spezielle Rotationslogik für south->east Übergang
            if (hamsterState.direction === 'east' && hamsterSprite.style.transform === 'rotate(0deg)') {
                // Erste Rotation auf -90° mit Animation (von south nach east)
                hamsterSprite.style.transform = 'rotate(-90deg)';

                // Nach Animation auf 270° ohne Animation wechseln
                setTimeout(() => {
                    // Animation temporär deaktivieren
                    const originalTransition = hamsterSprite.style.transition;
                    hamsterSprite.style.transition = 'none';

                    // Auf 270° setzen (visuell identisch mit -90°)
                    hamsterSprite.style.transform = 'rotate(270deg)';

                    // Animation nach kurzer Pause wieder aktivieren
                    setTimeout(() => {
                        hamsterSprite.style.transition = originalTransition;
                    }, 10);
                }, animationSpeed > 0 ? animationSpeed : 50);
            } else {
                // Normale Rotation für andere Richtungen
                const rotations = {
                    south: 0,
                    west: 90,
                    north: 180,
                    east: 270
                };
                hamsterSprite.style.transform = `rotate(${rotations[hamsterState.direction] || 0}deg)`;
            }
        } else {
            // Ohne Animation: Direkte Rotation ohne spezielle Übergangslogik
            const rotations = {
                south: 0,
                west: 90,
                north: 180,
                east: 270
            };
            hamsterSprite.style.transform = `rotate(${rotations[hamsterState.direction] || 0}deg)`;
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
        currentConfigIndex: currentConfigIndex, // Speichere auch die aktuelle Weltkonfiguration
        animationSpeed: animationSpeed, // Speichere auch die Geschwindigkeit
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

        if (data.currentTutorialIndex !== undefined && tutorialContents && data.currentTutorialIndex >= 0 && data.currentTutorialIndex < tutorialContents.length) {
            currentTutorialIndex = data.currentTutorialIndex;
            updateTutorialDisplay();
        }

        // Lade die gespeicherte Weltkonfiguration mit Validierung
        if (data.currentConfigIndex !== undefined) {
            // Validiere den gespeicherten Index
            if (data.currentConfigIndex >= 0 && data.currentConfigIndex < worldConfigurations.length) {
                currentConfigIndex = data.currentConfigIndex;
            } else {
                console.warn(`Gespeicherter Index ${data.currentConfigIndex} ist ungültig. Verwende Index 0.`);
                currentConfigIndex = 0;
            }

            const configSelect = document.getElementById('worldConfigSelect');
            if (configSelect) {
                configSelect.value = currentConfigIndex;
            }
            loadConfiguration(currentConfigIndex);
            updateConfigSelector(); // Anzeige aktualisieren
        } else {
            // Wenn keine Konfiguration gespeichert ist, lade die erste als Standard
            currentConfigIndex = 0;
            loadConfiguration(currentConfigIndex);
            updateConfigSelector(); // Anzeige aktualisieren
        }

        // Lade die gespeicherte Geschwindigkeit
        if (data.animationSpeed !== undefined) {
            animationSpeed = data.animationSpeed;
            const speedSelector = document.getElementById('speedSelector');
            if (speedSelector) {
                speedSelector.innerHTML = `<i class="fas fa-tachometer-alt"></i> ${animationSpeed}ms`;
            }
            updateHamsterTransition(); // Transition aktualisieren
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
    const url = urlElement ? urlElement.getAttribute('data-url') : '';

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
    loadContentToView: loadContentToView,
    // Hamster API
    get_grains: () => hamsterState.grains,
    get_direction: () => hamsterState.direction,
    get_position: () => ({ ...hamsterState.position }),
    load_configuration: loadConfiguration,
    set_current_config: (index) => {
        if (index >= 0 && index < worldConfigurations.length) {
            currentConfigIndex = index;
            loadConfiguration(currentConfigIndex);
        }
    }
};