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
        initializeEditor(task.id, textarea);
    }, 0);

    return card;
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

        // RelaxEngine ausführen
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

// RA-Ergebnis-Validierung
async function validateRASolution(taskId, userQuery, task) {
    try {
        console.log("✅ Validating solution...");

        // Für jetzt: einfacher String-Vergleich oder struktureller Vergleich
        // In einer echten Implementierung würde man die Ergebnisse vergleichen

        const isCorrect = userQuery.trim() === task.solutionCode.trim();

        if (isCorrect) {
            updateTaskStatus(taskId, "correct");
            showValidationFeedback(true);
        } else {
            updateTaskStatus(taskId, "incorrect");
            showValidationFeedback(false);
        }
    } catch (e) {
        console.error("Validation error:", e);
        updateTaskStatus(taskId, "incorrect");
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
