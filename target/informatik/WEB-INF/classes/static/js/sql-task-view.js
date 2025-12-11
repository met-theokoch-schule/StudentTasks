// SQL Task View - Main JavaScript
// Globale Variablen
let db = null;

// Base64 Decode Hilfsfunktion
function base64ToBytes(base64) {
    const binaryString = atob(base64);
    const bytes = new Uint8Array(binaryString.length);
    for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes;
}
let SQL = null;
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

// GitHub Spoiler Support - einfache HTML-Ersetzung mit anpassbarem Label
function processSpoilersMarkdown(markdown) {
    // Ersetze >!{Label} oder >! mit HTML
    return markdown.replace(/^>!\s*(?:\{([^}]+)\})?\s+(.+?)$/gm, (match, label, content) => {
        const displayLabel = label || "Tipp";
        return `<details style="display:block;padding:12px;border:1px solid #404040;border-radius:4px;margin:12px 0;background:rgba(64,64,64,0.2)"><summary style="cursor:pointer;font-weight:bold;color:#888;user-select:none"><i class="fas fa-lock" style="margin-right:6px;"></i>${displayLabel}</summary><div style="margin-top:8px">${content}</div></details>`;
    });
}

// Initialisierung beim Laden
document.addEventListener("DOMContentLoaded", async () => {
    console.log("🚀 SQL Task View initializing...");

    if (initializeFromDOM()) {
        console.log("✅ DOM data loaded");

        // Tutorial-Navigation initialisieren
        initializeTutorialNavigation();

        // SQL.js laden
        await initializeSQL();

        // Datenbank initialisieren
        if (taskData && taskData.database) {
            await initializeDatabase(taskData);
        }

        // Tasks rendern
        if (taskData && taskData.tasks) {
            renderTasks(taskData);
        }

        // Gespeicherten Content laden (verzögert, damit Editoren fertig initialisiert sind)
        setTimeout(() => {
            loadContentToView(currentContent);
        }, 50);

        // Event-Listener einrichten
        setupEventListeners();

        // Fortschritt aktualisieren
        updateProgressInfo();

        // Initial status: everything saved
        updateSaveStatus("saved");

        console.log("✅ SQL Task View initialized successfully");
    } else {
        console.error("❌ Failed to initialize: Invalid task data");
    }
});

// Daten aus versteckten DIVs holen
function initializeFromDOM() {
    try {
        // Basis-URL ermitteln (falls App nicht unter / läuft)
        const defaultLink = document.getElementById("default-link");
        const baseUrl = defaultLink?.getAttribute("href")?.replace(/\/$/, "") || "";
        
        // API-URLs mit Basis-URL zusammensetzen
        const saveUrlDiv = document.getElementById("task-save-url");
        const submitUrlDiv = document.getElementById("task-submit-url");
        const rawSaveUrl = saveUrlDiv?.dataset.url || "/dev/save";
        const rawSubmitUrl = submitUrlDiv?.dataset.url || "/dev/submit";
        
        saveUrl = rawSaveUrl.startsWith("/") ? rawSaveUrl : baseUrl + "/" + rawSaveUrl;
        submitUrl = rawSubmitUrl.startsWith("/") ? rawSubmitUrl : baseUrl + "/" + rawSubmitUrl;

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

        return parsed;
    } catch (e) {
        console.error("Invalid JSON in task description:", e);
        return null;
    }
}

// SQL.js initialisieren
async function initializeSQL() {
    try {
        console.log("📦 Loading SQL.js WASM...");
        const config = {
            locateFile: (filename) =>
                `https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.8.0/${filename}`,
        };
        SQL = await initSqlJs(config);
        console.log("✅ SQL.js loaded successfully");
    } catch (e) {
        console.error("❌ Failed to load SQL.js:", e);
        throw e;
    }
}

// Datenbank initialisieren
async function initializeDatabase(taskData) {
    try {
        console.log("🗄️  Initializing database...");

        if (!taskData.database) {
            console.warn("No database configuration found");
            db = new SQL.Database();
            return;
        }

        if (taskData.database.type === "base64") {
            // Base64-encoded SQLite-File
            const bytes = base64ToBytes(taskData.database.content);
            db = new SQL.Database(bytes);
            console.log("✅ Database loaded from Base64");
        } else if (taskData.database.type === "sql") {
            // SQL-Init-Script
            db = new SQL.Database();
            db.exec(taskData.database.content);
            console.log("✅ Database initialized from SQL script");
        } else {
            console.error("Unknown database type:", taskData.database.type);
            db = new SQL.Database();
        }
    } catch (e) {
        console.error("❌ Failed to initialize database:", e);
        db = new SQL.Database();
    }
}

// Tutorial Navigation initialisieren (Multi-Page Support)
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

    // Prüfe ob ERD-Content vorhanden ist (unabhängig vom Tutorial)
    const hasERDInTutorial = tutorialContents && tutorialContents.some((t) => t.image);
    const hasERDInTaskData = taskData && (taskData.erd_image || taskData.relationshipModel);
    if (hasERDInTutorial || hasERDInTaskData) {
        document.getElementById("erdTabBtn").style.display = "";
    }

    // Tab ausblenden wenn kein Inhalt
    if (!tutorialContents || tutorialContents.length === 0) {
        if (tutorialTab) {
            tutorialTab.style.display = "none";
        }
        return;
    }

    // Navigation HTML erstellen (nur Pfeile und Dots, keine Sub-Tabs)
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
    const currentContent = tutorialContents[currentTutorialIndex].content;

    // Markdown in HTML umwandeln
    const renderedHTML = marked ? marked.parse(currentContent) : currentContent;

    // Markdown rendern mit Links öffnen in neuem Tab
    let processedHTML = renderedHTML;
    if (renderedHTML) {
        // Links mit target="_blank" und rel="noopener noreferrer" versehen
        processedHTML = renderedHTML.replace(
            /<a\s+href=(['"])([^'"]+)\1([^>]*)>/gi,
            '<a href="$2" target="_blank" rel="noopener noreferrer"$3>',
        );
    }

    // Erstelle HTML für iframe
    const htmlContent = `
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { 
                    font-family: 'Segoe UI', sans-serif; 
                    line-height: 1.6; 
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
                    color: #f8f8f2;
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
                table { border-collapse: collapse; width: 100%; margin: 10px 0; }
                th, td { border: 1px solid #404040; padding: 8px; text-align: left; }
                th { background-color: #2d2d2d; }
                a { color: #6dbfff; text-decoration: none; cursor: pointer; }
                a:hover { text-decoration: underline; color: #7fd3ff; }
            </style>
        </head>
        <body>
            ${processedHTML}
        </body>
        </html>
    `;

    tutorialFrame.innerHTML = "";
    tutorialFrame.style.overflow = "auto";

    // Füge HTML direkt ein statt iframe zu verwenden
    const wrapper = document.createElement("div");
    wrapper.innerHTML = `
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { 
                    font-family: 'Segoe UI', sans-serif; 
                    line-height: 1.6; 
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
                    color: #f8f8f2;
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
                table { border-collapse: collapse; width: 100%; margin: 10px 0; }
                th, td { border: 1px solid #404040; padding: 8px; text-align: left; }
                th { background-color: #2d2d2d; }
                a { color: #6dbfff; text-decoration: none; cursor: pointer; }
                a:hover { text-decoration: underline; color: #7fd3ff; }
            </style>
        </head>
        <body>
            ${processedHTML}
        </body>
        </html>
    `;

    tutorialFrame.innerHTML = wrapper.innerHTML;
}

// ERD Display aktualisieren
function updateERDDisplay() {
    const erdOutput = document.getElementById("erdOutput");
    if (!erdOutput) return;

    if (!taskData) {
        erdOutput.innerHTML =
            '<div style="padding: 20px; color: #999;">Keine Daten verfügbar.</div>';
        return;
    }

    // Relationenmodell und Bild aus der task.description Ebene
    let modelContent = taskData.relationshipModel || "";
    let erdImage = taskData.erd_image || "";
    let modelHTML = "";

    if (isRelmodellMarkup && isRelmodellMarkup(modelContent)) {
        // Relationenmodell rendern
        const parsed = parseRelmodellMarkup(modelContent);
        modelHTML = renderRelmodell(parsed);
    } else if (modelContent) {
        // Normales Markdown rendern
        modelHTML = marked ? marked.parse(modelContent) : modelContent;
    }

    erdOutput.innerHTML = `
        <div style="padding: 20px; background: #1e1e1e; color: #cccccc; height: 100%; overflow: auto;">
            ${erdImage ? `<div style="margin-bottom: 20px;">
                <div style="position: relative; text-align: center;">
                    <img id="erdImage" src="${erdImage}" alt="ER-Diagramm" style="max-width: 100%; height: auto; border: 1px solid #404040; border-radius: 4px; cursor: pointer; transition: transform 0.2s;">
                    <button id="erdZoomBtn" style="position: absolute; top: 10px; right: 10px; padding: 8px 10px; background-color: #007acc; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; transition: background-color 0.2s; display: flex; align-items: center; justify-content: center; width: 36px; height: 36px;"><i class="fas fa-expand-alt"></i></button>
                </div>
            </div>` : ''}
            <div style="background: #2d2d2d; padding: 15px; border-radius: 4px; border: 1px solid #404040;">
                ${modelHTML}
            </div>
        </div>
    `;

    // Zoom Button Handler - nur wenn Bild existiert
    if (erdImage) {
        const zoomBtn = document.getElementById("erdZoomBtn");
        if (zoomBtn) zoomBtn.addEventListener("click", openERDModal);
        const erdImage_elem = document.getElementById("erdImage");
        if (erdImage_elem) erdImage_elem.addEventListener("click", openERDModal);
    }

    // Click-Handler für Relationenmodell-Elemente aufsetzen
    if (isRelmodellMarkup && isRelmodellMarkup(modelContent)) {
        setTimeout(() => {
            setupRelmodellClickHandlers();
        }, 0);
    }
}

// Modal für ERD-Vergrößerung
function openERDModal() {
    if (!taskData || !taskData.erd_image) return;

    const modal = document.createElement("div");
    modal.id = "erdModal";
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.9);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
    `;

    modal.innerHTML = `
        <div style="position: relative; max-width: 90%; max-height: 90%;">
            <img src="${taskData.erd_image}" alt="ER-Diagramm (vergrößert)" style="max-width: 100%; max-height: 90vh; border-radius: 4px;">
            <button id="closeModal" style="position: absolute; top: 10px; right: 10px; padding: 8px 10px; background-color: #d32f2f; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; display: flex; align-items: center; justify-content: center; width: 36px; height: 36px;"><i class="fas fa-times"></i></button>
        </div>
    `;

    document.body.appendChild(modal);

    document.getElementById("closeModal").addEventListener("click", () => {
        modal.remove();
    });

    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            modal.remove();
        }
    });
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
            if (targetTab === "erd") {
                updateERDDisplay();
            }
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
    } else if (tabName === "tutorial") {
        document.getElementById("tutorialOutput").classList.add("active");
    } else if (tabName === "erd") {
        document.getElementById("erdOutput").classList.add("active");
    }
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

        // Validierung
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

            // UI aktualisieren
            updateTaskIcon(savedTask.id, savedTask.status || "not_attempted");
        });

        // Tutorial-Index wiederherstellen
        if (
            content.currentTutorialIndex !== undefined &&
            tutorialContents.length > 0
        ) {
            currentTutorialIndex = Math.min(
                content.currentTutorialIndex,
                tutorialContents.length - 1,
            );
            updateTutorialDisplay();
        }

        // Fortschritt aktualisieren
        updateProgressInfo();

        console.log("✅ Loaded saved content with full status metadata");
    } catch (e) {
        console.error("Failed to load content:", e);
    }
}

// Task-Status abrufen
function getTaskStatus(taskId) {
    const taskStatusEntry = taskStatus.tasks.find((t) => t.id === taskId);
    return taskStatusEntry ? taskStatusEntry.status : "not_attempted";
}

// Last-Executed-Zeit abrufen
function getTaskLastExecuted(taskId) {
    const taskStatusEntry = taskStatus.tasks.find((t) => t.id === taskId);
    return taskStatusEntry ? taskStatusEntry.lastExecuted : null;
}

// Task-Status aktualisieren
function updateTaskStatus(taskId, status, triggerAutoSave = true) {
    const taskStatusEntry = taskStatus.tasks.find((t) => t.id === taskId);

    if (taskStatusEntry) {
        taskStatusEntry.status = status;
        taskStatusEntry.lastExecuted = new Date().toISOString();
        taskStatusEntry.attempts = (taskStatusEntry.attempts || 0) + 1;
    } else {
        taskStatus.tasks.push({
            id: taskId,
            status: status,
            lastExecuted: new Date().toISOString(),
            attempts: 1,
        });
    }

    // UI aktualisieren
    updateTaskIcon(taskId, status);
    updateProgressInfo();

    // Auto-Save triggern
    if (triggerAutoSave) {
        // autoSave();
    }
}

// Task-Icon aktualisieren
function updateTaskIcon(taskId, status) {
    const iconElement = document.getElementById(`task-icon-${taskId}`);
    const cardElement = document.getElementById(`task-card-${taskId}`);

    if (!iconElement) return;

    // Alte Klassen entfernen
    iconElement.classList.remove(
        "status-not-attempted",
        "status-correct",
        "status-incorrect",
    );
    if (cardElement) {
        cardElement.classList.remove("task-completed", "task-incorrect");
    }

    // Neue Klassen und Icon setzen
    if (status === "correct") {
        iconElement.innerHTML = "✅";
        iconElement.classList.add("status-correct");
        if (cardElement) cardElement.classList.add("task-completed");
    } else if (status === "incorrect") {
        iconElement.innerHTML = "❌";
        iconElement.classList.add("status-incorrect");
        if (cardElement) cardElement.classList.add("task-incorrect");
    } else {
        iconElement.innerHTML = "⭕";
        iconElement.classList.add("status-not-attempted");
    }
}

// Fortschritts-Info aktualisieren
function updateProgressInfo() {
    const completedCount = taskStatus.tasks.filter(
        (t) => t.status === "correct",
    ).length;
    const totalCount = taskData?.tasks?.length || 0;

    const completedElement = document.getElementById("completed-count");
    const totalElement = document.getElementById("total-count");

    if (completedElement) completedElement.textContent = completedCount;
    if (totalElement) totalElement.textContent = totalCount;

    taskStatus.metadata.totalTasks = totalCount;
    taskStatus.metadata.completedTasks = completedCount;
}

// Show dirty status (unsaved changes) - NO auto-save
function markDirty() {
    clearTimeout(autoSaveTimeout);
    updateSaveStatus("ready");
    // Autosave disabled - users must click Save button manually
}

// Speichern-Status aktualisieren
function updateSaveStatus(status) {
    const statusElement = document.getElementById("save-status");
    if (!statusElement) return;

    statusElement.classList.remove(
        "text-muted",
        "text-success",
        "text-danger",
        "text-warning",
        "text-info",
    );
    statusElement.classList.remove(
        "fa-circle",
        "fa-spinner",
        "fa-check-circle",
        "fa-times-circle",
    );

    switch (status) {
        case "saved":
            statusElement.classList.add("fa-circle", "text-success");
            statusElement.title = "Gespeichert";
            break;
        case "saving":
            statusElement.classList.add("fa-circle", "text-info");
            statusElement.title = "Speichert...";
            break;
        case "error":
            statusElement.classList.add("fa-circle", "text-danger");
            statusElement.title = "Fehler beim Speichern";
            break;
        case "submitted":
            statusElement.classList.add("fa-circle", "text-success");
            statusElement.title = "Abgegeben";
            break;
        case "ready":
        default:
            statusElement.classList.add("fa-circle", "text-warning");
            statusElement.title = "Nicht gespeicherte Änderungen";
            break;
    }
}

// Speichern-Funktion
async function saveContent(isSubmission = false) {
    const content = getContentFromView();

    // URL validieren
    const url = isSubmission ? submitUrl : saveUrl;
    if (!url || url === "") {
        console.error("Save URL not configured");
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
            console.log("✅ Content saved successfully");

            // iFrame-Kommunikation
            if (window.parent && window.parent !== window) {
                window.parent.postMessage(
                    isSubmission ? "content-submitted" : "content-saved",
                    "*",
                );
            }

            // Bei Abgabe: Read-Only-Modus aktivieren
            if (isSubmission) {
                disableEditing();
            }
        } else {
            console.error("Save failed:", response.status, response.statusText);
            updateSaveStatus("error");
        }
    } catch (error) {
        console.error("Save failed:", error);
        updateSaveStatus("error");
    }
}

// Aufgabe abgeben
function submitTask() {
    // Validierung: Sind alle Aufgaben bearbeitet?
    const uncompletedTasks =
        (taskData?.tasks?.length || 0) -
        taskStatus.tasks.filter((t) => t.status === "correct").length;

    let confirmMessage = "Möchten Sie diese Aufgabe wirklich abgeben?\n\n";

    if (uncompletedTasks > 0) {
        confirmMessage += `Sie haben noch ${uncompletedTasks} nicht korrekt gelöste Aufgabe(n).\n\n`;
    }

    confirmMessage +=
        "Nach der Abgabe können Sie keine Änderungen mehr vornehmen.";

    if (confirm(confirmMessage)) {
        saveContent(true);
    }
}

// Read-Only-Modus aktivieren
function disableEditing() {
    // Alle Editoren deaktivieren
    Object.values(editors).forEach((editor) => {
        editor.setOption("readOnly", true);
        editor.setOption("cursorBlinkRate", -1);
    });

    // Alle Buttons deaktivieren
    document.querySelectorAll(".btn-execute, .btn-reset").forEach((btn) => {
        btn.disabled = true;
    });

    document.getElementById("saveButton")?.setAttribute("disabled", "true");
    document.getElementById("submitButton")?.setAttribute("disabled", "true");

    console.log("🔒 Editing disabled (submitted)");
}

// Hilfsfunktion: Zur Ausgabe wechseln
function switchToOutputTab() {
    switchToTab("result");
}

console.log("📄 sql-task-view.js loaded");
