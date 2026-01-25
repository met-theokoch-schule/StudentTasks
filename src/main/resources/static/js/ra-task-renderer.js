// RA Task Renderer
// Rendert die Aufgaben-Liste mit CodeMirror-Editoren für Relationale Algebra

// Track letzten aktiven Editor
let lastActiveEditor = null;

function setupEditorTracking(editor, taskId) {
    editor.on("focus", () => {
        lastActiveEditor = editor;
        console.log(`📝 Active editor switched to task ${taskId}`);
    });
}

// Aufgaben rendern
function renderTasks(taskData) {
    const container = document.getElementById("tasks-container");

    if (!taskData || !taskData.tasks || taskData.tasks.length === 0) {
        container.innerHTML =
            '<div class="text-center text-muted p-4">Keine Aufgaben verfügbar.</div>';
        return;
    }

    container.innerHTML = "";

    taskData.tasks.forEach((task, index) => {
        const taskCard = createTaskCard(task, index);
        container.appendChild(taskCard);
    });

    console.log(`✅ Rendered ${taskData.tasks.length} task(s)`);
}

// Task-Card erstellen
function createTaskCard(task, index) {
    const card = document.createElement("div");
    card.className = "task-card";
    card.id = `task-card-${task.id}`;

    // Header mit Status-Icon und Titel
    const header = document.createElement("div");
    header.className = "task-header";

    const statusIcon = document.createElement("span");
    statusIcon.className = "task-status-icon status-not-attempted";
    statusIcon.id = `task-icon-${task.id}`;
    statusIcon.innerHTML = "⭕";

    const title = document.createElement("div");
    title.className = "task-title";
    title.textContent = task.title || `Aufgabe ${index + 1}`;

    header.appendChild(statusIcon);
    header.appendChild(title);
    card.appendChild(header);

    // Beschreibung (Markdown)
    if (task.description) {
        const description = document.createElement("div");
        description.className = "task-description";
        try {
            let markdown = processSpoilersMarkdown(task.description);
            let html = marked.parse(markdown);
            description.innerHTML = html;
        } catch (e) {
            console.error("Error rendering task description:", e);
            description.textContent = task.description;
        }
        card.appendChild(description);
    }

    // Editor mit Symbol-Buttons
    const editorContainer = document.createElement("div");
    editorContainer.className = "task-editor";

    // Symbol-Buttons über dem Editor
    const symbolButtonsContainer = createSymbolButtons(task);
    editorContainer.appendChild(symbolButtonsContainer);

    const textarea = document.createElement("textarea");
    textarea.id = `editor-${task.id}`;
    textarea.value = task.defaultCode || "";

    editorContainer.appendChild(textarea);
    card.appendChild(editorContainer);

    // Buttons
    const actions = document.createElement("div");
    actions.className = "task-actions";

    const executeBtn = document.createElement("button");
    executeBtn.className = "btn btn-execute";
    executeBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
    executeBtn.onclick = () => executeRA(task.id);

    actions.appendChild(executeBtn);

    // "Zurücksetzen" Button nur wenn defaultCode vorhanden
    if (task.defaultCode) {
        const resetBtn = document.createElement("button");
        resetBtn.className = "btn btn-reset";
        resetBtn.innerHTML = '<i class="fas fa-undo"></i> Zurücksetzen';
        resetBtn.onclick = () => resetTaskCode(task.id, task.defaultCode);
        actions.appendChild(resetBtn);
    }

    card.appendChild(actions);

    console.log(
        `🔧 Created ${task.defaultCode ? 2 : 1} button(s) for task ${task.id}`,
    );

    // CodeMirror initialisieren (nach DOM-Einfügen)
    setTimeout(() => {
        console.log(`🏗️ Initializing editor for task ${task.id}`);
        const editor = initializeEditor(task.id, textarea);
        
        // Prüfen, ob für diesen Task bereits gespeicherter Content vorliegt
        if (typeof taskStatus !== 'undefined' && taskStatus.tasks) {
            const savedTask = taskStatus.tasks.find(t => t.id === task.id);
            if (savedTask && savedTask.code !== undefined && savedTask.code !== null) {
                console.log(`✨ Setting saved code for editor ${task.id}: "${savedTask.code}"`);
                editor.setValue(savedTask.code);
            } else {
                console.log(`ℹ️ No saved code for editor ${task.id}`);
            }
        }
    }, 0);

    return card;
}

// Symbol-Buttons erstellen
function createSymbolButtons(task) {
    const container = document.createElement("div");
    container.className = "symbol-buttons-container";

    // Standard-Operatoren (immer sichtbar)
    const standardOps = [
        {
            symbol: "π",
            label: "Projektion",
            text: "pi",
            tooltip: "Projektion\nπ a, b ( A )\npi a, b A",
        },
        {
            symbol: "σ",
            label: "Selektion",
            text: "sigma",
            tooltip:
                "Selektion\nσ a < b ∧ b ≠ c ( A )\nsigma a < b and b != c A",
        },
        {
            symbol: "⨯",
            label: "Kreuzprodukt",
            text: "cross",
            tooltip: "Cross Join (Kreuzprodukt)\n( A ) ⨯ ( B )",
        },
        {
            symbol: "⋈",
            label: "Join",
            text: "join",
            tooltip: "Natural Join / θ-Join\n( A ) ⋈ ( B )",
        },
        {
            symbol: "∧",
            label: "Und",
            text: "and",
            tooltip: "Und\nσ a < b ∧ b ≠ c ( A )",
        },
        {
            symbol: "∨",
            label: "Oder",
            text: "or",
            tooltip: "Oder\nσ a < b ∨ b ≠ c ( A )",
        },
        {
            symbol: "¬",
            label: "Verneinung",
            text: "not",
            tooltip: "Verneinung\n¬(a < b) ( A )",
        },
        {
            symbol: "=",
            label: "Gleich",
            text: "=",
            tooltip: "Gleich\na = b ( A )",
        },
        {
            symbol: "≠",
            label: "Ungleich",
            text: "!=",
            tooltip: "Ungleich\na ≠ 'text' ( A )",
        },
        {
            symbol: "≥",
            label: "Größer-Gleich",
            text: ">=",
            tooltip: "Größer-Gleich\na ≥ 42 ( A )",
        },
        {
            symbol: "≤",
            label: "Kleiner-Gleich",
            text: "<=",
            tooltip: "Kleiner-Gleich\na ≤ 42 ( A )",
        },
    ];

    // Zusätz-Operatoren (nur wenn showLKOperations true)
    const lkOps = [
        {
            symbol: "ρ",
            label: "Umbenennen",
            text: "rho",
            tooltip: "Umbenennen (Relationen/Spalten)\nσ x.a > 1 ( ρ x ( A ) )",
        },
        {
            symbol: "←",
            label: "Spalte umbenennen (alt)",
            text: "<-",
            tooltip: "Umbenennung von Spalten\nσ A.y > 2 ( ρ y←a ( A ) )",
        },
        {
            symbol: "→",
            label: "Spalte umbenennen (neu)",
            text: "->",
            tooltip:
                "Umbenennung von Spalten (neuer Name rechts)\nσ A.y > 2 ( ρ a→y ( A ) )",
        },
        {
            symbol: "∩",
            label: "Schnittmenge",
            text: "intersect",
            tooltip: "Schnittmenge\n( A ) ∩ ( B )",
        },
        {
            symbol: "∪",
            label: "Vereinigung",
            text: "union",
            tooltip: "Vereinigung\n( A ) ∪ ( B )",
        },
        {
            symbol: "-",
            label: "Differenz",
            text: "minus",
            tooltip: "Mengendifferenz\n( A ) - ( B )",
        },
    ];

    const allOps = task.showLKOperations
        ? [...standardOps, ...lkOps]
        : standardOps;

    allOps.forEach((op) => {
        const btn = document.createElement("button");
        btn.className = "symbol-button";
        btn.textContent = op.symbol;
        btn.title = op.tooltip;
        btn.type = "button";
        btn.onclick = (e) => {
            e.preventDefault();
            insertSymbolAtCursor(task.id, op.symbol);
        };

        // Tooltip hinzufügen
        const tooltipSpan = document.createElement("span");
        tooltipSpan.className = "symbol-tooltip";
        const lines = op.tooltip.split("\n");
        if (lines.length > 1) {
            // Erste Zeile als Überschrift
            const titleDiv = document.createElement("div");
            titleDiv.className = "symbol-tooltip-title";
            titleDiv.textContent = lines[0];
            tooltipSpan.appendChild(titleDiv);

            // Rest als Content
            const contentDiv = document.createElement("div");
            contentDiv.className = "symbol-tooltip-content";
            contentDiv.innerHTML = lines.slice(1).join("<br>");
            tooltipSpan.appendChild(contentDiv);
        } else {
            tooltipSpan.textContent = op.tooltip;
        }
        btn.appendChild(tooltipSpan);

        container.appendChild(btn);
    });

    return container;
}

// Symbol an Cursor-Position einfügen
function insertSymbolAtCursor(taskId, symbol) {
    const editor = editors[taskId];
    if (!editor) return;

    const cursor = editor.getCursor();
    editor.replaceRange(symbol, cursor);
    editor.setCursor({ line: cursor.line, ch: cursor.ch + symbol.length });
    editor.focus();
    markDirty();
}

// CodeMirror-Editor initialisieren
function initializeEditor(taskId, textarea) {
    try {
        const editor = CodeMirror.fromTextArea(textarea, {
            mode: "text/plain",
            lineNumbers: true,
            matchBrackets: true,
            indentWithTabs: true,
            smartIndent: true,
            theme: "default",
            extraKeys: {
                "Ctrl-Enter": () => executeRA(taskId),
                "Cmd-Enter": () => executeRA(taskId),
            },
        });

        // Editor speichern
        editors[taskId] = editor;

        // Change-Event - show dirty status
        editor.on("change", () => {
            markDirty();
        });

        // Track editor für Schema Click-to-Insert
        setupEditorTracking(editor, taskId);

        console.log(`✅ Editor initialized for task ${taskId}`);
        return editor;
    } catch (e) {
        console.error(`Failed to initialize editor for task ${taskId}:`, e);
        return null;
    }
}

// Task-Code zurücksetzen
function resetTaskCode(taskId, defaultCode) {
    const editor = editors[taskId];
    if (!editor) {
        console.error("Editor not found for task:", taskId);
        return;
    }

    if (
        confirm(
            "Möchten Sie den Code wirklich auf den Standardwert zurücksetzen?",
        )
    ) {
        editor.setValue(defaultCode || "");
        console.log(`🔄 Reset code for task ${taskId}`);
        markDirty();
    }
}

// RA-Ausdruck ausführen
function executeRA(taskId) {
    console.log(`▶️ Executing RA for task ${taskId}`);

    // Task-Daten finden
    const task = taskData.tasks.find((t) => t.id === taskId);
    if (!task) {
        console.error("Task not found:", taskId);
        return;
    }

    const editor = editors[taskId];
    if (!editor) {
        console.error("Editor not found for task:", taskId);
        return;
    }

    const raQuery = editor.getValue().trim();

    if (!raQuery) {
        showError("Bitte geben Sie einen RA-Ausdruck ein.");
        return;
    }

    // Zur Ausgabe wechseln
    switchToOutputTab();

    // Ausgabe-Elemente vorbereiten
    const outputInfo = document.getElementById("output-info");
    const raOutput = document.getElementById("ra-output");
    const errorDisplay = document.getElementById("error-display");
    const resultContainer = document.getElementById("result-tables");

    outputInfo.style.display = "none";
    raOutput.style.display = "block";
    errorDisplay.style.display = "none";
    resultContainer.innerHTML =
        '<div class="text-muted"><i class="fas fa-spinner fa-spin"></i> Führe RA aus...</div>';

    try {
        console.log("🔍 Executing RA query:", raQuery);

        // Leeren für neue Ausführung
        resultContainer.innerHTML = "";

        // RelaxEngine ausführen (mit Paginierung für Anzeige)
        RelaxEngine.execute(raQuery, resultContainer, {
            showTree: true,
            showFormula: true,
            maxRowsPerPage: 5,
        });

        // Status update
        updateTaskStatus(taskId, "not_attempted");

        // Validierung wenn Musterlösung vorhanden
        if (task.solutionCode && task.validation) {
            validateRASolution(taskId, raQuery, task);
        }
    } catch (e) {
        console.error("RA execution error:", e);
        showError(`RA-Fehler: ${e.message}`);
        updateTaskStatus(taskId, "incorrect");
        return;
    }
}

// Spaltennamen normalisieren (entfernt Tabellenpräfix wie "S.Note" → "Note")
function normalizeColumnName(name) {
    if (!name) return "";
    return name.includes(".") ? name.split(".").pop() : name;
}

// Tabellen aus Container extrahieren
function extractTableFromContainer(container) {
    if (!container) return null;
    
    const table = container.querySelector("table");
    if (!table) return null;

    const headers = [];
    table.querySelectorAll("thead th").forEach((th) => {
        headers.push(normalizeColumnName(th.textContent.trim()));
    });

    const rows = [];
    table.querySelectorAll("tbody tr").forEach((tr) => {
        const row = [];
        tr.querySelectorAll("td").forEach((td) => {
            row.push(td.textContent.trim());
        });
        if (row.length > 0) {
            rows.push(row);
        }
    });

    return { headers, rows };
}

// Zwei Tabellen vergleichen (Spaltenreihenfolge und Namen ignorieren)
function compareRelationTables(userTable, solutionTable, allowExtraColumns) {
    if (!userTable || !solutionTable) {
        console.error("Tabellen nicht vorhanden für Vergleich");
        return false;
    }

    // Spalten normalisieren und in Sets konvertieren
    const userCols = new Set(userTable.headers);
    const solutionCols = new Set(solutionTable.headers);

    // Überprüfe ob Solution-Spalten alle in User-Spalten enthalten sind
    for (const col of solutionCols) {
        if (!userCols.has(col)) {
            console.error(`Fehlende Spalte: ${col}`);
            return false;
        }
    }

    // Überprüfe extra Spalten
    if (!allowExtraColumns && userCols.size !== solutionCols.size) {
        console.error("Zu viele Spalten im Ergebnis");
        return false;
    }

    // Spalten-Indices im User-Ergebnis finden
    const colIndices = {};
    solutionTable.headers.forEach((col) => {
        colIndices[col] = userTable.headers.indexOf(col);
    });

    // Zeilen vergleichen (Reihenfolge egal)
    const solutionRowSet = new Set();
    solutionTable.rows.forEach((row) => {
        const mappedRow = row.map((val, idx) => val).join("|||");
        solutionRowSet.add(mappedRow);
    });

    const userRowSet = new Set();
    userTable.rows.forEach((row) => {
        const mappedRow = solutionTable.headers
            .map((col, idx) => row[colIndices[col]] || "")
            .join("|||");
        userRowSet.add(mappedRow);
    });

    // Zeilenmenge vergleichen
    if (userRowSet.size !== solutionRowSet.size) {
        console.error(`Unterschiedliche Zeilenzahl: ${userRowSet.size} vs ${solutionRowSet.size}`);
        return false;
    }

    for (const userRow of userRowSet) {
        if (!solutionRowSet.has(userRow)) {
            console.error(`Unterschiedliche Zeile gefunden: ${userRow}`);
            return false;
        }
    }

    return true;
}

// RA-Ergebnis-Validierung
async function validateRASolution(taskId, userQuery, task) {
    try {
        console.log("✅ Validating solution...");

        const allowExtraColumns = task.validation?.extraColumnsAllowed ?? false;

        // User-Ergebnis in separatem Container mit ALLEN Zeilen extrahieren (für Vergleich)
        const userValidationContainer = document.createElement("div");
        userValidationContainer.style.display = "none";
        document.body.appendChild(userValidationContainer);

        try {
            RelaxEngine.execute(userQuery, userValidationContainer, {
                showTree: false,
                showFormula: false,
                maxRowsPerPage: 10000,  // ALLE Zeilen für Vergleich
            });

            // Warte auf Rendering
            let userTable = null;
            for (let i = 0; i < 20; i++) {
                await new Promise(resolve => setTimeout(resolve, 100));
                userTable = extractTableFromContainer(userValidationContainer);
                if (userTable) break;
            }

            console.log("🔍 User table extracted (full):", userTable);

            if (!userTable) {
                console.error("User-Tabelle konnte nicht extrahiert werden");
                updateTaskStatus(taskId, "incorrect");
                showValidationFeedback(false);
                return;
            }

            // Solution-Query in separatem Container ausführen (mit ALLEN Zeilen)
            const tempContainer = document.createElement("div");
            tempContainer.style.display = "none";
            document.body.appendChild(tempContainer);

            try {
                RelaxEngine.execute(task.solutionCode, tempContainer, {
                    showTree: false,
                    showFormula: false,
                    maxRowsPerPage: 10000,  // ALLE Zeilen für Vergleich
                });

                // Warte auf Rendering der Tabelle
                let solutionTable = null;
                for (let i = 0; i < 20; i++) {
                    await new Promise(resolve => setTimeout(resolve, 100));
                    solutionTable = extractTableFromContainer(tempContainer);
                    if (solutionTable) break;
                }

                console.log("🔍 Solution table extracted (full):", solutionTable);

                if (!solutionTable) {
                    console.error("Solution-Tabelle konnte nicht extrahiert werden");
                    updateTaskStatus(taskId, "incorrect");
                    showValidationFeedback(false);
                    return;
                }

                // Tabellen vergleichen
                const isCorrect = compareRelationTables(userTable, solutionTable, allowExtraColumns);

                console.log(
                    `📊 Validation result: ${isCorrect ? "CORRECT" : "INCORRECT"}`,
                    {
                        userTable,
                        solutionTable,
                    }
                );

                if (isCorrect) {
                    updateTaskStatus(taskId, "correct");
                    showValidationFeedback(true);
                } else {
                    updateTaskStatus(taskId, "incorrect");
                    showValidationFeedback(false);
                }
            } finally {
                document.body.removeChild(tempContainer);
            }
        } finally {
            document.body.removeChild(userValidationContainer);
        }
    } catch (e) {
        console.error("Validation error:", e);
        updateTaskStatus(taskId, "incorrect");
        showValidationFeedback(false);
    }
}

// Validierungsfeedback anzeigen
function showValidationFeedback(
    isCorrect,
    userResult = null,
    solutionResult = null,
) {
    const resultTables = document.getElementById("result-tables");

    if (!resultTables) return;

    const feedbackDiv = document.createElement("div");
    feedbackDiv.className = isCorrect
        ? "validation-correct"
        : "validation-incorrect";
    feedbackDiv.style.padding = "12px";
    feedbackDiv.style.borderRadius = "4px";
    feedbackDiv.style.marginBottom = "12px";
    feedbackDiv.style.fontWeight = "bold";

    if (isCorrect) {
        feedbackDiv.style.background = "rgba(76, 175, 80, 0.1)";
        feedbackDiv.style.border = "1px solid #4CAF50";
        feedbackDiv.style.color = "#4CAF50";
        feedbackDiv.innerHTML = "✅ Korrekt! Der RA-Ausdruck ist richtig.";
    } else {
        feedbackDiv.style.background = "rgba(244, 67, 54, 0.1)";
        feedbackDiv.style.border = "1px solid #f44336";
        feedbackDiv.style.color = "#f44336";
        feedbackDiv.innerHTML =
            "❌ Nicht korrekt. Überprüfen Sie Ihren RA-Ausdruck.";
    }

    // Feedback am Anfang einfügen
    if (resultTables.firstChild) {
        resultTables.insertBefore(feedbackDiv, resultTables.firstChild);
    } else {
        resultTables.appendChild(feedbackDiv);
    }
}

// Fehler anzeigen
function showError(message) {
    const errorDisplay = document.getElementById("error-display");
    errorDisplay.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${message}`;
    errorDisplay.style.display = "block";

    // Nach 5 Sekunden ausblenden
    setTimeout(() => {
        errorDisplay.style.display = "none";
    }, 5000);
}

console.log("📄 ra-task-renderer.js loaded");
