// RA Task View - Main JavaScript für Relationale Algebra
// Globale Variablen
let taskData = null;
let currentContent = "";
let tutorial = "";
let tutorialContents = [];
let currentTutorialIndex = 0;
let saveUrl = "";
let submitUrl = "";
let editors = {};
let taskStatus = {
    tasks: [],
    metadata: {
        totalTasks: 0,
        completedTasks: 0,
    },
};
let autoSaveTimeout = null;
const AUTO_SAVE_DELAY = 3000;
let isDirty = false;

// GitHub Spoiler Support - einfache HTML-Ersetzung mit anpassbarem Label
function processSpoilersMarkdown(markdown) {
    return markdown.replace(/^>!\s*(?:\{([^}]+)\})?\s+(.+?)$/gm, (match, label, content) => {
        const displayLabel = label || "Tipp";
        return `<details style="display:block;padding:12px;border:1px solid #404040;border-radius:4px;margin:12px 0;background:rgba(64,64,64,0.2)"><summary style="cursor:pointer;font-weight:bold;color:#888;user-select:none"><i class="fas fa-lock" style="margin-right:6px;"></i>${displayLabel}</summary><div style="margin-top:8px">${content}</div></details>`;
    });
}

// Initialisierung beim Laden
document.addEventListener("DOMContentLoaded", async () => {
    console.log("🚀 RA Task View initializing...");

    if (initializeFromDOM()) {
        console.log("✅ DOM data loaded");

        // Tutorial-Navigation initialisieren
        initializeTutorialNavigation();

        // RelaxEngine initialisieren
        if (taskData && taskData.tables) {
            await initializeRelaxEngine(taskData.tables);
            
            // Schema rendern
            renderSchema(taskData.tables);
        }

        // Tasks rendern
        if (taskData && taskData.tasks) {
            renderTasks(taskData);
        }

        // Gespeicherten Content laden
        loadContentToView(currentContent);

        // Event-Listener einrichten
        setupEventListeners();

        // Fortschritt aktualisieren
        updateProgressInfo();

        // Initial status: everything saved
        updateSaveStatus("saved");

        console.log("✅ RA Task View initialized successfully");
    } else {
        console.error("❌ Failed to initialize: Invalid task data");
    }
});

// Daten aus versteckten DIVs holen
function initializeFromDOM() {
    try {
        // API-URLs
        const saveUrlDiv = document.getElementById("task-save-url");
        const submitUrlDiv = document.getElementById("task-submit-url");
        saveUrl = saveUrlDiv?.dataset.url || "/dev/save";
        submitUrl = submitUrlDiv?.dataset.url || "/dev/submit";

        // Content-Daten
        currentContent =
            document.getElementById("currentContent")?.textContent?.trim() ||
            "";
        const description =
            document.getElementById("description")?.textContent?.trim() || "";
        tutorial =
            document.getElementById("tutorial")?.textContent?.trim() || "";

        console.log("📁 Save URL:", saveUrl);
        console.log("📁 Submit URL:", submitUrl);

        // task.description parsen
        if (!description) {
            console.error("No task description found");
            return false;
        }

        taskData = parseTaskDescription(description);

        if (!taskData) {
            console.error("Failed to parse task description");
            return false;
        }

        console.log("📋 Loaded", taskData.tasks?.length || 0, "tasks");
        console.log("📊 Loaded", Object.keys(taskData.tables || {}).length, "tables");
        return true;
    } catch (e) {
        console.error("Error initializing from DOM:", e);
        return false;
    }
}

// JSON-Parser für task.description
function parseTaskDescription(descriptionString) {
    try {
        const parsed = JSON.parse(descriptionString);

        // Validierung
        if (!parsed.version) {
            console.warn("No version specified in task description");
        }

        if (!parsed.tasks || !Array.isArray(parsed.tasks)) {
            console.error("Invalid tasks array in task description");
            return null;
        }

        if (!parsed.tables || typeof parsed.tables !== 'object') {
            console.warn("No tables found in task description");
            parsed.tables = {};
        }

        return parsed;
    } catch (e) {
        console.error("Invalid JSON in task description:", e);
        return null;
    }
}

// RelaxEngine initialisieren
async function initializeRelaxEngine(tables) {
    try {
        console.log("🔧 Initializing RelaxEngine...");
        
        // Tabellen setzen
        if (RelaxEngine && RelaxEngine.setRelations) {
            RelaxEngine.setRelations(tables);
            console.log("✅ RelaxEngine initialized with", Object.keys(tables).length, "tables");
        } else {
            console.warn("RelaxEngine not fully loaded yet");
        }
    } catch (e) {
        console.error("❌ Failed to initialize RelaxEngine:", e);
    }
}

// Schema aus Tabellendefinition generieren und rendern
function renderSchema(tables) {
    const schemaContainer = document.getElementById("schema-container");
    if (!schemaContainer) return;

    schemaContainer.innerHTML = "";

    if (!tables || Object.keys(tables).length === 0) {
        schemaContainer.innerHTML = '<div style="color: #999; padding: 10px; font-size: 12px;">Keine Tabellen definiert.</div>';
        return;
    }

    // Jede Tabelle als separates Element
    for (const [tableName, tableData] of Object.entries(tables)) {
        const tableSchema = document.createElement("div");
        tableSchema.className = "schema-table";

        // Tabellennamen (anklickbar)
        const tableNameElem = document.createElement("div");
        tableNameElem.className = "schema-table-name";
        tableNameElem.textContent = tableName;
        tableNameElem.dataset.insert = tableName;
        tableNameElem.addEventListener('click', () => {
            insertIntoLastActiveEditor(tableName);
        });
        tableSchema.appendChild(tableNameElem);

        // Spalten
        if (tableData.columns && Array.isArray(tableData.columns)) {
            const columnsDiv = document.createElement("div");
            columnsDiv.className = "schema-columns";

            tableData.columns.forEach(column => {
                const columnDiv = document.createElement("div");
                columnDiv.className = "schema-column";

                // Spaltenname (anklickbar)
                const colName = document.createElement("span");
                colName.className = "schema-col-name";
                colName.textContent = column.name;
                colName.dataset.insert = column.name;
                colName.addEventListener('click', () => {
                    insertIntoLastActiveEditor(column.name);
                });

                // Spaltentyp (nicht anklickbar)
                const colType = document.createElement("span");
                colType.className = "schema-col-type";
                colType.textContent = column.type || "unknown";

                columnDiv.appendChild(colName);
                columnDiv.appendChild(colType);
                columnsDiv.appendChild(columnDiv);
            });

            tableSchema.appendChild(columnsDiv);
        }

        schemaContainer.appendChild(tableSchema);
    }

    console.log("✅ Schema rendered");
}

// Text in letzten aktiven Editor einfügen
function insertIntoLastActiveEditor(text) {
    if (lastActiveEditor) {
        const cursor = lastActiveEditor.getCursor();
        lastActiveEditor.replaceRange(text, cursor);
        lastActiveEditor.focus();
        markDirty();
    }
}

// Tutorial Navigation initialisieren
function initializeTutorialNavigation() {
    const tutorialOutput = document.getElementById("tutorialOutput");
    const tutorialTab = document.querySelector(
        '.output-tab[data-output-tab="tutorial"]',
    );

    // Tutorial-Daten parsen
    console.log("📖 Raw tutorial data:", tutorial);
    if (tutorial && tutorial.trim()) {
        try {
            tutorialContents = new Function(`return (${tutorial});`)();
            console.log("✅ Parsed tutorial pages:", tutorialContents.length);
        } catch (e) {
            console.warn(
                "Tutorial parsing failed, treating as single page:",
                e,
            );
            tutorialContents = [{ content: tutorial }];
        }
    } else {
        console.warn("No tutorial data found");
    }

    // Tab ausblenden wenn kein Inhalt
    if (!tutorialContents || tutorialContents.length === 0) {
        if (tutorialTab) {
            tutorialTab.style.display = "none";
        }
        return;
    }

    // Navigation HTML erstellen
    const navigationHTML = `
        <div class="tutorial-navigation">
            <button id="tutorialPrev" class="nav-arrow">←</button>
            <div class="tutorial-dots">
                ${tutorialContents
                    .map(
                        (_, index) =>
                            `<span class="tutorial-dot ${index === 0 ? "active" : ""}" data-index="${index}"></span>`,
                    )
                    .join("")}
            </div>
            <button id="tutorialNext" class="nav-arrow">→</button>
        </div>
        <div class="tutorial-content">
            <div id="tutorialFrame"></div>
        </div>
    `;

    tutorialOutput.innerHTML = navigationHTML;

    // Event Listeners hinzufügen
    document.getElementById("tutorialPrev").addEventListener("click", () => {
        if (currentTutorialIndex > 0) {
            currentTutorialIndex--;
            updateTutorialDisplay();
        }
    });

    document.getElementById("tutorialNext").addEventListener("click", () => {
        if (currentTutorialIndex < tutorialContents.length - 1) {
            currentTutorialIndex++;
            updateTutorialDisplay();
        }
    });

    // Dot Navigation
    document.querySelectorAll(".tutorial-dot").forEach((dot) => {
        dot.addEventListener("click", (e) => {
            currentTutorialIndex = parseInt(e.target.dataset.index);
            updateTutorialDisplay();
        });
    });

    updateTutorialDisplay();
}

// Tutorial Display aktualisieren
function updateTutorialDisplay() {
    // Dots aktualisieren
    document.querySelectorAll(".tutorial-dot").forEach((dot, index) => {
        dot.classList.toggle("active", index === currentTutorialIndex);
    });

    // Button States
    document.getElementById("tutorialPrev").disabled =
        currentTutorialIndex === 0;
    document.getElementById("tutorialNext").disabled =
        currentTutorialIndex === tutorialContents.length - 1;

    // Content aktualisieren
    const tutorialFrame = document.getElementById("tutorialFrame");
    const currentTutorialContent = tutorialContents[currentTutorialIndex].content;

    // Markdown in HTML umwandeln
    const renderedHTML = marked ? marked.parse(currentTutorialContent) : currentTutorialContent;

    // Links mit target="_blank" versehen
    let processedHTML = renderedHTML;
    if (renderedHTML) {
        processedHTML = renderedHTML.replace(
            /<a\s+href=(['"])([^'"]+)\1([^>]*)>/gi,
            '<a href="$2" target="_blank" rel="noopener noreferrer"$3>',
        );
    }

    tutorialFrame.innerHTML = processedHTML;
}

// Event-Listener einrichten
function setupEventListeners() {
    // Speichern-Button
    const saveButton = document.getElementById("saveButton");
    if (saveButton) {
        saveButton.addEventListener("click", () => saveContent(false));
    }

    // Abgeben-Button
    const submitButton = document.getElementById("submitButton");
    if (submitButton) {
        submitButton.addEventListener("click", submitTask);
    }

    // Tab-Switching
    const outputTabs = document.querySelectorAll(".output-tab");
    outputTabs.forEach((tab) => {
        tab.addEventListener("click", () => {
            const targetTab = tab.dataset.outputTab;
            switchToTab(targetTab);
        });
    });

    // Splitter für Panel-Größenanpassung
    setupSplitter();
}

// Tab-Switching
function switchToTab(tabName) {
    // Tabs aktualisieren
    document.querySelectorAll(".output-tab").forEach((tab) => {
        tab.classList.remove("active");
    });
    const activeTab = document.querySelector(
        `.output-tab[data-output-tab="${tabName}"]`,
    );
    if (activeTab) {
        activeTab.classList.add("active");
    }

    // Content aktualisieren
    document.querySelectorAll(".output-content").forEach((content) => {
        content.classList.remove("active");
    });

    if (tabName === "result") {
        document.getElementById("resultOutput").classList.add("active");
    } else if (tabName === "schema") {
        document.getElementById("schemaOutput").classList.add("active");
    } else if (tabName === "tutorial") {
        document.getElementById("tutorialOutput").classList.add("active");
    }
}

// Zur Ausgabe-Tab wechseln
function switchToOutputTab() {
    switchToTab("result");
}

// Splitter für Panel-Größenanpassung
function setupSplitter() {
    const splitter = document.getElementById("splitter");
    const mainContent = document.querySelector(".main-content");
    const tasksPanel = document.querySelector(".tasks-panel");
    const outputPanel = document.querySelector(".output-panel");

    let isResizing = false;

    splitter.addEventListener("mousedown", (e) => {
        isResizing = true;
        document.body.style.cursor = "col-resize";
        e.preventDefault();
    });

    document.addEventListener("mousemove", (e) => {
        if (!isResizing) return;

        const containerRect = mainContent.getBoundingClientRect();
        const offsetX = e.clientX - containerRect.left;
        const percentage = (offsetX / containerRect.width) * 100;

        // Begrenzung: 20% - 80%
        if (percentage > 20 && percentage < 80) {
            tasksPanel.style.width = `${percentage}%`;
            outputPanel.style.width = `${100 - percentage}%`;
        }
    });

    document.addEventListener("mouseup", () => {
        if (isResizing) {
            isResizing = false;
            document.body.style.cursor = "default";
        }
    });
}

// Content aus View extrahieren
function getContentFromView() {
    const content = {
        version: "1.0",
        tasks: [],
        currentTutorialIndex: currentTutorialIndex,
        metadata: {
            totalTasks: taskData?.tasks?.length || 0,
            completedTasks: taskStatus.tasks.filter(
                (t) => t.status === "correct",
            ).length,
            lastSaved: new Date().toISOString(),
            taskOrder: taskData?.tasks?.map((t) => t.id) || [],
        },
    };

    if (taskData && taskData.tasks) {
        taskData.tasks.forEach((task, index) => {
            const editor = editors[task.id];
            const statusEntry = taskStatus.tasks.find((t) => t.id === task.id);

            content.tasks.push({
                id: task.id,
                code: editor ? editor.getValue() : task.defaultCode || "",
                status: statusEntry?.status || "not_attempted",
                lastExecuted: statusEntry?.lastExecuted || null,
                attempts: statusEntry?.attempts || 0,
            });
        });
    }

    return JSON.stringify(content);
}

// Content in View laden
function loadContentToView(contentString) {
    if (!contentString || contentString.trim() === "") {
        console.log("No saved content to load");
        return;
    }

    try {
        const content = JSON.parse(contentString);

        if (content.version !== "1.0") {
            console.warn("Unknown content version:", content.version);
        }

        if (!content.tasks || !Array.isArray(content.tasks)) {
            console.warn("Invalid tasks in saved content");
            return;
        }

        // Editoren und Status laden
        content.tasks.forEach((savedTask) => {
            const editor = editors[savedTask.id];
            if (editor) {
                editor.setValue(savedTask.code || "");
            }

            // Status vollständig wiederherstellen
            const statusEntry = taskStatus.tasks.find(
                (t) => t.id === savedTask.id,
            );
            if (statusEntry) {
                statusEntry.status = savedTask.status || "not_attempted";
                statusEntry.lastExecuted = savedTask.lastExecuted || null;
                statusEntry.attempts = savedTask.attempts || 0;
            } else {
                taskStatus.tasks.push({
                    id: savedTask.id,
                    status: savedTask.status || "not_attempted",
                    lastExecuted: savedTask.lastExecuted || null,
                    attempts: savedTask.attempts || 0,
                });
            }
        });

        console.log("✅ Content loaded successfully");
        updateSaveStatus("saved");
    } catch (e) {
        console.error("Error loading content:", e);
    }
}

// Content speichern
async function saveContent(isSubmission = false) {
    const content = getContentFromView();
    const url = isSubmission ? submitUrl : saveUrl;
    

    if (!url) {
        console.error("No save URL configured");
        updateSaveStatus("error");
        return;
    }

    updateSaveStatus("saving");

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: content }),
        });

        if (response.ok) {
            updateSaveStatus(isSubmission ? "submitted" : "saved");
            isDirty = false;
            console.log(isSubmission ? "✅ Task submitted" : "✅ Content saved");
        } else {
            updateSaveStatus("error");
            console.error("Save failed:", response.status);
        }
    } catch (error) {
        updateSaveStatus("error");
        console.error("Save error:", error);
    }
}

// Aufgabe abgeben
function submitTask() {
    if (
        confirm(
            "Möchten Sie diese Aufgabe wirklich abgeben? Sie können sie danach nicht mehr bearbeiten.",
        )
    ) {
        saveContent(true);
    }
}

// Status-Anzeige aktualisieren
function updateSaveStatus(status) {
    const statusElement = document.getElementById("save-status");
    if (!statusElement) return;

    statusElement.className = "fas fa-circle";
    statusElement.title = "";
    statusElement.style.color = "";

    switch (status) {
        case "saved":
            statusElement.classList.add("text-success");
            statusElement.title = "Gespeichert";
            break;
        case "saving":
            statusElement.classList.add("text-warning");
            statusElement.classList.add("fa-spinner");
            statusElement.classList.add("fa-spin");
            statusElement.title = "Speichert...";
            break;
        case "error":
            statusElement.classList.add("text-danger");
            statusElement.title = "Fehler beim Speichern";
            break;
        case "submitted":
            statusElement.classList.add("text-info");
            statusElement.title = "Abgegeben";
            break;
        default:
            statusElement.style.color = "rgb(255, 193, 7)";
            statusElement.title = "Bereit zum Speichern";
    }
}

// Dirty-Flag setzen (nur Status aktualisieren, kein Auto-Save)
function markDirty() {
    isDirty = true;
    updateSaveStatus("ready");

    // Auto-Save wurde deaktiviert - nur manuelles Speichern via "Speichern" Button
}

// Task-Status aktualisieren
function updateTaskStatus(taskId, status) {
    let statusEntry = taskStatus.tasks.find((t) => t.id === taskId);
    
    if (!statusEntry) {
        statusEntry = {
            id: taskId,
            status: status,
            lastExecuted: new Date().toISOString(),
            attempts: 1,
        };
        taskStatus.tasks.push(statusEntry);
    } else {
        statusEntry.status = status;
        statusEntry.lastExecuted = new Date().toISOString();
        statusEntry.attempts = (statusEntry.attempts || 0) + 1;
    }

    // UI aktualisieren
    const statusIcon = document.getElementById(`task-icon-${taskId}`);
    const taskCard = document.getElementById(`task-card-${taskId}`);
    
    if (statusIcon && taskCard) {
        taskCard.classList.remove("task-completed", "task-incorrect");
        
        switch (status) {
            case "correct":
                statusIcon.innerHTML = "✅";
                statusIcon.className = "task-status-icon status-correct";
                taskCard.classList.add("task-completed");
                break;
            case "incorrect":
                statusIcon.innerHTML = "❌";
                statusIcon.className = "task-status-icon status-incorrect";
                taskCard.classList.add("task-incorrect");
                break;
            default:
                statusIcon.innerHTML = "⭕";
                statusIcon.className = "task-status-icon status-not-attempted";
        }
    }

    updateProgressInfo();
}

// Fortschritt aktualisieren
function updateProgressInfo() {
    const completedCount = taskStatus.tasks.filter(
        (t) => t.status === "correct",
    ).length;
    const totalCount = taskData?.tasks?.length || 0;

    const completedElement = document.getElementById("completed-count");
    const totalElement = document.getElementById("total-count");

    if (completedElement) completedElement.textContent = completedCount;
    if (totalElement) totalElement.textContent = totalCount;
}

// Auto-Save bei Fenster-Schließen
window.addEventListener("beforeunload", (e) => {
    if (isDirty) {
        e.preventDefault();
        e.returnValue = "";
        saveContent(false);
    }
});

console.log("📄 ra-task-view.js loaded");
