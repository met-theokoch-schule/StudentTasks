// Globale Variablen
let pythonWorker = null;
let pyodideReady = false;
let isExecuting = false;
let isResizing = false;
let currentExecutionId = null;
let currentTutorialIndex = 0;

let teacherTemplateCode = "";
let parsedTemplate = null;
let studentGapAnswers = {};
let gapInputElements = new Map();
let currentEditorFontSize = 14;
let mypyReady = false;
let mainThreadPyodide = null;
let mypyInitializationTried = false;
let mypyInitializationPromise = null;
let isPreparingExecution = false;

const MAX_CONSOLE_SIZE = 50000;
const TYPE_CHECK_STUBS = `def sleep(duration: float) -> None:
    ...
`;
const GAP_PATTERN =
    /\[\[__gap(?::([A-Za-z0-9_-]+))?(?:\|(\d{1,3}))?__\]\]([^\n]*?)\[\[\/__gap__\]\]/g;
const GAP_DEFAULT_WIDTH_CHARS = 8;
const GAP_WIDTH_MIN_CHARS = 1;
const GAP_WIDTH_MAX_CHARS = 60;
const GAP_WIDTH_BASE_CH = 2;
const GAP_WIDTH_PER_CHAR_CH = 1.1;
const GAP_INPUT_MAX_WIDTH_CH = 60;

const PYTHON_KEYWORDS = new Set([
    "False",
    "None",
    "True",
    "and",
    "as",
    "assert",
    "async",
    "await",
    "break",
    "class",
    "continue",
    "def",
    "del",
    "elif",
    "else",
    "except",
    "finally",
    "for",
    "from",
    "global",
    "if",
    "import",
    "in",
    "is",
    "lambda",
    "nonlocal",
    "not",
    "or",
    "pass",
    "raise",
    "return",
    "try",
    "while",
    "with",
    "yield",
]);

const PYTHON_BUILTINS = new Set([
    "abs",
    "all",
    "any",
    "bool",
    "dict",
    "enumerate",
    "filter",
    "float",
    "input",
    "int",
    "len",
    "list",
    "map",
    "max",
    "min",
    "open",
    "print",
    "range",
    "round",
    "set",
    "sorted",
    "str",
    "sum",
    "tuple",
    "type",
    "zip",
]);

let tutorialContents = [];

// Initialisierung beim Laden der Seite
document.addEventListener("DOMContentLoaded", function () {
    initializeTutorialData();
    initializeOutputTabs();
    initializeResizer();
    initializeControls();
    initializePythonWorker();
    initializeTemplateFromDefault();
    loadSavedContent();
    initializeTaskContent();
    initializeTutorialNavigation();
    ObjectViewer.init();
    updateSaveStatus("saved");
    applyFontSizeSetting(currentEditorFontSize);
});

function initializeTutorialData() {
    const tutorialElement = document.getElementById("tutorial");
    const tutorialText = tutorialElement
        ? tutorialElement.textContent.trim()
        : "";

    if (!tutorialText) {
        tutorialContents = [];
        return;
    }

    try {
        const parsed = new Function(`return (${tutorialText});`)();
        tutorialContents = Array.isArray(parsed) ? parsed : [];
    } catch (error) {
        tutorialContents = [];
        console.error("Fehler beim Parsen des Tutorial-Inhalts:", error);
    }
}

function normalizeLineBreaks(text) {
    return (text || "").replace(/\r\n/g, "\n");
}

function extractTemplateFromDefault(rawDefault) {
    const trimmed = (rawDefault || "").trim();
    if (!trimmed) {
        return "";
    }

    try {
        const parsed = JSON.parse(trimmed);
        if (typeof parsed === "string") {
            return normalizeLineBreaks(parsed);
        }
        if (parsed && typeof parsed.pythonCode === "string") {
            return normalizeLineBreaks(parsed.pythonCode);
        }
    } catch {
        // Nicht JSON, dann als direkten Code behandeln
    }

    return normalizeLineBreaks(trimmed);
}

function initializeTemplateFromDefault() {
    const defaultElement = document.getElementById("defaultSubmission");
    const rawDefault = defaultElement ? defaultElement.textContent : "";
    const templateCode = extractTemplateFromDefault(rawDefault);

    setTemplateCode(templateCode);

    if (!parsedTemplate || !parsedTemplate.hasGaps) {
        addToConsole(
            "Hinweis: Keine Lückenmarkierungen gefunden. Der Code ist vollständig vorgegeben.",
            "info",
        );
    }
}

function setTemplateCode(templateCode) {
    teacherTemplateCode = normalizeLineBreaks(templateCode);
    parsedTemplate = parseTemplateCode(teacherTemplateCode);
    gapInputElements.clear();
    studentGapAnswers = filterAnswersForTemplate(
        studentGapAnswers,
        parsedTemplate,
    );
    renderTemplateCode();
}

function filterAnswersForTemplate(answers, template) {
    if (!template || !Array.isArray(template.gapDefinitions)) {
        return {};
    }

    const next = {};
    template.gapDefinitions.forEach((gap) => {
        if (Object.prototype.hasOwnProperty.call(answers, gap.key)) {
            next[gap.key] = String(answers[gap.key]);
        }
    });
    return next;
}

function parseTemplateCode(templateCode) {
    const normalized = normalizeLineBreaks(templateCode);
    const lines = normalized.split("\n");
    const parsedLines = [];
    const gapDefinitions = [];
    let gapCounter = 0;

    lines.forEach((lineText, lineIndex) => {
        const segments = [];
        const regex = new RegExp(GAP_PATTERN);
        let cursor = 0;
        let match = null;

        while ((match = regex.exec(lineText)) !== null) {
            if (match.index > cursor) {
                segments.push({
                    type: "text",
                    text: lineText.slice(cursor, match.index),
                });
            }

            const markerId = match[1] || "";
            const configuredWidthChars = match[2]
                ? parseInt(match[2], 10)
                : null;
            const defaultText = match[3] || "";
            const key = `gap_${gapCounter}`;
            const gapDefinition = {
                key,
                markerId,
                widthChars: configuredWidthChars,
                defaultText,
                lineNumber: lineIndex + 1,
                startColumn: match.index + 1,
            };

            gapDefinitions.push(gapDefinition);
            segments.push({
                type: "gap",
                ...gapDefinition,
            });

            gapCounter += 1;
            cursor = regex.lastIndex;
        }

        if (cursor < lineText.length) {
            segments.push({
                type: "text",
                text: lineText.slice(cursor),
            });
        }

        if (segments.length === 0) {
            segments.push({
                type: "text",
                text: "",
            });
        }

        parsedLines.push({
            lineNumber: lineIndex + 1,
            segments,
        });
    });

    return {
        rawCode: normalized,
        lines: parsedLines,
        gapDefinitions,
        hasGaps: gapDefinitions.length > 0,
    };
}

function tokenizePythonText(text) {
    const tokens = [];
    let i = 0;

    while (i < text.length) {
        const ch = text[i];

        if (ch === "#") {
            tokens.push({ type: "comment", text: text.slice(i) });
            break;
        }

        if (ch === '"' || ch === "'") {
            const quote = ch;
            let j = i + 1;

            while (j < text.length) {
                if (text[j] === "\\") {
                    j += 2;
                    continue;
                }
                if (text[j] === quote) {
                    j += 1;
                    break;
                }
                j += 1;
            }

            tokens.push({
                type: "string",
                text: text.slice(i, Math.min(j, text.length)),
            });
            i = j;
            continue;
        }

        if (/\d/.test(ch)) {
            let j = i + 1;
            while (j < text.length && /[\d_\.eE]/.test(text[j])) {
                j += 1;
            }
            tokens.push({ type: "number", text: text.slice(i, j) });
            i = j;
            continue;
        }

        if (/[A-Za-z_]/.test(ch)) {
            let j = i + 1;
            while (j < text.length && /[A-Za-z0-9_]/.test(text[j])) {
                j += 1;
            }

            const word = text.slice(i, j);
            if (PYTHON_KEYWORDS.has(word)) {
                tokens.push({ type: "keyword", text: word });
            } else if (PYTHON_BUILTINS.has(word)) {
                tokens.push({ type: "builtin", text: word });
            } else {
                tokens.push({ type: "text", text: word });
            }

            i = j;
            continue;
        }

        if ("=+-*/%<>!&|^~:,.;()[]{}".includes(ch)) {
            tokens.push({ type: "operator", text: ch });
            i += 1;
            continue;
        }

        tokens.push({ type: "text", text: ch });
        i += 1;
    }

    return tokens;
}

function appendHighlightedText(targetElement, text) {
    const tokens = tokenizePythonText(text);

    tokens.forEach((token) => {
        if (token.type === "text") {
            targetElement.appendChild(document.createTextNode(token.text));
            return;
        }

        const span = document.createElement("span");
        span.className = `py-token py-${token.type}`;
        span.textContent = token.text;
        targetElement.appendChild(span);
    });
}

function adjustGapInputWidth(inputElement) {
    const minWidthCh = parseFloat(inputElement.dataset.minWidthCh || "0");
    const currentLength = inputElement.value.length;
    const dynamicWidthCh = calculateGapInputWidthCh(currentLength);
    const widthCh = Math.max(minWidthCh, dynamicWidthCh);
    inputElement.style.width = `${Math.min(GAP_INPUT_MAX_WIDTH_CH, widthCh)}ch`;
}

function parseGapWidthChars(rawWidth) {
    const parsed = parseInt(rawWidth, 10);
    if (Number.isNaN(parsed)) {
        return GAP_DEFAULT_WIDTH_CHARS;
    }

    return Math.max(GAP_WIDTH_MIN_CHARS, Math.min(GAP_WIDTH_MAX_CHARS, parsed));
}

function calculateGapInputWidthCh(chars) {
    const safeChars = Math.max(1, parseInt(chars, 10) || 0);
    return GAP_WIDTH_BASE_CH + safeChars * GAP_WIDTH_PER_CHAR_CH;
}

function createGapInput(gap) {
    const input = document.createElement("input");
    input.type = "text";
    input.className = "code-gap-input";
    input.dataset.gapKey = gap.key;

    const minChars = parseGapWidthChars(gap.widthChars);
    input.dataset.minWidthCh = String(calculateGapInputWidthCh(minChars));
    input.placeholder = "";
    input.value = Object.prototype.hasOwnProperty.call(
        studentGapAnswers,
        gap.key,
    )
        ? studentGapAnswers[gap.key]
        : "";

    input.setAttribute(
        "aria-label",
        `Eingabefeld Zeile ${gap.lineNumber}${gap.markerId ? ` (${gap.markerId})` : ""}`,
    );

    adjustGapInputWidth(input);

    input.addEventListener("input", function () {
        studentGapAnswers[gap.key] = input.value;
        if (input.value === "") {
            delete studentGapAnswers[gap.key];
        }
        adjustGapInputWidth(input);
        updateSaveStatus("ready");
    });

    return input;
}

function renderTemplateCode() {
    const editorContainer = document.getElementById("pythonEditor");
    if (!editorContainer || !parsedTemplate) {
        return;
    }

    gapInputElements.clear();
    editorContainer.classList.add("template-editor");
    editorContainer.innerHTML = "";

    const wrapper = document.createElement("div");
    wrapper.id = "codeTemplateWrapper";
    wrapper.className = "code-template-wrapper";

    const codeElement = document.createElement("div");
    codeElement.id = "codeTemplate";
    codeElement.className = "code-template";

    parsedTemplate.lines.forEach((line) => {
        const lineRow = document.createElement("div");
        lineRow.className = "code-line";

        const hasGap = line.segments.some((segment) => segment.type === "gap");
        if (hasGap) {
            lineRow.classList.add("code-line-has-gap");
        }

        const lineNumber = document.createElement("span");
        lineNumber.className = "code-line-number";
        lineNumber.textContent = String(line.lineNumber);

        const lineContent = document.createElement("span");
        lineContent.className = "code-line-content";

        line.segments.forEach((segment) => {
            if (segment.type === "text") {
                appendHighlightedText(lineContent, segment.text);
                return;
            }

            const input = createGapInput(segment);
            gapInputElements.set(segment.key, input);
            lineContent.appendChild(input);
        });

        lineRow.appendChild(lineNumber);
        lineRow.appendChild(lineContent);
        codeElement.appendChild(lineRow);
    });

    wrapper.appendChild(codeElement);

    editorContainer.appendChild(wrapper);
    applyFontSizeSetting(currentEditorFontSize);
}

function buildExecutableCode() {
    if (!parsedTemplate) {
        return "";
    }

    return parsedTemplate.lines
        .map((line) => {
            return line.segments
                .map((segment) => {
                    if (segment.type === "text") {
                        return segment.text;
                    }

                    if (
                        Object.prototype.hasOwnProperty.call(
                            studentGapAnswers,
                            segment.key,
                        )
                    ) {
                        return studentGapAnswers[segment.key];
                    }

                    return "";
                })
                .join("");
        })
        .join("\n");
}

function normalizeTypeDiagnostics(rawDiagnostics, stubLineCount = 0) {
    if (!Array.isArray(rawDiagnostics)) {
        return [];
    }

    return rawDiagnostics
        .map((entry) => {
            if (!Array.isArray(entry) || entry.length < 3) {
                return null;
            }

            const rawLine = parseInt(entry[0], 10);
            if (Number.isNaN(rawLine)) {
                return null;
            }

            const lineNumber = rawLine - stubLineCount;
            if (lineNumber < 1) {
                return null;
            }

            const severity = String(entry[1] || "")
                .toLowerCase()
                .includes("warning")
                ? "warning"
                : "error";
            const message = String(entry[2] || "").trim();
            if (!message) {
                return null;
            }

            return {
                lineNumber,
                severity,
                message,
            };
        })
        .filter(Boolean)
        .sort((a, b) => {
            if (a.lineNumber !== b.lineNumber) {
                return a.lineNumber - b.lineNumber;
            }
            if (a.severity === b.severity) {
                return 0;
            }
            return a.severity === "error" ? -1 : 1;
        });
}

async function ensureTypeCheckerReady() {
    if (mypyReady && mainThreadPyodide) {
        return true;
    }

    if (mypyInitializationPromise) {
        await mypyInitializationPromise;
        return mypyReady && !!mainThreadPyodide;
    }

    if (!mypyInitializationTried) {
        mypyInitializationTried = true;
        mypyInitializationPromise = initializeMyPy().finally(() => {
            mypyInitializationPromise = null;
        });
        await mypyInitializationPromise;
    }

    return mypyReady && !!mainThreadPyodide;
}

async function runTypeCheckForCode(code) {
    if (!code || !code.trim()) {
        return [];
    }

    const checkerReady = await ensureTypeCheckerReady();
    if (!checkerReady) {
        return null;
    }

    try {
        const stubLineCount = TYPE_CHECK_STUBS.trimEnd().split("\n").length;
        const codeToCheck = `${TYPE_CHECK_STUBS}${code}`;

        mainThreadPyodide.globals.set("code_to_check", codeToCheck);
        const pyResult = await mainThreadPyodide.runPythonAsync(
            "type_checker.check_code(code_to_check)",
        );

        const rawDiagnostics =
            pyResult && typeof pyResult.toJs === "function"
                ? pyResult.toJs()
                : [];

        if (pyResult && typeof pyResult.destroy === "function") {
            pyResult.destroy();
        }

        return normalizeTypeDiagnostics(rawDiagnostics, stubLineCount);
    } catch (error) {
        console.error("Type-Checking-Fehler:", error);
        return null;
    } finally {
        try {
            if (
                mainThreadPyodide &&
                mainThreadPyodide.globals &&
                typeof mainThreadPyodide.globals.delete === "function"
            ) {
                mainThreadPyodide.globals.delete("code_to_check");
            }
        } catch {
            // ignore cleanup errors
        }
    }
}

function reportTypeDiagnosticsToConsole(diagnostics) {
    if (!Array.isArray(diagnostics) || diagnostics.length === 0) {
        return;
    }

    const errorCount = diagnostics.filter((entry) => entry.severity === "error")
        .length;
    const warningCount = diagnostics.filter(
        (entry) => entry.severity === "warning",
    ).length;

    const parts = [];
    if (errorCount > 0) {
        parts.push(`${errorCount} Fehler`);
    }
    if (warningCount > 0) {
        parts.push(`${warningCount} Warnung${warningCount === 1 ? "" : "en"}`);
    }

    addToConsole(
        `Type-Checking vor Ausführung: ${parts.join(", ")}`,
        errorCount > 0 ? "error" : "warning",
    );

    diagnostics.slice(0, 20).forEach((entry) => {
        addToConsole(
            `Zeile ${entry.lineNumber}: ${entry.message}`,
            entry.severity === "error" ? "error" : "warning",
        );
    });

    if (diagnostics.length > 20) {
        addToConsole(
            `${diagnostics.length - 20} weitere Type-Checking-Meldungen ausgeblendet.`,
            "warning",
        );
    }
}

function initializeFallbackChecker() {
    if (!mainThreadPyodide) {
        return false;
    }

    try {
        mainThreadPyodide.runPython(`
import ast

class FallbackTypeChecker:
    def check_code(self, code):
        try:
            ast.parse(code)
            return []
        except SyntaxError as e:
            return [(e.lineno or 1, "error", f"Syntax-Fehler: {e.msg}")]
        except Exception as e:
            return [(1, "error", f"Code-Fehler: {str(e)}")]

type_checker = FallbackTypeChecker()
        `);
        return true;
    } catch (error) {
        console.error("Fallback-Checker Initialisierung fehlgeschlagen:", error);
        return false;
    }
}

async function initializeMyPy() {
    if (mypyReady && mainThreadPyodide) {
        return;
    }

    try {
        if (typeof loadPyodide === "undefined") {
            throw new Error("loadPyodide ist nicht verfügbar");
        }

        addToConsole("Type-Checking wird initialisiert...", "info");

        mainThreadPyodide = await loadPyodide({
            indexURL: "https://cdn.jsdelivr.net/pyodide/v0.24.1/full/",
        });

        await mainThreadPyodide.loadPackage(["micropip"]);
        await mainThreadPyodide.runPythonAsync(`
import micropip
await micropip.install(["typing-extensions", "mypy-extensions"])
        `);
        await mainThreadPyodide.loadPackage(["mypy"]);

        mainThreadPyodide.runPython(`
import os
import re
import mypy.api
import tempfile

class RobustTypeChecker:
    def __init__(self):
        self.temp_dir = tempfile.mkdtemp()

    def _parse_output(self, output, temp_file):
        errors = []
        pattern = re.compile(r"^.*?:(\\d+):(?:(\\d+):)?\\s*(error|warning|note):\\s*(.*)$")

        for raw_line in (output or "").splitlines():
            line = raw_line.strip()
            if not line or temp_file not in line:
                continue

            match = pattern.match(line)
            if not match:
                continue

            try:
                line_num = int(match.group(1))
            except ValueError:
                continue

            level_token = (match.group(3) or "").strip().lower()
            message = (match.group(4) or line).strip()
            severity = "warning" if "warning" in level_token else "error"
            errors.append((line_num, severity, message))
        return errors

    def check_code(self, code):
        temp_file = os.path.join(self.temp_dir, "check_code.py")
        try:
            with open(temp_file, "w", encoding="utf-8") as handle:
                handle.write(code)

            stdout, stderr, _ = mypy.api.run([
                temp_file,
                "--no-error-summary",
                "--no-color-output",
                "--ignore-missing-imports",
                "--follow-imports=silent",
            ])

            errors = self._parse_output(stdout, temp_file)
            if stderr and stderr.strip():
                errors.append((1, "error", stderr.strip()))
            return errors
        except Exception as exc:
            return [(1, "error", f"MyPy-Fehler: {str(exc)}")]
        finally:
            try:
                if os.path.exists(temp_file):
                    os.remove(temp_file)
            except Exception:
                pass

type_checker = RobustTypeChecker()
        `);

        mypyReady = true;
        addToConsole("MyPy Type-Checking aktiv ✓", "info");
    } catch (error) {
        console.error("MyPy Initialisierung fehlgeschlagen:", error);

        const fallbackReady = initializeFallbackChecker();
        if (fallbackReady) {
            mypyReady = true;
            addToConsole("Fallback-Type-Checking aktiv ✓", "warning");
        } else {
            mypyReady = false;
            addToConsole("Type-Checking konnte nicht gestartet werden.", "error");
        }
    }
}

function resolveAppUrl(path) {
    if (!path) {
        return "";
    }

    const defaultLink = document.getElementById("default-link");
    const baseHref =
        (defaultLink && defaultLink.getAttribute("href")) ||
        window.location.pathname ||
        "/";
    const normalizedBase = baseHref.endsWith("/") ? baseHref : baseHref + "/";

    try {
        return new URL(
            path,
            window.location.origin + normalizedBase,
        ).toString();
    } catch {
        return path;
    }
}

// Output-Tab-Navigation initialisieren
function initializeOutputTabs() {
    const outputTabs = document.querySelectorAll(
        ".output-tab[data-output-tab]",
    );
    const outputContents = document.querySelectorAll(".output-content");
    const clearOutputBtn = document.getElementById("clearOutputBtn");

    outputTabs.forEach((tab) => {
        tab.addEventListener("click", function () {
            const tabName = this.getAttribute("data-output-tab");

            outputTabs.forEach((t) => t.classList.remove("active"));
            outputContents.forEach((oc) => oc.classList.remove("active"));

            this.classList.add("active");
            const target = document.getElementById(tabName + "Output");
            if (target) {
                target.classList.add("active");
            }

            if (tabName === "result") {
                clearOutputBtn.classList.add("show");
            } else {
                clearOutputBtn.classList.remove("show");
            }
        });
    });

    clearOutputBtn.classList.add("show");
}

// Resizable Splitter initialisieren
function initializeResizer() {
    const splitter = document.getElementById("splitter");
    const editorPanel = document.querySelector(".editor-panel");
    const outputPanel = document.querySelector(".output-panel");
    const container = document.querySelector(".main-content");

    if (!splitter || !editorPanel || !outputPanel || !container) {
        return;
    }

    splitter.addEventListener("mousedown", startResize);
    splitter.addEventListener("touchstart", startResize);

    function startResize(e) {
        e.preventDefault();
        isResizing = true;

        if (e.type === "mousedown") {
            document.addEventListener("mousemove", handleMouseMove);
            document.addEventListener("mouseup", stopResize);
        } else if (e.type === "touchstart") {
            document.addEventListener("touchmove", handleTouchMove, {
                passive: false,
            });
            document.addEventListener("touchend", stopResize);
        }

        document.body.style.userSelect = "none";
        splitter.style.backgroundColor = "#007acc";
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
            const mouseY = clientY - containerRect.top;
            const containerHeight = containerRect.height;
            const minHeight = 200;
            const maxHeight = containerHeight - 200;

            const newHeight = Math.max(minHeight, Math.min(maxHeight, mouseY));
            const topPercent = (newHeight / containerHeight) * 100;
            const bottomPercent = 100 - topPercent;

            editorPanel.style.height = topPercent + "%";
            outputPanel.style.height = bottomPercent + "%";
            return;
        }

        const mouseX = clientX - containerRect.left;
        const containerWidth = containerRect.width;
        const minWidth = 300;
        const maxWidth = containerWidth - 300;

        const newWidth = Math.max(minWidth, Math.min(maxWidth, mouseX));
        const leftPercent = (newWidth / containerWidth) * 100;
        const rightPercent = 100 - leftPercent;

        editorPanel.style.width = leftPercent + "%";
        outputPanel.style.width = rightPercent + "%";
    }

    function stopResize() {
        isResizing = false;
        document.removeEventListener("mousemove", handleMouseMove);
        document.removeEventListener("mouseup", stopResize);
        document.removeEventListener("touchmove", handleTouchMove);
        document.removeEventListener("touchend", stopResize);

        document.body.style.userSelect = "";
        splitter.style.backgroundColor = "";
    }
}

function getStoredFontSize() {
    const rawValue = localStorage.getItem("editorFontSize");
    if (!rawValue) {
        return 14;
    }

    const parsed = parseInt(rawValue, 10);
    if (Number.isNaN(parsed) || parsed < 10 || parsed > 40) {
        return 14;
    }

    return parsed;
}

function applyFontSizeSetting(fontSize) {
    currentEditorFontSize = fontSize;

    const wrapper = document.getElementById("codeTemplateWrapper");
    if (wrapper) {
        wrapper.style.setProperty("--template-font-size", `${fontSize}px`);
    }

    const consoleOutput = document.getElementById("consoleOutput");
    if (consoleOutput) {
        consoleOutput.style.fontSize = fontSize + "px";
    }

    gapInputElements.forEach((input) => adjustGapInputWidth(input));
}

// Steuerungselemente initialisieren
function initializeControls() {
    const fontSizeSelect = document.getElementById("fontSizeSelect");
    const fullscreenBtn = document.getElementById("fullscreenBtn");
    const clearOutputBtn = document.getElementById("clearOutputBtn");
    const runBtn = document.getElementById("runBtn");
    const saveButton = document.getElementById("saveButton");
    const submitButton = document.getElementById("submitButton");
    const resetBtn = document.getElementById("resetBtn");

    currentEditorFontSize = getStoredFontSize();
    if (fontSizeSelect) {
        fontSizeSelect.value = String(currentEditorFontSize);
        fontSizeSelect.addEventListener("change", function () {
            const fontSize = parseInt(this.value, 10);
            if (Number.isNaN(fontSize)) {
                return;
            }
            applyFontSizeSetting(fontSize);
            localStorage.setItem("editorFontSize", String(fontSize));
        });
    }

    if (fullscreenBtn) {
        fullscreenBtn.addEventListener("click", function () {
            if (!document.fullscreenElement) {
                document.documentElement.requestFullscreen().catch((err) => {
                    console.error(
                        "Fehler beim Aktivieren des Vollbildmodus:",
                        err,
                    );
                });
            } else {
                document.exitFullscreen();
            }
        });

        document.addEventListener("fullscreenchange", function () {
            const icon = fullscreenBtn.querySelector("i");
            if (!icon) {
                return;
            }

            if (document.fullscreenElement) {
                icon.classList.remove("fa-expand");
                icon.classList.add("fa-compress");
                fullscreenBtn.title = "Vollbild verlassen";
            } else {
                icon.classList.remove("fa-compress");
                icon.classList.add("fa-expand");
                fullscreenBtn.title = "Vollbild";
            }
        });
    }

    if (clearOutputBtn) {
        clearOutputBtn.addEventListener("click", function () {
            const consoleOutput = document.getElementById("consoleOutput");
            if (consoleOutput) {
                consoleOutput.innerHTML = "";
            }
        });
    }

    if (runBtn) {
        runBtn.addEventListener("click", function () {
            if (isExecuting) {
                stopPythonExecution();
            } else if (pyodideReady) {
                runPythonCode();
            } else {
                addToConsole("Pyodide ist noch nicht bereit", "warning");
            }
        });
    }

    if (saveButton) {
        saveButton.addEventListener("click", function () {
            saveContent();
        });
    }

    if (submitButton) {
        submitButton.addEventListener("click", function () {
            submitTask();
        });
    }

    if (resetBtn) {
        resetBtn.addEventListener("click", function () {
            resetToDefault();
        });
    }
}

function addToConsole(text, type = "info") {
    const consoleOutput = document.getElementById("consoleOutput");
    if (!consoleOutput) {
        return;
    }

    const timestamp = new Date().toLocaleTimeString();
    const prefix = type === "error" ? "❌" : type === "warning" ? "⚠️" : "ℹ️";

    const escapedText = String(text)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");

    consoleOutput.innerHTML += `[${timestamp}] ${prefix} ${escapedText}<br>`;

    if (consoleOutput.innerHTML.length > MAX_CONSOLE_SIZE) {
        const excessChars =
            consoleOutput.innerHTML.length - MAX_CONSOLE_SIZE + 2000;
        consoleOutput.innerHTML =
            "...[frühere Ausgaben entfernt]...<br>" +
            consoleOutput.innerHTML.substring(excessChars);
    }

    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

function addToConsoleWithoutTimestamp(text) {
    const consoleOutput = document.getElementById("consoleOutput");
    if (!consoleOutput) {
        return;
    }

    const escapedText = String(text)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/\n/g, "<br>");

    consoleOutput.innerHTML += escapedText + "<br>";

    if (consoleOutput.innerHTML.length > MAX_CONSOLE_SIZE) {
        const excessChars =
            consoleOutput.innerHTML.length - MAX_CONSOLE_SIZE + 2000;
        consoleOutput.innerHTML =
            "...[frühere Ausgaben entfernt]...<br>" +
            consoleOutput.innerHTML.substring(excessChars);
    }

    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

// Python Worker initialisieren
function initializePythonWorker() {
    try {
        const defaultLink = document.getElementById("default-link");
        const baseUrl = defaultLink
            ? defaultLink.getAttribute("href") || "/"
            : "/";
        pythonWorker = new Worker(baseUrl + "js/python-worker.js");

        pythonWorker.onmessage = function (e) {
            handleWorkerMessage(e.data);
        };

        pythonWorker.onerror = function (error) {
            addToConsole("Worker Fehler: " + error.message, "error");
            pyodideReady = false;
            updateRunButton("error");
        };

        pythonWorker.postMessage({ type: "init" });
    } catch (error) {
        addToConsole(
            "Fehler beim Erstellen des Workers: " + error.message,
            "error",
        );
        pyodideReady = false;
    }
}

// Python-Code ausführen (Web Worker)
async function runPythonCode() {
    if (!pyodideReady) {
        addToConsole(
            "Pyodide ist noch nicht bereit. Bitte warten...",
            "warning",
        );
        return;
    }

    if (isExecuting || isPreparingExecution) {
        addToConsole("Code wird bereits ausgeführt...", "warning");
        return;
    }

    const code = buildExecutableCode();
    if (!code.trim()) {
        addToConsole("Kein Python-Code zum Ausführen vorhanden.", "warning");
        return;
    }

    isPreparingExecution = true;
    try {
        const typeDiagnostics = await runTypeCheckForCode(code);
        if (typeDiagnostics === null) {
            addToConsole(
                "Type-Checking ist aktuell nicht verfügbar. Ausführung wird fortgesetzt.",
                "warning",
            );
        } else {
            reportTypeDiagnosticsToConsole(typeDiagnostics);
        }

        isExecuting = true;
        currentExecutionId = Date.now();
        updateRunButton("executing");

        pythonWorker.postMessage({
            type: "execute",
            data: {
                code: code,
                id: currentExecutionId,
            },
        });
    } finally {
        isPreparingExecution = false;
    }
}

// Python-Ausführung stoppen (mit Worker-Terminierung für Infinite Loops)
function stopPythonExecution() {
    if (!isExecuting) {
        return;
    }

    updateRunButton("stopping");
    addToConsole("Stoppe Ausführung...", "info");
    pythonWorker.postMessage({ type: "stop" });

    setTimeout(() => {
        if (isExecuting) {
            addToConsole(
                "Stopp-Button gedrückt, Programm wird angehalten...",
                "warning",
            );
            terminateAndRecreateWorker();
        }
    }, 300);
}

// Worker hart terminieren und neu erstellen (für Infinite Loops)
function terminateAndRecreateWorker() {
    try {
        if (pythonWorker) {
            pythonWorker.terminate();
        }

        pyodideReady = false;
        isExecuting = false;
        currentExecutionId = null;

        updateRunButton("loading");
        addToConsole("Worker wird neu initialisiert...", "info");

        initializePythonWorker();
    } catch (error) {
        addToConsole(
            "Fehler beim Neustart des Workers: " + error.message,
            "error",
        );
        updateRunButton("error");
    }
}

// Input-Anfrage vom Worker behandeln
function handleInputRequest(promptText) {
    const consoleOutput = document.getElementById("consoleOutput");
    if (!consoleOutput) {
        return;
    }

    if (promptText) {
        const promptSpan = document.createElement("span");
        promptSpan.className = "console-prompt-text";
        promptSpan.textContent = promptText;
        consoleOutput.appendChild(promptSpan);
    }

    const inputContainer = document.createElement("span");
    inputContainer.className = "console-input-container";

    const inputField = document.createElement("input");
    inputField.type = "text";
    inputField.className = "console-input-field";
    inputField.setAttribute("autocomplete", "off");
    inputField.setAttribute("autocorrect", "off");
    inputField.setAttribute("autocapitalize", "off");
    inputField.setAttribute("spellcheck", "false");

    inputContainer.appendChild(inputField);
    consoleOutput.appendChild(inputContainer);
    consoleOutput.scrollTop = consoleOutput.scrollHeight;
    inputField.focus();

    function handleSubmit(e) {
        if (e.key !== "Enter") {
            return;
        }

        e.preventDefault();
        const userInput = inputField.value;

        const inputText = document.createElement("span");
        inputText.className = "console-input-text";
        inputText.textContent = userInput;
        inputContainer.replaceWith(inputText);

        consoleOutput.appendChild(document.createElement("br"));

        pythonWorker.postMessage({
            type: "input_response",
            data: userInput,
        });
    }

    inputField.addEventListener("keydown", handleSubmit);

    function focusInput(e) {
        if (e.target !== inputField) {
            inputField.focus();
        }
    }

    consoleOutput.addEventListener("click", focusInput);

    const observer = new MutationObserver(() => {
        if (!document.contains(inputField)) {
            consoleOutput.removeEventListener("click", focusInput);
            observer.disconnect();
        }
    });
    observer.observe(consoleOutput, { childList: true, subtree: true });
}

// Worker Message Handler
function handleWorkerMessage(message) {
    const { type, data, message: msg } = message;

    switch (type) {
        case "worker_ready":
            break;

        case "status":
            if (data === "loading") {
                addToConsole(msg, "info");
            } else if (data === "ready") {
                pyodideReady = true;
                addToConsole(msg, "info");
                updateRunButton("ready");
            } else if (data === "executing") {
                addToConsole(msg, "info");
            } else if (data === "stopped" || data === "interrupted") {
                isExecuting = false;
                currentExecutionId = null;
                updateRunButton("ready");
                addToConsole(msg, "info");
            }
            break;

        case "output":
            addToConsoleWithoutTimestamp(data);
            break;

        case "objects_clear":
            ObjectViewer.clear();
            break;

        case "object_created":
            ObjectViewer.createObject(data);
            break;

        case "object_updated":
            ObjectViewer.updateObject(data);
            break;

        case "object_deleted":
            ObjectViewer.deleteObject(data.id);
            break;

        case "input_request":
            handleInputRequest(data);
            break;

        case "completed":
            isExecuting = false;
            currentExecutionId = null;
            updateRunButton("ready");
            break;

        case "error":
            isExecuting = false;
            currentExecutionId = null;
            updateRunButton("ready");

            if (data === "not_ready") {
                addToConsole(
                    "Pyodide ist noch nicht bereit. Bitte warten...",
                    "warning",
                );
            } else if (data === "already_executing") {
                addToConsole("Code wird bereits ausgeführt...", "warning");
            } else if (data === "execution_error") {
                addToConsole(msg, "error");
            } else if (data === "initialization_failed") {
                pyodideReady = false;
                updateRunButton("error");
                addToConsole(msg, "error");
            } else {
                addToConsole("Unbekannter Fehler: " + msg, "error");
            }
            break;

        default:
            console.log("Unbekannte Worker-Nachricht:", type, data);
    }
}

// Run Button Status aktualisieren
function updateRunButton(state) {
    const runBtn = document.getElementById("runBtn");
    if (!runBtn) {
        return;
    }

    switch (state) {
        case "ready":
            runBtn.disabled = false;
            runBtn.style.opacity = "1";
            runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
            runBtn.className = "btn btn-primary";
            break;

        case "executing":
            runBtn.disabled = false;
            runBtn.style.opacity = "1";
            runBtn.innerHTML = '<i class="fas fa-stop"></i> Stoppen';
            runBtn.className = "btn btn-danger";
            break;

        case "stopping":
            runBtn.disabled = true;
            runBtn.style.opacity = "0.8";
            runBtn.innerHTML = '<i class="fas fa-pause"></i> Stoppt...';
            runBtn.className = "btn btn-warning";
            break;

        case "loading":
            runBtn.disabled = true;
            runBtn.style.opacity = "0.6";
            runBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Lädt...';
            runBtn.className = "btn btn-secondary";
            break;

        case "error":
            runBtn.disabled = true;
            runBtn.style.opacity = "0.6";
            runBtn.innerHTML =
                '<i class="fas fa-exclamation-triangle"></i> Fehler';
            runBtn.className = "btn btn-secondary";
            break;

        default:
            runBtn.disabled = true;
            runBtn.style.opacity = "0.6";
            runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
            runBtn.className = "btn btn-secondary";
    }
}

function resetToDefault() {
    if (
        !confirm(
            "Möchten Sie die Eingaben wirklich zurücksetzen? Der vorgegebene Code bleibt erhalten.",
        )
    ) {
        return;
    }

    studentGapAnswers = {};
    initializeTemplateFromDefault();
    updateSaveStatus("ready");
}

function renderMarkdown(markdownText) {
    if (typeof marked === "undefined") {
        return markdownText
            .replace(/^# (.*$)/gm, "<h1>$1</h1>")
            .replace(/^## (.*$)/gm, "<h2>$1</h2>")
            .replace(/^### (.*$)/gm, "<h3>$1</h3>")
            .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
            .replace(/\*(.*?)\*/g, "<em>$1</em>")
            .replace(/`(.*?)`/g, "<code>$1</code>")
            .replace(/^\- (.*$)/gm, "<ul><li>$1</li></ul>")
            .replace(/^\d+\. (.*$)/gm, "<ol><li>$1</li></ol>")
            .replace(/\n/g, "<br>");
    }

    let html = marked.parse(markdownText);

    if (typeof ace !== "undefined") {
        try {
            const staticHighlight = ace.require("ace/ext/static_highlight");
            if (staticHighlight) {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, "text/html");
                const codeBlocks = doc.querySelectorAll("pre code");

                codeBlocks.forEach((block) => {
                    const langClass = block.className.match(/language-(\w+)/);
                    const lang = langClass ? langClass[1] : "python";
                    const code = block.textContent.trimEnd();
                    const aceMode = "ace/mode/" + lang;

                    try {
                        const highlighted = staticHighlight.render(
                            code,
                            aceMode,
                            "ace/theme/a11y_dark",
                            1,
                            true,
                        );
                        const preElement = block.parentElement;
                        if (preElement && preElement.tagName === "PRE") {
                            preElement.outerHTML = highlighted.html;
                        }
                    } catch {
                        // Fallback ohne Highlighting
                    }
                });

                html = doc.body.innerHTML;
            }
        } catch {
            // Fallback ohne ACE Static Highlight
        }
    }

    return html;
}

// Task-Content initialisieren
function initializeTaskContent() {
    const description = document.getElementById("description")?.textContent;
    const taskTab = document.querySelector(
        '.output-tab[data-output-tab="task"]',
    );

    if (description && description.trim()) {
        const taskOutput = document.getElementById("taskOutput");
        if (taskOutput) {
            taskOutput.innerHTML = renderMarkdown(description);
        }
        if (taskTab) {
            taskTab.style.display = "";
        }
    } else if (taskTab) {
        taskTab.style.display = "none";
    }
}

// Tutorial Navigation initialisieren
function initializeTutorialNavigation() {
    const tutorialTab = document.querySelector(
        '.output-tab[data-output-tab="tutorial"]',
    );
    const tutorialNav = document.getElementById("tutorialNav");
    const tutorialDots = document.getElementById("tutorialDots");

    if (!tutorialContents || tutorialContents.length === 0) {
        if (tutorialTab) {
            tutorialTab.style.display = "none";
        }
        if (tutorialNav) {
            tutorialNav.style.display = "none";
        }
        return;
    }

    if (tutorialDots) {
        tutorialDots.innerHTML = tutorialContents
            .map(
                (_, index) =>
                    `<span class="tutorial-dot ${index === 0 ? "active" : ""}" data-index="${index}"></span>`,
            )
            .join("");
    }

    const prevBtn = document.getElementById("tutorialPrev");
    const nextBtn = document.getElementById("tutorialNext");

    if (prevBtn) {
        prevBtn.addEventListener("click", () => {
            if (currentTutorialIndex > 0) {
                currentTutorialIndex -= 1;
                updateTutorialDisplay();
            }
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener("click", () => {
            if (currentTutorialIndex < tutorialContents.length - 1) {
                currentTutorialIndex += 1;
                updateTutorialDisplay();
            }
        });
    }

    document.querySelectorAll(".tutorial-dot").forEach((dot) => {
        dot.addEventListener("click", (e) => {
            currentTutorialIndex = parseInt(e.target.dataset.index, 10);
            updateTutorialDisplay();
        });
    });

    updateTutorialDisplay();
}

function updateTutorialDisplay() {
    if (!tutorialContents || tutorialContents.length === 0) {
        return;
    }

    document.querySelectorAll(".tutorial-dot").forEach((dot, index) => {
        dot.classList.toggle("active", index === currentTutorialIndex);
    });

    const prevBtn = document.getElementById("tutorialPrev");
    const nextBtn = document.getElementById("tutorialNext");

    if (prevBtn) {
        prevBtn.disabled = currentTutorialIndex === 0;
    }
    if (nextBtn) {
        nextBtn.disabled = currentTutorialIndex === tutorialContents.length - 1;
    }

    const tutorialContent = document.getElementById("tutorialContent");
    if (!tutorialContent) {
        return;
    }

    const currentContent = tutorialContents[currentTutorialIndex].content || "";
    tutorialContent.innerHTML = renderMarkdown(currentContent);

    tutorialContent.querySelectorAll("a").forEach((link) => {
        link.setAttribute("target", "_blank");
        link.setAttribute("rel", "noopener noreferrer");
    });
}

function updateTaskTab(markdownText) {
    const taskOutput = document.getElementById("taskOutput");
    const taskTab = document.querySelector(
        '.output-tab[data-output-tab="task"]',
    );

    if (!taskOutput || !taskTab) {
        return;
    }

    if (markdownText && markdownText.trim() !== "") {
        taskOutput.innerHTML = renderMarkdown(markdownText);
        taskTab.style.display = "block";
        return;
    }

    taskOutput.innerHTML = "";
    taskTab.style.display = "none";
}

function buildPersistedAnswers() {
    const persisted = {};

    if (!parsedTemplate || !Array.isArray(parsedTemplate.gapDefinitions)) {
        return persisted;
    }

    parsedTemplate.gapDefinitions.forEach((gap) => {
        if (Object.prototype.hasOwnProperty.call(studentGapAnswers, gap.key)) {
            persisted[gap.key] = studentGapAnswers[gap.key];
        }
    });

    return persisted;
}

// TaskView-konforme Funktionen
function getContentFromView() {
    const gapAnswers = buildPersistedAnswers();

    const content = {
        version: "2.0",
        type: "python-gap-editor",
        gapAnswers,
        currentTutorialIndex: currentTutorialIndex,
        metadata: {
            lastModified: new Date().toISOString(),
            answerCount: Object.keys(gapAnswers).length,
            templateHasGaps: !!(parsedTemplate && parsedTemplate.hasGaps),
        },
    };

    return JSON.stringify(content);
}

function loadContentToView(content) {
    if (!content || content.trim() === "" || content === "{}") {
        return;
    }

    let data = null;
    try {
        data = JSON.parse(content);
    } catch {
        // Legacy-Format ohne JSON: ignorieren und mit Default-Template starten.
        return;
    }

    if (!data || typeof data !== "object") {
        return;
    }

    if (data && typeof data.gapAnswers === "object" && data.gapAnswers) {
        studentGapAnswers = { ...data.gapAnswers };
        studentGapAnswers = filterAnswersForTemplate(
            studentGapAnswers,
            parsedTemplate,
        );
        renderTemplateCode();
    }

    if (
        data.currentTutorialIndex !== undefined &&
        data.currentTutorialIndex >= 0 &&
        data.currentTutorialIndex < tutorialContents.length
    ) {
        currentTutorialIndex = data.currentTutorialIndex;
        updateTutorialDisplay();
    }

    updateSaveStatus("saved");
}

// Lade gespeicherte Inhalte beim Start
function loadSavedContent() {
    const contentElement = document.getElementById("currentContent");
    if (!contentElement) {
        return;
    }

    const savedContent = contentElement.textContent.trim();
    loadContentToView(savedContent);
}

function saveContent(isSubmission = false) {
    updateSaveStatus("saving");

    const content = getContentFromView();
    const urlElement = document.getElementById(
        isSubmission ? "task-submit-url" : "task-save-url",
    );
    const rawUrl = urlElement ? urlElement.getAttribute("data-url") || "" : "";
    const url = resolveAppUrl(rawUrl);

    if (!url) {
        console.error("Keine URL für Speicherung gefunden");
        updateSaveStatus("error");
        return;
    }

    fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content: content }),
    })
        .then((response) => {
            if (response.ok) {
                updateSaveStatus(isSubmission ? "submitted" : "saved");

                if (window.parent && window.parent !== window) {
                    window.parent.postMessage("content-saved", "*");
                }
            } else {
                updateSaveStatus("error");
                console.error("Speicherfehler:", response.status);
            }
        })
        .catch((error) => {
            console.error("Speicherfehler:", error);
            updateSaveStatus("error");
        });
}

function submitTask() {
    if (
        confirm(
            "Möchten Sie diese Aufgabe wirklich abgeben? Nach der Abgabe können Sie keine Änderungen mehr vornehmen.",
        )
    ) {
        saveContent(true);
    }
}

function updateSaveStatus(status) {
    const statusElement = document.getElementById("save-status");
    if (!statusElement) {
        return;
    }

    statusElement.className = "";

    switch (status) {
        case "saved":
            statusElement.className = "fas fa-circle text-success";
            statusElement.setAttribute("title", "Änderungen gespeichert");
            statusElement.style.color = "#28a745";
            break;
        case "saving":
            statusElement.className = "fas fa-spinner fa-spin text-primary";
            statusElement.setAttribute("title", "Speichere...");
            statusElement.style.color = "#007bff";
            break;
        case "error":
            statusElement.className = "fas fa-circle text-danger";
            statusElement.setAttribute("title", "Fehler beim Speichern");
            statusElement.style.color = "#dc3545";
            break;
        case "ready":
            statusElement.className = "fas fa-circle text-warning";
            statusElement.setAttribute("title", "Ungespeicherte Änderungen");
            statusElement.style.color = "#ffc107";
            break;
        case "submitted":
            statusElement.className = "fas fa-circle text-success";
            statusElement.setAttribute("title", "Aufgabe abgegeben");
            statusElement.style.color = "#28a745";
            break;
        default:
            statusElement.className = "fas fa-circle text-muted";
            statusElement.setAttribute("title", "Bereit zum Speichern");
            statusElement.style.color = "#6c757d";
            break;
    }
}

// Globale Funktionen für spätere Phasen
window.editorAPI = {
    getPythonCode: () => buildExecutableCode(),
    getCurrentEditor: () => null,
    setPythonCode: (code) => {
        studentGapAnswers = {};
        setTemplateCode(code || "");
        updateSaveStatus("ready");
    },
    getTemplateCode: () => teacherTemplateCode,
    getGapAnswers: () => ({ ...studentGapAnswers }),
    addToConsole: addToConsole,
    runPythonCode: runPythonCode,
    updateTaskTab: updateTaskTab,
    renderMarkdown: renderMarkdown,
    saveContent: saveContent,
    submitTask: submitTask,
    getContentFromView: getContentFromView,
    loadContentToView: loadContentToView,
};
