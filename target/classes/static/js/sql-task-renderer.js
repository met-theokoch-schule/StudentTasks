// SQL Task Renderer
// Rendert die Aufgaben-Liste mit CodeMirror-Editoren

// Track letzten aktiven Editor
function setupEditorTracking(editor, taskId) {
    editor.on('focus', () => {
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

    // Editor
    const editorContainer = document.createElement("div");
    editorContainer.className = "task-editor";

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
    executeBtn.onclick = () => executeSQL(task.id);

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
    console.log(
        "Actions element:",
        actions,
        "Display:",
        actions.style.display,
        "Visibility:",
        actions.style.visibility,
    );

    // CodeMirror initialisieren (nach DOM-Einfügen)
    setTimeout(() => {
        initializeEditor(task.id, textarea);
    }, 0);

    return card;
}

// CodeMirror-Editor initialisieren
function initializeEditor(taskId, textarea) {
    try {
        const editor = CodeMirror.fromTextArea(textarea, {
            mode: "text/x-mysql",
            lineNumbers: true,
            matchBrackets: true,
            indentWithTabs: true,
            smartIndent: true,
            theme: "default",
            extraKeys: {
                "Ctrl-Enter": () => executeSQL(taskId),
                "Cmd-Enter": () => executeSQL(taskId),
            },
        });

        // Editor speichern
        editors[taskId] = editor;

        // Change-Event - show dirty status
        editor.on("change", () => {
            markDirty();
        });

        // Track editor für Relmodell Click-to-Insert
        setupEditorTracking(editor, taskId);

        console.log(`✅ Editor initialized for task ${taskId}`);
    } catch (e) {
        console.error(`Failed to initialize editor for task ${taskId}:`, e);
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

// SQL ausführen
function executeSQL(taskId) {
    console.log(`▶️ Executing SQL for task ${taskId}`);

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

    const sqlCode = editor.getValue().trim();

    if (!sqlCode) {
        showError("Bitte geben Sie eine SQL-Abfrage ein.");
        return;
    }

    if (!db) {
        showError("Datenbank nicht geladen. Bitte Seite neu laden.");
        return;
    }

    // Zur Ausgabe wechseln
    switchToOutputTab();

    // Ausgabe vorbereiten
    const outputInfo = document.getElementById("output-info");
    const sqlOutput = document.getElementById("sql-output");
    const errorDisplay = document.getElementById("error-display");
    const resultTables = document.getElementById("result-tables");

    outputInfo.style.display = "none";
    sqlOutput.style.display = "block";
    errorDisplay.style.display = "none";
    resultTables.innerHTML =
        '<div class="text-muted"><i class="fas fa-spinner fa-spin"></i> Führe SQL aus...</div>';

    try {
        // SQL ausführen
        const results = db.exec(sqlCode);

        console.log("📊 SQL results:", results);

        // Ergebnisse anzeigen
        resultTables.innerHTML = "";

        // Prüfe ob Ergebnis leer ist
        const isEmpty = results.length === 0 || (results.length > 0 && results[0].values.length === 0);
        
        if (isEmpty) {
            // Lösche alte Fehler und Validierungsfeedback
            errorDisplay.innerHTML = "";
            const validationFeedback = resultTables.parentNode.querySelector('.validation-feedback');
            if (validationFeedback) {
                validationFeedback.innerHTML = "";
            }
            resultTables.innerHTML = 
                '<div style="color: #ff6b6b; padding: 12px; background: rgba(255, 107, 107, 0.1); border-radius: 4px; border-left: 3px solid #ff6b6b;">' +
                '⚠️ Die SQL-Abfrage hat ein leeres Ergebnis zurückgegeben. Bitte überprüfen Sie Ihre Abfrage.' +
                '</div>';
            updateTaskStatus(taskId, "not_attempted");
            return;
        } else {
            results.forEach((result, idx) => {
                const table = createResultTable(result.columns, result.values);
                resultTables.appendChild(table);

                if (idx < results.length - 1) {
                    const separator = document.createElement("hr");
                    separator.style.margin = "20px 0";
                    separator.style.borderColor = "#404040";
                    resultTables.appendChild(separator);
                }
            });
        }

        // Store results for validation feedback
        window.lastResults = results;

        // Validierung durchführen wenn Musterlösung vorhanden
        if (task.solutionCode && task.validation) {
            validateSolution(taskId, sqlCode, task, results);
        } else {
            // Keine Validierung - Status auf "bearbeitet" setzen
            updateTaskStatus(taskId, "not_attempted");
        }
    } catch (e) {
        console.error("SQL execution error:", e);
        showError(`SQL-Fehler: ${e.message}`);
        resultTables.innerHTML = "";

        // Validierungs-Feedback ausblenden bei SQL-Fehler
        const validationFeedback = resultTables.parentNode.querySelector('.validation-feedback');
        if (validationFeedback) {
            validationFeedback.innerHTML = "";
        }

        // Status auf "incorrect" setzen bei SQL-Fehler
        updateTaskStatus(taskId, "incorrect");
        return;
    }
}

// Ergebnistabelle erstellen
function createResultTable(columns, values, invalidColumnIndices = []) {
    const table = document.createElement("table");

    // Header
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");

    columns.forEach((col, idx) => {
        const th = document.createElement("th");
        th.textContent = col;

        // Markiere falsche Spaltennamen in Rot
        if (invalidColumnIndices.includes(idx)) {
            th.style.color = "#ff6b6b";
            th.style.fontWeight = "bold";
        }

        headerRow.appendChild(th);
    });

    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Body
    const tbody = document.createElement("tbody");

    if (values.length === 0) {
        const emptyRow = document.createElement("tr");
        const emptyCell = document.createElement("td");
        emptyCell.colSpan = columns.length;
        emptyCell.textContent = "Keine Zeilen gefunden";
        emptyCell.style.textAlign = "center";
        emptyCell.style.color = "#999";
        emptyRow.appendChild(emptyCell);
        tbody.appendChild(emptyRow);
    } else {
        values.forEach((row) => {
            const tr = document.createElement("tr");

            row.forEach((cell) => {
                const td = document.createElement("td");
                td.textContent = cell !== null ? cell : "NULL";
                if (cell === null) {
                    td.style.fontStyle = "italic";
                    td.style.color = "#999";
                }
                tr.appendChild(td);
            });

            tbody.appendChild(tr);
        });
    }

    table.appendChild(tbody);

    return table;
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

console.log("📄 sql-task-renderer.js loaded");
