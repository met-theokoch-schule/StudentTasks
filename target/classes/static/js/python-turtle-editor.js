// Global variables
let pythonEditor;
let pyodide = null;
let pyodideReady = false;
let isResizing = false;
let typeCheckInterval = null;
let lastPythonCode = "";
let mypyReady = false;
let currentTutorialIndex = 0;
let isRunning = false;
let turtleCompleterRegistered = false;

const TURTLE_INSTANCE_METHODS = [
    "forward",
    "fd",
    "backward",
    "back",
    "bk",
    "right",
    "rt",
    "left",
    "lt",
    "up",
    "penup",
    "pu",
    "down",
    "pendown",
    "pd",
    "goto",
    "setpos",
    "setposition",
    "setx",
    "sety",
    "setheading",
    "seth",
    "home",
    "circle",
    "dot",
    "speed",
    "color",
    "pencolor",
    "fillcolor",
    "pensize",
    "width",
    "begin_fill",
    "end_fill",
    "begin_poly",
    "end_poly",
    "get_poly",
    "stamp",
    "clearstamp",
    "clearstamps",
    "undo",
    "reset",
    "clear",
    "hideturtle",
    "showturtle",
    "ht",
    "st",
    "isdown",
    "isvisible",
    "pos",
    "position",
    "xcor",
    "ycor",
    "heading",
    "distance",
    "towards",
    "clone",
    "shape",
    "shapesize",
    "turtlesize",
    "tilt",
    "tiltangle",
    "settiltangle",
    "shearfactor",
    "shapetransform",
    "resizemode",
    "filling",
    "write",
    "onclick",
    "ondrag",
    "onrelease",
    "setundobuffer",
];

const TURTLE_MEMBER_SNIPPETS = {
    Turtle: "Turtle()",
    Screen: "Screen()",
    done: "done()",
    mainloop: "mainloop()",
    show_scene: "show_scene()",
    restart: "restart()",
    replay_scene: "replay_scene()",
    addshape: "addshape(${1:name}, ${2:shape})",
    bgcolor: "bgcolor(${1:color})",
    bgpic: "bgpic(${1:picname})",
    bye: "bye()",
    clearscreen: "clearscreen()",
    colormode: "colormode(${1:cmode})",
    delay: "delay(${1:delay})",
    exitonclick: "exitonclick()",
    getcanvas: "getcanvas()",
    getshapes: "getshapes()",
    listen: "listen()",
    mode: "mode(${1:mode})",
    numinput: "numinput(${1:title}, ${2:prompt})",
    onkey: "onkey(${1:fun}, ${2:key})",
    onkeypress: "onkeypress(${1:fun}, ${2:key})",
    onkeyrelease: "onkeyrelease(${1:fun}, ${2:key})",
    onscreenclick: "onscreenclick(${1:fun})",
    ontimer: "ontimer(${1:fun}, ${2:t})",
    register_shape: "register_shape(${1:name}, ${2:shape})",
    resetscreen: "resetscreen()",
    save: "save(${1:filename})",
    screensize: "screensize(${1:canvwidth}, ${2:canvheight})",
    setup: "setup(${1:width}, ${2:height})",
    setworldcoordinates:
        "setworldcoordinates(${1:llx}, ${2:lly}, ${3:urx}, ${4:ury})",
    svg: "svg()",
    textinput: "textinput(${1:title}, ${2:prompt})",
    title: "title(${1:titlestring})",
    tracer: "tracer(${1:n}, ${2:delay})",
    turtles: "turtles()",
    update: "update()",
    window_height: "window_height()",
    window_width: "window_width()",
    forward: "forward(${1:distance})",
    fd: "fd(${1:distance})",
    backward: "backward(${1:distance})",
    back: "back(${1:distance})",
    bk: "bk(${1:distance})",
    right: "right(${1:angle})",
    rt: "rt(${1:angle})",
    left: "left(${1:angle})",
    lt: "lt(${1:angle})",
    up: "up()",
    penup: "penup()",
    pu: "pu()",
    down: "down()",
    pendown: "pendown()",
    pd: "pd()",
    goto: "goto(${1:x}, ${2:y})",
    setpos: "setpos(${1:x}, ${2:y})",
    setposition: "setposition(${1:x}, ${2:y})",
    setx: "setx(${1:x})",
    sety: "sety(${1:y})",
    setheading: "setheading(${1:to_angle})",
    seth: "seth(${1:to_angle})",
    home: "home()",
    circle: "circle(${1:radius})",
    dot: "dot(${1:size}, ${2:color})",
    speed: "speed(${1:speed})",
    color: "color(${1:pencolor})",
    pencolor: "pencolor(${1:color})",
    fillcolor: "fillcolor(${1:color})",
    pensize: "pensize(${1:width})",
    width: "width(${1:width})",
    begin_fill: "begin_fill()",
    end_fill: "end_fill()",
    begin_poly: "begin_poly()",
    end_poly: "end_poly()",
    get_poly: "get_poly()",
    stamp: "stamp()",
    clearstamp: "clearstamp(${1:stampid})",
    clearstamps: "clearstamps(${1:n})",
    undo: "undo()",
    reset: "reset()",
    clear: "clear()",
    hideturtle: "hideturtle()",
    showturtle: "showturtle()",
    ht: "ht()",
    st: "st()",
    isdown: "isdown()",
    isvisible: "isvisible()",
    pos: "pos()",
    position: "position()",
    xcor: "xcor()",
    ycor: "ycor()",
    heading: "heading()",
    distance: "distance(${1:x}, ${2:y})",
    towards: "towards(${1:x}, ${2:y})",
    clone: "clone()",
    shape: "shape(${1:name})",
    shapesize: "shapesize(${1:stretch_wid}, ${2:stretch_len}, ${3:outline})",
    turtlesize: "turtlesize(${1:stretch_wid}, ${2:stretch_len}, ${3:outline})",
    tilt: "tilt(${1:angle})",
    tiltangle: "tiltangle(${1:angle})",
    settiltangle: "settiltangle(${1:angle})",
    shearfactor: "shearfactor(${1:shear})",
    shapetransform: "shapetransform(${1:t11}, ${2:t12}, ${3:t21}, ${4:t22})",
    resizemode: "resizemode(${1:rmode})",
    filling: "filling()",
    write: "write(${1:arg})",
    onclick: "onclick(${1:fun})",
    ondrag: "ondrag(${1:fun})",
    onrelease: "onrelease(${1:fun})",
    setundobuffer: "setundobuffer(${1:size})",
};

const TURTLE_MODULE_MEMBERS = Array.from(
    new Set([
        "Turtle",
        "Screen",
        "done",
        "mainloop",
        "show_scene",
        "restart",
        "replay_scene",
        "addshape",
        "bgcolor",
        "bgpic",
        "bye",
        "clearscreen",
        "colormode",
        "delay",
        "exitonclick",
        "getcanvas",
        "getshapes",
        "listen",
        "mode",
        "numinput",
        "onkey",
        "onkeypress",
        "onkeyrelease",
        "onscreenclick",
        "ontimer",
        "register_shape",
        "resetscreen",
        "save",
        "screensize",
        "setup",
        "setworldcoordinates",
        "svg",
        "textinput",
        "title",
        "tracer",
        "turtles",
        "update",
        "window_height",
        "window_width",
        ...TURTLE_INSTANCE_METHODS,
    ]),
);

function buildTurtleStubText() {
    const moduleNames = TURTLE_MODULE_MEMBERS;
    const instanceMethods = TURTLE_INSTANCE_METHODS;
    const lines = [];

    lines.push("from typing import Any");
    lines.push("");
    lines.push("class Turtle:");
    lines.push("    def __getattr__(self, name: str) -> Any: ...");
    instanceMethods.forEach((name) => {
        lines.push(
            `    def ${name}(self, *args: Any, **kwargs: Any) -> Any: ...`,
        );
    });
    lines.push("");
    lines.push("class Screen:");
    lines.push("    def __getattr__(self, name: str) -> Any: ...");
    lines.push("");

    moduleNames.forEach((name) => {
        if (name === "Turtle" || name === "Screen") {
            return;
        }
        lines.push(`def ${name}(*args: Any, **kwargs: Any) -> Any: ...`);
    });

    lines.push("");
    lines.push(
        `__all__ = [${moduleNames.map((name) => `"${name}"`).join(", ")}]`,
    );

    return lines.join("\n");
}

// Initialize on DOM ready
document.addEventListener("DOMContentLoaded", function () {
    initializeEditors();
    initializeTabs();
    initializeOutputTabs();
    initializeResizer();
    initializeControls();
    initializePyodide();
    initializeTaskContent();
    initializeTutorialNavigation();

    loadSavedContent();
    pythonEditor.gotoLine(1);

    updateSaveStatus("saved");

    pythonEditor.on("change", function () {
        updateSaveStatus("ready");
    });

    const runBtn = document.getElementById("runBtn");
    if (runBtn) {
        runBtn.dataset.state = "execute";
    }

    configureSubmitButton();
});

// ACE Editor initialization
function initializeEditors() {
    try {
        pythonEditor = ace.edit("pythonEditor");
        pythonEditor.setTheme("ace/theme/a11y_dark");
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
            wrapBehavioursEnabled: true,
        });
        registerTurtleCompleter();

        console.log("Editor initialized");
    } catch (error) {
        console.error("Editor init error:", error);
    }
}

function registerTurtleCompleter() {
    if (turtleCompleterRegistered) {
        return;
    }

    const langTools = ace.require("ace/ext/language_tools");
    if (!langTools || typeof langTools.addCompleter !== "function") {
        return;
    }

    const turtleCompleter = {
        identifierRegexps: [/[a-zA-Z_0-9]/],
        getCompletions: function (editor, session, pos, prefix, callback) {
            const context = getTurtleCompletionContext(session, pos, prefix);
            if (!context) {
                callback(null, []);
                return;
            }

            const names =
                context.type === "module" || context.type === "star"
                    ? TURTLE_MODULE_MEMBERS
                    : TURTLE_INSTANCE_METHODS;
            const filtered = prefix
                ? names.filter((name) => name.startsWith(prefix))
                : names;
            const completions = filtered.map((name, index) => ({
                caption: toCompletionCaption(
                    TURTLE_MEMBER_SNIPPETS[name] || name + "()",
                ),
                value: name,
                snippet: TURTLE_MEMBER_SNIPPETS[name] || name + "()",
                score: 2000 - index,
                meta:
                    context.type === "module" || context.type === "star"
                        ? "turtle"
                        : "turtle instance",
            }));

            callback(null, completions);
        },
    };

    langTools.addCompleter(turtleCompleter);
    turtleCompleterRegistered = true;
}

function getTurtleCompletionContext(session, pos, prefix) {
    const linePrefix = session.getLine(pos.row).slice(0, pos.column);
    const dotMatch = linePrefix.match(/([A-Za-z_][\w]*)\.([A-Za-z_]*)$/);
    if (!dotMatch) {
        const symbolState = analyzeTurtleSymbols(session.getValue());
        if (symbolState.hasStarImport && prefix && prefix.length > 0) {
            return { type: "star" };
        }
        return null;
    }

    const receiver = dotMatch[1];
    const symbolState = analyzeTurtleSymbols(session.getValue());

    if (symbolState.moduleAliases.has(receiver)) {
        return { type: "module" };
    }

    if (symbolState.instanceNames.has(receiver)) {
        return { type: "instance" };
    }

    return null;
}

function analyzeTurtleSymbols(sourceCode) {
    const moduleAliases = new Set(["turtle"]);
    const classAliases = new Set();
    const instanceNames = new Set();
    let hasStarImport = false;

    const importRegex = /^\s*import\s+([^\n#]+)$/gm;
    let importMatch;
    while ((importMatch = importRegex.exec(sourceCode)) !== null) {
        const importParts = importMatch[1].split(",");
        importParts.forEach((part) => {
            const normalized = part.trim();
            const aliasMatch = normalized.match(
                /^turtle(?:\s+as\s+([A-Za-z_][\w]*))?$/,
            );
            if (aliasMatch) {
                moduleAliases.add(aliasMatch[1] || "turtle");
            }
        });
    }

    const fromImportRegex = /^\s*from\s+turtle\s+import\s+([^\n#]+)$/gm;
    let fromImportMatch;
    while ((fromImportMatch = fromImportRegex.exec(sourceCode)) !== null) {
        const importItems = fromImportMatch[1].split(",");
        importItems.forEach((item) => {
            const normalized = item.trim();
            if (normalized === "*") {
                hasStarImport = true;
                classAliases.add("Turtle");
                return;
            }
            const turtleClassMatch = normalized.match(
                /^Turtle(?:\s+as\s+([A-Za-z_][\w]*))?$/,
            );
            if (turtleClassMatch) {
                classAliases.add(turtleClassMatch[1] || "Turtle");
            }
        });
    }

    moduleAliases.forEach((alias) => {
        const assignRegex = new RegExp(
            "(^|\\n)\\s*([A-Za-z_][\\w]*)\\s*=\\s*" +
                escapeRegExp(alias) +
                "\\.Turtle\\s*\\(",
            "g",
        );
        let assignMatch;
        while ((assignMatch = assignRegex.exec(sourceCode)) !== null) {
            instanceNames.add(assignMatch[2]);
        }
    });

    classAliases.forEach((classAlias) => {
        const assignRegex = new RegExp(
            "(^|\\n)\\s*([A-Za-z_][\\w]*)\\s*=\\s*" +
                escapeRegExp(classAlias) +
                "\\s*\\(",
            "g",
        );
        let assignMatch;
        while ((assignMatch = assignRegex.exec(sourceCode)) !== null) {
            instanceNames.add(assignMatch[2]);
        }
    });

    return { moduleAliases, instanceNames, hasStarImport };
}

function escapeRegExp(value) {
    return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function toCompletionCaption(snippet) {
    return snippet
        .replace(/\$\{\d+:([^}]+)\}/g, "$1")
        .replace(/\$\{\d+\}/g, "");
}

// Tab navigation (single tab)
function initializeTabs() {
    setTimeout(() => {
        pythonEditor.resize();
    }, 100);
}

// Output tabs
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
            document.getElementById(tabName + "Output").classList.add("active");

            if (tabName === "result") {
                clearOutputBtn.classList.add("show");
            } else {
                clearOutputBtn.classList.remove("show");
            }
        });
    });

    clearOutputBtn.classList.add("show");
}

// Resizable splitter
function initializeResizer() {
    const splitter = document.getElementById("splitter");
    const editorPanel = document.querySelector(".editor-panel");
    const outputPanel = document.querySelector(".output-panel");
    const container = document.querySelector(".main-content");

    splitter.addEventListener("mousedown", startResize);
    splitter.addEventListener("touchstart", startResize);

    function startResize(e) {
        e.preventDefault();
        isResizing = true;

        if (e.type === "mousedown") {
            document.addEventListener("mousemove", handleMouseMove);
            document.addEventListener("mouseup", stopResize);
        } else if (e.type === "touchstart") {
            document.addEventListener("touchmove", handleTouchMove);
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

            let newHeight = Math.max(minHeight, Math.min(maxHeight, mouseY));
            let topPercent = (newHeight / containerHeight) * 100;
            let bottomPercent = 100 - topPercent;

            editorPanel.style.height = topPercent + "%";
            outputPanel.style.height = bottomPercent + "%";
        } else {
            const mouseX = clientX - containerRect.left;
            const containerWidth = containerRect.width;
            const minWidth = 300;
            const maxWidth = containerWidth - 300;

            let newWidth = Math.max(minWidth, Math.min(maxWidth, mouseX));
            let leftPercent = (newWidth / containerWidth) * 100;
            let rightPercent = 100 - leftPercent;

            editorPanel.style.width = leftPercent + "%";
            outputPanel.style.width = rightPercent + "%";
        }

        setTimeout(() => {
            pythonEditor.resize();
        }, 10);
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

    window.addEventListener("resize", function () {
        setTimeout(() => {
            pythonEditor.resize();
        }, 100);
    });
}

// Controls
function initializeControls() {
    const fontSizeSelect = document.getElementById("fontSizeSelect");
    fontSizeSelect.addEventListener("change", function () {
        const fontSize = parseInt(this.value, 10);

        pythonEditor.setFontSize(fontSize);

        const consoleOutput = document.getElementById("consoleOutput");
        if (consoleOutput) {
            consoleOutput.style.fontSize = fontSize + "px";
        }

        localStorage.setItem("editorFontSize", fontSize);
    });

    const savedFontSize = localStorage.getItem("editorFontSize");
    if (savedFontSize) {
        fontSizeSelect.value = savedFontSize;
        const fontSize = parseInt(savedFontSize, 10);
        pythonEditor.setFontSize(fontSize);

        const consoleOutput = document.getElementById("consoleOutput");
        if (consoleOutput) {
            consoleOutput.style.fontSize = fontSize + "px";
        }
    }

    const clearOutputBtn = document.getElementById("clearOutputBtn");
    clearOutputBtn.addEventListener("click", function () {
        clearConsoleOutput();
    });

    document.getElementById("runBtn").addEventListener("click", function () {
        if (!pyodideReady || isRunning) {
            return;
        }

        if (this.dataset.state === "reset") {
            resetTurtleOutput();
        } else {
            runPythonCode();
        }
    });

    document
        .getElementById("saveButton")
        .addEventListener("click", function () {
            saveContent();
        });

    document
        .getElementById("submitButton")
        .addEventListener("click", function () {
            submitTask();
        });

    const resetEditorButton = document.getElementById("resetEditorButton");
    if (resetEditorButton) {
        resetEditorButton.addEventListener("click", function () {
            const confirmed = window.confirm(
                "Möchtest du den Editor wirklich auf den Ausgangscode der Aufgabe zurücksetzen? Nicht gespeicherte Änderungen gehen verloren.",
            );
            if (!confirmed) {
                return;
            }
            resetEditorToDefaultSubmission();
        });
    }
}

// Utility
function getCurrentEditor() {
    return pythonEditor;
}

const MAX_CONSOLE_SIZE = 50000;

function addToConsole(text, type = "info") {
    const consoleOutput = document.getElementById("consoleOutput");
    const timestamp = new Date().toLocaleTimeString();
    const prefix = type === "error" ? "X" : type === "warning" ? "!" : "";

    const escapedText = text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");

    const newContent =
        "[" +
        timestamp +
        "] " +
        (prefix ? prefix + " " : "") +
        escapedText +
        "<br>";

    consoleOutput.innerHTML += newContent;

    if (consoleOutput.innerHTML.length > MAX_CONSOLE_SIZE) {
        const excessChars =
            consoleOutput.innerHTML.length - MAX_CONSOLE_SIZE + 2000;
        consoleOutput.innerHTML =
            "...[earlier output removed]...<br>" +
            consoleOutput.innerHTML.substring(excessChars);
    }

    consoleOutput.scrollTop = consoleOutput.scrollHeight;
}

function clearConsoleOutput() {
    const consoleOutput = document.getElementById("consoleOutput");
    consoleOutput.innerHTML = "";
}

function resetTurtleOutput() {
    clearConsoleOutput();
    const turtleOutput = document.getElementById("turtleOutput");
    if (turtleOutput) {
        turtleOutput.innerHTML = "";
    }
    if (pyodideReady) {
        renderInitialScene();
    }
    updateRunButton("execute");
}

function getBaseUrl() {
    const baseUrl =
        document.getElementById("default-link")?.getAttribute("href") || "/";
    return baseUrl.replace(/\/$/, "");
}

function buildUrl(rawPath) {
    if (!rawPath) return "";
    const base = getBaseUrl();
    return rawPath.startsWith("/") ? base + rawPath : base + "/" + rawPath;
}

function configureSubmitButton() {
    const submitButton = document.getElementById("submitButton");
    const rawSubmitUrl =
        document.getElementById("task-submit-url")?.dataset?.url || "";

    if (!rawSubmitUrl) {
        submitButton.disabled = true;
        submitButton.style.opacity = "0.5";
        submitButton.title = "Abgabe im Lehrermodus deaktiviert";
    }
}

// Pyodide + Turtle
async function initializePyodide() {
    addToConsole("Pyodide wird geladen...", "info");

    try {
        pyodide = await loadPyodide({
            stdout: (msg) => addToConsole(msg, "info"),
            stderr: (msg) => addToConsole(msg, "error"),
        });

        pyodideReady = true;
        addToConsole("Pyodide erfolgreich geladen", "info");

        registerBasthonModule();
        await loadTurtlePackage();

        document.getElementById("runBtn").disabled = false;
        document.getElementById("runBtn").style.opacity = "1";

        await renderInitialScene();

        await initializeMyPy();
    } catch (error) {
        addToConsole(
            "Fehler beim Laden von Pyodide: " + error.message,
            "error",
        );
        console.error("Pyodide error:", error);
    }
}

function registerBasthonModule() {
    const turtleOutput = document.getElementById("turtleOutput");

    const fakeBasthonPackage = {
        kernel: {
            display_event: (e) => {
                if (!turtleOutput) return;
                const content = e.toJs().get("content");
                const node = elementFromProps(content);
                if (!node) {
                    turtleOutput.innerHTML = "";
                } else if (node.nodeType === Node.TEXT_NODE) {
                    turtleOutput.textContent = node.textContent || "";
                } else {
                    turtleOutput.innerHTML = node.outerHTML;
                }
            },
            locals: () => pyodide.runPython("globals()"),
        },
    };

    pyodide.registerJsModule("basthon", fakeBasthonPackage);
}

async function loadTurtlePackage() {
    const absoluteFromLocation = new URL(
        "turtle-0.0.1-py3-none-any.whl",
        window.location.href,
    ).toString();
    const originRoot =
        window.location.origin + "/turtle-0.0.1-py3-none-any.whl";

    const candidateUrls = [
        buildUrl("turtle-0.0.1-py3-none-any.whl"),
        absoluteFromLocation,
        originRoot,
        "./turtle-0.0.1-py3-none-any.whl",
        "/turtle-0.0.1-py3-none-any.whl",
        "/turtle/turtle-0.0.1-py3-none-any.whl",
    ];

    const wheelBytes = await fetchWheelBytes(candidateUrls);
    if (!wheelBytes) {
        throw new Error(
            "Turtle-Wheel nicht gefunden. Stelle sicher, dass turtle-0.0.1-py3-none-any.whl statisch auslieferbar ist.",
        );
    }

    await pyodide.unpackArchive(
        wheelBytes,
        "zip",
        "/lib/python3.11/site-packages",
    );
    await pyodide.runPythonAsync(
        "import importlib; importlib.invalidate_caches()",
    );

    addToConsole("Turtle-Paket geladen", "info");
}

async function fetchWheelBytes(urls) {
    for (const url of urls) {
        try {
            const response = await fetch(url, { cache: "no-store" });
            if (!response.ok) {
                continue;
            }
            const buffer = await response.arrayBuffer();
            if (!buffer || buffer.byteLength < 4) {
                continue;
            }
            const signature = new Uint8Array(buffer.slice(0, 4));
            const isZip = signature[0] === 0x50 && signature[1] === 0x4b;
            if (!isZip) {
                try {
                    const text = new TextDecoder().decode(
                        new Uint8Array(buffer.slice(0, 200)),
                    );
                    console.warn(
                        "Wheel fetch returned non-zip content for",
                        url,
                        text,
                    );
                } catch (error) {
                    console.warn(
                        "Wheel fetch returned non-zip content for",
                        url,
                    );
                }
                continue;
            }
            return buffer;
        } catch (error) {
            console.warn("Wheel fetch failed:", url, error);
        }
    }
    return null;
}

function elementFromProps(map) {
    const tag = map.get("tag");
    if (!tag) {
        return document.createTextNode(map.get("text"));
    }

    const node = document.createElement(map.get("tag"));

    for (const [key, value] of map.get("props")) {
        node.setAttribute(key, value);
    }
    for (const childProps of map.get("children")) {
        node.appendChild(elementFromProps(childProps));
    }

    return node;
}

async function showScene() {
    await pyodide.runPythonAsync(`
import turtle
import basthon

svg_dict = turtle.Screen().show_scene()
basthon.kernel.display_event({ "display_type": "turtle", "content": svg_dict })
turtle.restart()
`);
}

async function renderInitialScene() {
    await pyodide.runPythonAsync(`
import turtle
import basthon

turtle.Turtle()
svg_dict = turtle.Screen().show_scene()
basthon.kernel.display_event({ "display_type": "turtle", "content": svg_dict })
turtle.restart()
    `);
}

// Run Python
async function runPythonCode() {
    if (!pyodideReady) {
        addToConsole("Python wird noch geladen...", "warning");
        return;
    }

    const code = pythonEditor.getValue();
    if (!code.trim()) {
        addToConsole("Kein Code zum Ausführen.", "warning");
        return;
    }

    try {
        isRunning = true;
        clearConsoleOutput();

        updateRunButton("running");

        await pyodide.runPythonAsync(code);
        await showScene();

        addToConsole("Programm erfolgreich ausgeführt.", "info");
        updateRunButton("reset");
    } catch (error) {
        addToConsole("Fehler beim Ausführen:", "error");
        addToConsole(error.message || String(error), "error");
        console.error("Run error:", error);
        updateRunButton("reset");
    } finally {
        isRunning = false;
    }
}

function updateRunButton(mode) {
    const runBtn = document.getElementById("runBtn");
    if (!runBtn) return;

    if (mode === "reset") {
        runBtn.innerHTML = '<i class="fas fa-undo"></i>  Zurücksetzen';
        runBtn.className = "btn btn-secondary";
        runBtn.dataset.state = "reset";
        runBtn.disabled = false;
    } else if (mode === "running") {
        runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
        runBtn.className = "btn btn-primary";
        runBtn.dataset.state = "running";
        runBtn.disabled = true;
    } else {
        runBtn.innerHTML = '<i class="fas fa-play"></i> Ausführen';
        runBtn.className = "btn btn-primary";
        runBtn.dataset.state = "execute";
        runBtn.disabled = false;
    }
}

// MyPy initialization
async function initializeMyPy() {
    try {
        addToConsole("MyPy wird installiert...", "info");

        await pyodide.loadPackage(["micropip"]);
        addToConsole("micropip geladen", "info");

        await pyodide.runPythonAsync(`
import micropip
await micropip.install(['typing-extensions', 'mypy-extensions'])
        `);
        addToConsole(
            "typing-extensions und mypy-extensions installiert",
            "info",
        );

        await pyodide.loadPackage(["mypy"]);
        addToConsole("MyPy-Paket geladen", "info");

        const turtleStubText = buildTurtleStubText();
        const turtleStubJson = JSON.stringify(turtleStubText);

        pyodide.runPython(`
import sys
import os
import tempfile
from typing import Any

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

TURTLE_STUB_TEXT = ${turtleStubJson}

class RobustTypeChecker:
    def __init__(self):
        self.temp_dir = tempfile.mkdtemp()
        self.cache = FileSystemCache()
        self._write_turtle_stub()

    def _write_turtle_stub(self):
        try:
            stub_path = os.path.join(self.temp_dir, 'turtle.pyi')
            with open(stub_path, 'w', encoding='utf-8') as f:
                f.write(TURTLE_STUB_TEXT)
        except Exception as e:
            print(f"Warnung: konnte turtle stub nicht schreiben: {e}")

    def check_code(self, code):
        errors = []
        temp_file = None

        try:
            temp_file = os.path.join(self.temp_dir, 'check_code.py')
            with open(temp_file, 'w', encoding='utf-8') as f:
                f.write(code)

            try:
                result = mypy.api.run([
                    temp_file,
                    '--no-error-summary',
                    '--no-color-output',
                    '--ignore-missing-imports',
                    '--follow-imports=silent',
                    f'--mypy-path={self.temp_dir}'
                ])

                stdout, stderr, exit_code = result

                if stdout and stdout.strip():
                    for line in stdout.strip().split('\\n'):
                        if line and temp_file in line and ':' in line:
                            parts = line.split(':', 4)
                            if len(parts) >= 4:
                                try:
                                    line_num = int(parts[1])
                                    error_level = parts[3].strip() if len(parts) > 3 else 'error'
                                    message = parts[4].strip() if len(parts) > 4 else line

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
            if temp_file and os.path.exists(temp_file):
                try:
                    os.remove(temp_file)
                except:
                    pass

        return errors

try:
    type_checker = RobustTypeChecker()
    print("RobustTypeChecker erfolgreich erstellt")
except Exception as e:
    print(f"Fehler beim Erstellen des TypeCheckers: {e}")
    raise
        `);

        mypyReady = true;
        addToConsole("MyPy erfolgreich konfiguriert", "info");
        startTypeChecking();
    } catch (error) {
        addToConsole(
            "Fehler beim Initialisieren von MyPy: " + error.message,
            "error",
        );
        console.error("MyPy init error:", error);
        mypyReady = false;

        addToConsole("Fallback: einfacher Syntax-Checker", "warning");
        initializeFallbackChecker();
    }
}

function initializeFallbackChecker() {
    pyodide.runPython(`
import ast

class FallbackTypeChecker:
    def check_code(self, code):
        errors = []
        try:
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
    addToConsole("Fallback Type-Checker aktiviert", "info");
    startTypeChecking();
}

function startTypeChecking() {
    if (typeCheckInterval) {
        clearInterval(typeCheckInterval);
    }

    typeCheckInterval = setInterval(() => {
        if (mypyReady) {
            checkPythonTypes();
        }
    }, 800);
}

async function checkPythonTypes() {
    const currentCode = pythonEditor.getValue();

    if (currentCode === lastPythonCode || !currentCode.trim()) {
        return;
    }

    lastPythonCode = currentCode;

    try {
        clearTypeErrors();

        const codeForTypeChecking = currentCode;
        const escapedCode = codeForTypeChecking.replace(/'/g, "\\'");

        const errors = pyodide
            .runPython(
                `
errors = type_checker.check_code(r'''${escapedCode}''')
errors
        `,
            )
            .toJs();

        if (errors && errors.length > 0) {
            markTypeErrors(errors);
        }
    } catch (error) {
        console.error("Type-Checking error:", error);
    }
}

function markTypeErrors(errors) {
    const session = pythonEditor.getSession();

    errors.forEach(([lineNum, errorType, message]) => {
        const line = lineNum - 1;

        session.setAnnotations(
            session.getAnnotations().concat([
                {
                    row: line,
                    column: 0,
                    text: message,
                    type: errorType === "error" ? "error" : "warning",
                },
            ]),
        );

        const Range = ace.require("ace/range").Range;
        const range = new Range(
            line,
            0,
            line,
            pythonEditor.getSession().getLine(line).length,
        );

        session.addMarker(
            range,
            errorType === "error" ? "type-error" : "type-warning",
            "text",
        );
    });
}

function clearTypeErrors() {
    const session = pythonEditor.getSession();

    session.clearAnnotations();

    const markers = session.getMarkers();
    if (markers) {
        Object.keys(markers).forEach((markerId) => {
            const marker = markers[markerId];
            if (
                marker.clazz === "type-error" ||
                marker.clazz === "type-warning"
            ) {
                session.removeMarker(markerId);
            }
        });
    }
}

// Load saved content
function loadSavedContent() {
    const contentElement = document.getElementById("currentContent");
    if (contentElement) {
        const savedContent = contentElement.textContent.trim();
        loadContentToView(savedContent);
    }
}

// Markdown rendering
function renderMarkdown(markdownText) {
    if (typeof marked !== "undefined") {
        return marked.parse(markdownText);
    }

    return markdownText
        .replace(/^# (.*$)/gm, "<h1>$1</h1>")
        .replace(/^## (.*$)/gm, "<h2>$1</h2>")
        .replace(/^### (.*$)/gm, "<h3>$1</h3>")
        .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
        .replace(/\*(.*?)\*/g, "<em>$1</em>")
        .replace(/`(.*?)`/g, "<code>$1</code>")
        .replace(/^- (.*$)/gm, "<ul><li>$1</li></ul>")
        .replace(/^\d+\. (.*$)/gm, "<ol><li>$1</li></ol>")
        .replace(/\n/g, "<br>");
}

// Tutorial content
const tutorialText = document.getElementById("tutorial").textContent.trim();

let tutorialContents;
if (tutorialText) {
    tutorialContents = new Function("return (" + tutorialText + ");")();
}

// Task content init
function initializeTaskContent() {
    updateTaskTab(document.getElementById("description").textContent);
}

// Tutorial navigation
function initializeTutorialNavigation() {
    const tutorialOutput = document.getElementById("tutorialOutput");
    const tutorialTab = document.querySelector(
        '.output-tab[data-output-tab="tutorial"]',
    );

    if (!tutorialContents || tutorialContents.length === 0) {
        if (tutorialTab) {
            tutorialTab.style.display = "none";
        }
        return;
    }

    const navigationHTML = `
        <div class="tutorial-navigation">
            <button id="tutorialPrev" class="nav-arrow">&larr;</button>
            <div class="tutorial-dots">
                ${tutorialContents
                    .map(
                        (_, index) =>
                            `<span class="tutorial-dot ${index === 0 ? "active" : ""}" data-index="${index}"></span>`,
                    )
                    .join("")}
            </div>
            <button id="tutorialNext" class="nav-arrow">&rarr;</button>
        </div>
        <div class="tutorial-content">
            <iframe id="tutorialFrame" src="" class="tutorial-iframe"></iframe>
        </div>
    `;

    tutorialOutput.innerHTML = navigationHTML;

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

    document.querySelectorAll(".tutorial-dot").forEach((dot) => {
        dot.addEventListener("click", (e) => {
            currentTutorialIndex = parseInt(e.target.dataset.index, 10);
            updateTutorialDisplay();
        });
    });

    updateTutorialDisplay();
}

function updateTutorialDisplay() {
    document.querySelectorAll(".tutorial-dot").forEach((dot, index) => {
        dot.classList.toggle("active", index === currentTutorialIndex);
    });

    document.getElementById("tutorialPrev").disabled =
        currentTutorialIndex === 0;
    document.getElementById("tutorialNext").disabled =
        currentTutorialIndex === tutorialContents.length - 1;

    const tutorialFrame = document.getElementById("tutorialFrame");
    const currentContent = tutorialContents[currentTutorialIndex].content;

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

    const blob = new Blob([htmlContent], { type: "text/html" });
    const url = URL.createObjectURL(blob);
    tutorialFrame.src = url;

    tutorialFrame.onload = () => {
        URL.revokeObjectURL(url);
    };
}

function updateTaskTab(markdownText) {
    const taskOutput = document.getElementById("taskOutput");
    const taskTab = document.querySelector(
        '.output-tab[data-output-tab="task"]',
    );

    if (taskOutput) {
        if (markdownText && markdownText.trim() !== "") {
            taskOutput.innerHTML = renderMarkdown(markdownText);
            taskTab.style.display = "block";
        } else {
            taskOutput.innerHTML = "";
            taskTab.style.display = "none";
        }
    }
}

// TaskView content
function getContentFromView() {
    const content = {
        version: "1.0",
        type: "python-code-editor",
        pythonCode: pythonEditor.getValue(),
        currentTutorialIndex: currentTutorialIndex,
        metadata: {
            lastModified: new Date().toISOString(),
            codeLength: pythonEditor.getValue().length,
        },
    };

    return JSON.stringify(content);
}

function loadContentToView(content) {
    try {
        if (!content || content.trim() === "" || content === "{}") {
            const defaultSubmission =
                document
                    .getElementById("defaultSubmission")
                    ?.textContent?.trim() || "";
            if (defaultSubmission) {
                applyDefaultSubmission(defaultSubmission);
            }
            return;
        }

        const data = JSON.parse(content);

        if (data.pythonCode) {
            pythonEditor.setValue(data.pythonCode);
        }

        if (
            data.currentTutorialIndex !== undefined &&
            tutorialContents &&
            data.currentTutorialIndex >= 0 &&
            data.currentTutorialIndex < tutorialContents.length
        ) {
            currentTutorialIndex = data.currentTutorialIndex;
            updateTutorialDisplay();
        }

        updateSaveStatus("saved");
    } catch (error) {
        console.error("Load content error:", error);
        updateSaveStatus("error");
    }
}

function applyDefaultSubmission(defaultSubmission) {
    try {
        if (defaultSubmission.startsWith("{")) {
            const data = JSON.parse(defaultSubmission);
            if (data.pythonCode) {
                pythonEditor.setValue(data.pythonCode);
                return;
            }
            if (data.defaultContent) {
                pythonEditor.setValue(data.defaultContent);
                return;
            }
        }
    } catch (error) {
        console.warn("Default submission parse error:", error);
    }

    pythonEditor.setValue(defaultSubmission);
}

function resetEditorToDefaultSubmission() {
    const defaultSubmission =
        document.getElementById("defaultSubmission")?.textContent?.trim() || "";

    if (defaultSubmission) {
        applyDefaultSubmission(defaultSubmission);
    } else {
        pythonEditor.setValue("");
    }

    pythonEditor.gotoLine(1);
    updateSaveStatus("ready");
}

function saveContent(isSubmission = false) {
    updateSaveStatus("saving");

    const content = getContentFromView();
    const rawSaveUrl =
        document.getElementById("task-save-url")?.dataset?.url || "";
    const rawSubmitUrl =
        document.getElementById("task-submit-url")?.dataset?.url || "";
    const url = isSubmission ? buildUrl(rawSubmitUrl) : buildUrl(rawSaveUrl);

    if (!url) {
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
            }
        })
        .catch((error) => {
            console.error("Save error:", error);
            updateSaveStatus("error");
        });
}

function submitTask() {
    const rawSubmitUrl =
        document.getElementById("task-submit-url")?.dataset?.url || "";
    if (!rawSubmitUrl) return;

    if (confirm("Möchten Sie diese Aufgabe wirklich abgeben?")) {
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

// Public API
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
};
