/******/ (() => { // webpackBootstrap
/******/ 	var __webpack_modules__ = ({

/***/ 27:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   config: () => (/* binding */ config)
/* harmony export */ });
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
class Config {
  constructor() {
    this.data = {
      InsertNode: {
        color: "rgb(255,255,243)"
      },
      Placeholder: {
        color: "rgb(255,255,243)"
      },
      InsertCase: {
        color: "rgb(250, 218, 209)"
      },
      InputNode: {
        use: true,
        id: "InputButton",
        text: "Eingabe-Feld",
        icon: "taskIcon",
        color: "rgb(253, 237, 206)"
      },
      OutputNode: {
        use: true,
        id: "OutputButton",
        text: "Ausgabe-Feld",
        icon: "taskIcon",
        color: "rgb(253, 237, 206)"
      },
      TaskNode: {
        use: true,
        id: "TaskButton",
        text: "Anweisung",
        icon: "taskIcon",
        color: "rgb(253, 237, 206)"
      },
      CountLoopNode: {
        use: true,
        id: "CountLoopButton",
        text: "Zählergesteuerte Schleife",
        icon: "countLoopIcon",
        color: "rgb(220, 239, 231)"
      },
      HeadLoopNode: {
        use: true,
        id: "HeadLoopButton",
        text: "Kopfgesteuerte Schleife",
        icon: "countLoopIcon",
        color: "rgb(220, 239, 231)"
      },
      FootLoopNode: {
        use: false,
        id: "FootLoopButton",
        text: "Fußgesteuerte Schleife",
        icon: "footLoopIcon",
        color: "rgb(220, 239, 231)"
      },
      BranchNode: {
        use: true,
        id: "BranchButton",
        text: "Verzweigung",
        icon: "branchIcon",
        color: "rgb(250, 218, 209)"
      },
      CaseNode: {
        use: false,
        id: "CaseButton",
        text: "Fallunterscheidung",
        icon: "caseIcon",
        color: "rgb(250, 218, 209)"
      },
      FunctionNode: {
        use: true,
        id: "FunctionButton",
        text: "Funktionsblock",
        icon: "funcIcon",
        color: "rgb(255, 255, 255)"
      },
      TryCatchNode: {
        use: true,
        id: "TryCatchButton",
        text: "Try-Catch-Block",
        icon: "tryCatchIcon",
        color: "rgb(250, 218, 209)"
      },
      showCodeButton: true
    };
    this.alternatives = {
      python: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: true,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: true,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: true,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: true,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: true,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_simple: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: false,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: false,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: false,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: false,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_if: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: false,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: false,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: true,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: false,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_loop: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: true,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: true,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: false,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: false,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_for: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: true,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: false,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: false,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: false,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_while: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: false,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: true,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: false,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: false,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_if_loop: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: true,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: true,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: true,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: false,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      python_function: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: true,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: true,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: false,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: true,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: false,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: true,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: false,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      },
      standard: {
        InsertNode: {
          color: "rgb(255,255,243)"
        },
        Placeholder: {
          color: "rgb(255,255,243)"
        },
        InsertCase: {
          color: "rgb(250, 218, 209)"
        },
        InputNode: {
          use: true,
          id: "InputButton",
          text: "Eingabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        OutputNode: {
          use: true,
          id: "OutputButton",
          text: "Ausgabe-Feld",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        TaskNode: {
          use: true,
          id: "TaskButton",
          text: "Anweisung",
          icon: "taskIcon",
          color: "rgb(253, 237, 206)"
        },
        CountLoopNode: {
          use: true,
          id: "CountLoopButton",
          text: "Zählergesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        HeadLoopNode: {
          use: true,
          id: "HeadLoopButton",
          text: "Kopfgesteuerte Schleife",
          icon: "countLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        FootLoopNode: {
          use: true,
          id: "FootLoopButton",
          text: "Fußgesteuerte Schleife",
          icon: "footLoopIcon",
          color: "rgb(220, 239, 231)"
        },
        BranchNode: {
          use: true,
          id: "BranchButton",
          text: "Verzweigung",
          icon: "branchIcon",
          color: "rgb(250, 218, 209)"
        },
        CaseNode: {
          use: true,
          id: "CaseButton",
          text: "Fallunterscheidung",
          icon: "caseIcon",
          color: "rgb(250, 218, 209)"
        },
        FunctionNode: {
          use: true,
          id: "FunctionButton",
          text: "Funktionsblock",
          icon: "funcIcon",
          color: "rgb(255, 255, 255)"
        },
        TryCatchNode: {
          use: true,
          id: "TryCatchButton",
          text: "Try-Catch-Block",
          icon: "tryCatchIcon",
          color: "rgb(250, 218, 209)"
        },
        showCodeButton: true
      }
    };
  }
  get() {
    return this.data;
  }
  loadConfig(id) {
    if (id in this.alternatives) {
      this.data = this.alternatives[id];
    }
  }
  getCurrentConfigName() {
    // Determine which configuration is currently active
    for (const [name, config] of Object.entries(this.alternatives)) {
      if (JSON.stringify(config) === JSON.stringify(this.data)) {
        return name;
      }
    }
    return "default";
  }
}
const config = new Config();

/***/ }),

/***/ 142:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "favicon-16x16.png");

/***/ }),

/***/ 222:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "site.webmanifest");

/***/ }),

/***/ 322:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "favicon-32x32.png");

/***/ }),

/***/ 419:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "apple-touch-icon.png");

/***/ }),

/***/ 425:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "safari-pinned-tab.svg");

/***/ }),

/***/ 435:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "browserconfig.xml");

/***/ }),

/***/ 504:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

var map = {
	"./android-chrome-192x192.png": 844,
	"./android-chrome-512x512.png": 980,
	"./apple-touch-icon.png": 419,
	"./browserconfig.xml": 435,
	"./favicon-16x16.png": 142,
	"./favicon-32x32.png": 322,
	"./favicon.ico": 601,
	"./mstile-150x150.png": 516,
	"./safari-pinned-tab.svg": 425,
	"./site.webmanifest": 222
};


function webpackContext(req) {
	var id = webpackContextResolve(req);
	return __webpack_require__(id);
}
function webpackContextResolve(req) {
	if(!__webpack_require__.o(map, req)) {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	}
	return map[req];
}
webpackContext.keys = function webpackContextKeys() {
	return Object.keys(map);
};
webpackContext.resolve = webpackContextResolve;
module.exports = webpackContext;
webpackContext.id = 504;

/***/ }),

/***/ 516:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "mstile-150x150.png");

/***/ }),

/***/ 601:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "favicon.ico");

/***/ }),

/***/ 632:
/***/ ((__unused_webpack_module, __unused_webpack_exports, __webpack_require__) => {

const faviconsContext = __webpack_require__(504);
faviconsContext.keys().forEach(faviconsContext);

/***/ }),

/***/ 844:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "android-chrome-192x192.png");

/***/ }),

/***/ 980:
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (__webpack_require__.p + "android-chrome-512x512.png");

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/define property getters */
/******/ 	(() => {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = (exports, definition) => {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/global */
/******/ 	(() => {
/******/ 		__webpack_require__.g = (function() {
/******/ 			if (typeof globalThis === 'object') return globalThis;
/******/ 			try {
/******/ 				return this || new Function('return this')();
/******/ 			} catch (e) {
/******/ 				if (typeof window === 'object') return window;
/******/ 			}
/******/ 		})();
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	(() => {
/******/ 		__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	(() => {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = (exports) => {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/publicPath */
/******/ 	(() => {
/******/ 		var scriptUrl;
/******/ 		if (__webpack_require__.g.importScripts) scriptUrl = __webpack_require__.g.location + "";
/******/ 		var document = __webpack_require__.g.document;
/******/ 		if (!scriptUrl && document) {
/******/ 			if (document.currentScript && document.currentScript.tagName.toUpperCase() === 'SCRIPT')
/******/ 				scriptUrl = document.currentScript.src;
/******/ 			if (!scriptUrl) {
/******/ 				var scripts = document.getElementsByTagName("script");
/******/ 				if(scripts.length) {
/******/ 					var i = scripts.length - 1;
/******/ 					while (i > -1 && (!scriptUrl || !/^http(s?):/.test(scriptUrl))) scriptUrl = scripts[i--].src;
/******/ 				}
/******/ 			}
/******/ 		}
/******/ 		// When supporting browsers where an automatic publicPath is not supported you must specify an output.publicPath manually via configuration
/******/ 		// or pass an empty string ("") and set the __webpack_public_path__ variable from your code to use your own logic.
/******/ 		if (!scriptUrl) throw new Error("Automatic publicPath is not supported in this browser");
/******/ 		scriptUrl = scriptUrl.replace(/^blob:/, "").replace(/#.*$/, "").replace(/\?.*$/, "").replace(/\/[^\/]+$/, "/");
/******/ 		__webpack_require__.p = scriptUrl;
/******/ 	})();
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry needs to be wrapped in an IIFE because it needs to be in strict mode.
(() => {
"use strict";

// EXTERNAL MODULE: ./src/assets/favicons/favicons.js
var favicons = __webpack_require__(632);
// EXTERNAL MODULE: ./src/config.js
var config = __webpack_require__(27);
;// ./src/helpers/generator.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Generate a random id string
 *
 * @ return   string   random generated
 */
function guidGenerator() {
  const gen = function () {
    return ((1 + Math.random()) * 0x10000 | 0).toString(16).substring(1);
  };
  return gen() + gen() + '-' + gen() + '-' + gen() + '-' + gen() + '-' + gen() + gen();
}

/**
 * Generate HTML tree
 *
 */
function generateHtmltree() {
  // Header
  const header = document.createElement('header');
  header.classList.add('container');
  document.body.appendChild(header);
  const section1 = document.createElement('section');
  section1.classList.add('nav-col');
  header.appendChild(section1);
  const logoDiv = document.createElement('div');
  logoDiv.classList.add('nav-logo-container');
  section1.appendChild(logoDiv);
  const logoAnker = document.createElement('a');
  logoAnker.classList.add('column', 'container');
  let url = 'index.html';
  const browserUrl = new URL(window.location.href);
  if (browserUrl.searchParams.get('config')) {
    url = url + '?config=' + browserUrl.searchParams.get('config');
  }
  logoAnker.setAttribute('href', url);
  logoDiv.appendChild(logoAnker);
  const logo = document.createElement('div');
  logo.classList.add('logo', 'logo-container');
  logoAnker.appendChild(logo);
  const logoText = document.createElement('strong');
  logoText.classList.add('nav-col');
  logoText.appendChild(document.createTextNode('Struktog.'));
  logoAnker.appendChild(logoText);
  const section2 = document.createElement('section');
  section2.classList.add('nav-col-opt');
  header.appendChild(section2);
  const divOptions = document.createElement('div');
  divOptions.classList.add('options-container');
  divOptions.setAttribute('id', 'optionButtons');
  section2.appendChild(divOptions);
  const divider = document.createElement('div');
  divider.classList.add('divider');
  document.body.appendChild(divider);

  // main
  const main = document.createElement('main');
  document.body.appendChild(main);
  const editor = document.createElement('div');
  editor.classList.add('container');
  editor.setAttribute('id', 'editorDisplay');
  main.appendChild(editor);
  const modal = document.createElement('div');
  modal.classList.add('modal');
  modal.setAttribute('id', 'IEModal');
  main.appendChild(modal);
  const modalOverlay = document.createElement('div');
  modalOverlay.classList.add('modal-overlay');
  modalOverlay.setAttribute('aria-label', 'Close');
  modalOverlay.addEventListener('click', () => {
    document.getElementById('IEModal').classList.remove('active');
  });
  modal.appendChild(modalOverlay);
  const modalContainer = document.createElement('div');
  modalContainer.classList.add('modal-container');
  modal.appendChild(modalContainer);
  const modalHeader = document.createElement('div');
  modalHeader.classList.add('modal-header');
  modalContainer.appendChild(modalHeader);
  const modalHeaderClose = document.createElement('div');
  modalHeaderClose.classList.add('close', 'hand', 'cancelIcon');
  modalHeaderClose.addEventListener('click', () => {
    document.getElementById('IEModal').classList.remove('active');
  });
  modalHeader.appendChild(modalHeaderClose);
  const modalBody = document.createElement('div');
  modalBody.classList.add('modal-body');
  modalContainer.appendChild(modalBody);
  const modalBodyContent = document.createElement('div');
  modalBodyContent.classList.add('content');
  modalBodyContent.setAttribute('id', 'modal-content');
  modalBody.appendChild(modalBodyContent);
  const modalFooter = document.createElement('div');
  modalFooter.classList.add('modal-footer', 'container');
  modalFooter.setAttribute('id', 'modal-footer');
  modalContainer.appendChild(modalFooter);
}

/**
 * Generate HTML tree for footer
**/
function generateFooter() {
  const footer = document.createElement('footer');
  footer.classList.add('container');
  document.body.appendChild(footer);
  const footerDiv = document.createElement('div');
  footerDiv.classList.add('column');
  footer.appendChild(footerDiv);
  const footerSpan = document.createElement('span');
  footerDiv.appendChild(footerSpan);
}
function generateResetButton(presenter, domNode) {
  // reset button must be last defined
  const resetButtonDiv = document.createElement('div');
  resetButtonDiv.classList.add('struktoOption', 'resetIcon', 'tooltip', 'tooltip-bottom', 'hand');
  resetButtonDiv.setAttribute('data-tooltip', 'Reset');
  resetButtonDiv.addEventListener('click', () => {
    const content = document.getElementById('modal-content');
    const footer = document.getElementById('modal-footer');
    while (content.hasChildNodes()) {
      content.removeChild(content.lastChild);
    }
    while (footer.hasChildNodes()) {
      footer.removeChild(footer.lastChild);
    }
    content.appendChild(document.createTextNode('Alles löschen?'));
    const doButton = document.createElement('div');
    doButton.classList.add('modal-buttons', 'acceptIcon', 'hand');
    doButton.addEventListener('click', () => presenter.resetModel());
    footer.appendChild(doButton);
    const cancelButton = document.createElement('div');
    cancelButton.classList.add('modal-buttons', 'deleteIcon', 'hand');
    cancelButton.addEventListener('click', () => document.getElementById('IEModal').classList.remove('active'));
    footer.appendChild(cancelButton);
    document.getElementById('IEModal').classList.add('active');
  });
  domNode.appendChild(resetButtonDiv);
}
function generateInfoButton(domNode) {
  const infoButtonDiv = document.createElement('div');
  infoButtonDiv.classList.add('options-element', 'infoIcon', 'tooltip', 'tooltip-bottomInfo', 'hand');
  infoButtonDiv.setAttribute('data-tooltip', 'Gitlab Repository');
  infoButtonDiv.addEventListener('click', () => {
    window.open('https://gitlab.com/dev-ddi/cs-school-tools/struktog', '_blank');
  });
  domNode.appendChild(infoButtonDiv);
}
;// ./src/model/main.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


class Model {
  constructor(tree = {
    id: guidGenerator(),
    type: 'InsertNode',
    followElement: {
      type: 'Placeholder'
    }
  }) {
    this.tree = tree;
    this.presenter = null;
  }
  setPresenter(presenter) {
    this.presenter = presenter;
  }
  getTree() {
    return this.tree;
  }
  setTree(content) {
    this.tree = content;
  }
  reset() {
    this.tree = {
      id: guidGenerator(),
      type: 'InsertNode',
      followElement: {
        type: 'Placeholder'
      }
    };
  }

  /**
   * Find an element by id in the tree and use a function on that element
   *
   * @param    subTree         part of the tree with all children of current element
   * @param    alterFunction   function to be executed on the found element
   * @param    hasRealParent   indicates, if the parent element was a container node
   * @param    text            the new text for the node
   * @return   subTree         altered subTree object
   */
  findAndAlterElement(uid, subTree, alterFunction, hasRealParent, text) {
    // end the recursion
    if (subTree === null || subTree.type === 'Placeholder') {
      return subTree;
    } else {
      if (subTree.id === uid) {
        // call the given function
        alterFunction = alterFunction.bind(this);
        subTree = alterFunction(subTree, hasRealParent, text);
        // reset the insert buttons, on drag and drop
        if (this.presenter.getMoveId() === null) {
          this.presenter.resetButtons();
        }
        return subTree;
      } else {
        switch (subTree.type) {
          case 'InsertNode':
            subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, hasRealParent, text);
            return subTree;
          case 'InputNode':
          case 'OutputNode':
          case 'TaskNode':
            subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, true, text);
            return subTree;
          case 'HeadLoopNode':
          case 'FootLoopNode':
          case 'CountLoopNode':
          case 'FunctionNode':
            subTree.child = this.findAndAlterElement(uid, subTree.child, alterFunction, false, text);
            subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, true, text);
            return subTree;
          case 'BranchNode':
            subTree.trueChild = this.findAndAlterElement(uid, subTree.trueChild, alterFunction, false, text);
            subTree.falseChild = this.findAndAlterElement(uid, subTree.falseChild, alterFunction, false, text);
            subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, true, text);
            return subTree;
          case 'TryCatchNode':
            subTree.tryChild = this.findAndAlterElement(uid, subTree.tryChild, alterFunction, false, text);
            subTree.catchChild = this.findAndAlterElement(uid, subTree.catchChild, alterFunction, false, text);
            subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, true, text);
            return subTree;
          case 'CaseNode':
            {
              const nodes = [];
              for (const element of subTree.cases) {
                const val = this.findAndAlterElement(uid, element, alterFunction, false, text);
                if (!(val === null)) {
                  nodes.push(val);
                }
              }
              if (nodes.length >= 2) {
                subTree.cases = nodes;
              }
              const valDefault = this.findAndAlterElement(uid, subTree.defaultNode, alterFunction, false, text);
              if (valDefault === null) {
                subTree.defaultOn = false;
              } else {
                subTree.defaultNode = valDefault;
              }
              subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, true, text);
              return subTree;
            }
          case 'InsertCase':
            subTree.followElement = this.findAndAlterElement(uid, subTree.followElement, alterFunction, hasRealParent, text);
            return subTree;
        }
      }
    }
  }

  /**
   * Remove the node and reconnect the follow element
   *
   * @param    subTree         part of the tree with all children of current element
   * @param    hasRealParent   indicates if an Placeholder node has to be added
   * @param    text            not used in this function
   * @return   subTree         altered subTree object (without removed element)
   */
  removeNode(subTree, hasRealParent, text) {
    // InsertCases are just completly removed, they do not have follow elements
    if (subTree.type === 'InsertCase') {
      return null;
    }
    // remove a node, but check if the parent is a container and a placeholder must be inserted
    if (subTree.followElement.followElement === null && !hasRealParent) {
      return {
        type: 'Placeholder'
      };
    }
    // alter followElement of the node to the follow element of the next node
    return subTree.followElement.followElement;
  }

  /**
   * Change the text of the current node
   *
   * @param    subTree         part of the tree with all children of current element
   * @param    hasRealParent   not used in this function
   * @param    text            the new text for the node
   * @return   subTree         altered subTree object (with changed text)
   */
  editElement(subTree, hasRealParent, text) {
    // if subtree is a function node, update also the function parameters
    if (subTree.type === 'FunctionNode') {
      const words = text.split('|');
      if (words[0] === 'funcname') {
        subTree.text = words[1];
      } else {
        // update function parameters (var names) in the tree model
        if (subTree.parameters.length !== 0) {
          let index = 0;
          for (const par of subTree.parameters) {
            if (words[0] === par.pos) {
              // update existing entry
              subTree.parameters[index].parName = words[1];
              return subTree;
            }
            index += 1;
          }
        }
        // parameter does not exist in model, create a new entry
        subTree.parameters.push({
          pos: words[0],
          parName: words[1]
        });
      }
    } else {
      subTree.text = text;
    }
    return subTree;
  }

  /**
   * Insert an element in the model tree and connect children
   *
   * @param    subTree         part of the tree with all children of current element
   * @param    hasRealParent   not used in this function
   * @param    text            not used in this function
   * @return   subTree         altered subTree object (with newly inserted element)
   */
  insertElement(subTree, hasRealParent, text) {
    const element = this.presenter.getNextInsertElement();
    // check for children
    if (!(subTree.followElement === null || subTree.followElement.type === 'Placeholder')) {
      // connect children with the element to insert
      element.followElement.followElement = subTree.followElement;
    }
    // insert the new element
    subTree.followElement = element;
    return subTree;
  }

  /**
   * Switch the display of the default case
   *
   * @param    subTree         part of the tree with all children of current element
   * @param    hasRealParent   not used in this function
   * @param    text            not used in this function
   * @return   subTree         altered subTree object (with changed state of default case)
   */
  switchDefaultCase(subTree, hasRealParent, text) {
    if (subTree.defaultOn) {
      subTree.defaultOn = false;
    } else {
      subTree.defaultOn = true;
    }
    return subTree;
  }

  /**
   * Insert a new empty case element
   *
   * @param    subTree         part of the tree with all children of current element
   * @param    hasRealParent   not used in this function
   * @param    text            not used in this function
   * @return   subTree         altered subTree object (with inserted case element)
   */
  insertNewCase(subTree, hasRealParent, text) {
    // check for max number of cases, duo to rendering issues
    if (subTree.cases.length < 7) {
      // add a new case
      subTree.cases.push({
        id: guidGenerator(),
        type: 'InsertCase',
        text: 'Fall',
        followElement: {
          id: guidGenerator(),
          type: 'InsertNode',
          followElement: {
            type: 'Placeholder'
          }
        }
      });
    }
    return subTree;
  }

  /**
   * Recursive function to get a real copy of an element by his id
   *
   * @param    id              id of the element, which to find
   * @param    subTree         part of the tree with all children of current element
   * @return   subTree         copy of the subTree object
   */
  getElementInTree(uid, subTree) {
    // stop recursion if the end of a sub tree is reached
    if (subTree === null || subTree.type === 'Placeholder') {
      return null;
    } else {
      if (subTree.id === uid) {
        // return a real copy
        return JSON.parse(JSON.stringify(subTree));
      } else {
        switch (subTree.type) {
          case 'InsertNode':
          case 'InputNode':
          case 'OutputNode':
          case 'TaskNode':
          case 'InsertCase':
            return this.getElementInTree(uid, subTree.followElement);
          case 'HeadLoopNode':
          case 'CountLoopNode':
          case 'FootLoopNode':
          case 'FunctionNode':
            {
              // follow children first, then the follow node
              const node = this.getElementInTree(uid, subTree.child);
              if (node === null) {
                return this.getElementInTree(uid, subTree.followElement);
              } else {
                return node;
              }
            }
          case 'BranchNode':
            {
              // follow both children first, then the follow node
              let node = this.getElementInTree(uid, subTree.trueChild);
              if (node === null) {
                node = this.getElementInTree(uid, subTree.falseChild);
                if (node === null) {
                  return this.getElementInTree(uid, subTree.followElement);
                } else {
                  return node;
                }
              } else {
                return node;
              }
            }
          case 'TryCatchNode':
            {
              // follow both children first, then the follow node
              let node = this.getElementInTree(uid, subTree.tryChild);
              if (node === null) {
                node = this.getElementInTree(uid, subTree.catchChild);
                if (node === null) {
                  return this.getElementInTree(uid, subTree.followElement);
                } else {
                  return node;
                }
              } else {
                return node;
              }
            }
          case 'CaseNode':
            {
              // follow every case first
              let node = null;
              for (const element of subTree.cases) {
                const caseNode = this.getElementInTree(uid, element);
                if (caseNode != null) {
                  node = caseNode;
                }
              }
              // then the default case
              if (node === null) {
                node = this.getElementInTree(uid, subTree.defaultNode);
                if (node === null) {
                  // then the follow element
                  return this.getElementInTree(uid, subTree.followElement);
                } else {
                  return node;
                }
              } else {
                return node;
              }
            }
        }
      }
    }
  }
}

// create a singleton of the model object
const model = new Model();
;// ./src/presenter/main.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


class Presenter {
  constructor(model, config) {
    this.model = model;
    this.config = config;
    this.insertMode = false;
    this.settingFunctionMode = false; // if the user is setting a function block then true
    this.views = [];
    this.moveId = null;
    this.nextInsertElement = null;
    this.displaySourcecode = false;
    this.undoList = [];
    this.redoList = [];
  }
  addView(view) {
    this.views.push(view);
  }
  getInsertMode() {
    return this.insertMode;
  }
  getSettingFunctionMode() {
    return this.settingFunctionMode;
  }
  getModelTree() {
    return this.model.getTree();
  }
  getElementByUid(uid) {
    return this.model.getElementInTree(uid, this.model.getTree());
  }
  resetButtons() {
    for (const view of this.views) {
      view.resetButtons();
    }
  }
  reset() {
    // reset the model fields connected to inserting
    this.insertMode = false;
    this.settingFunctionMode = false;
    this.nextInsertElement = null;
    this.moveId = null;
  }
  setSourcecodeDisplay(state) {
    this.displaySourcecode = state;
  }
  getSourcecodeDisplay() {
    return this.displaySourcecode;
  }

  /**
   * Update the model stored in the browser store
   */
  /*
  updateBrowserStore () {
    // check if browser supports web storage
    if (typeof Storage !== 'undefined') {
      // update the model as stringified JSON data
      localStorage.tree = JSON.stringify(this.model.getTree())
      localStorage.displaySourcecode = this.displaySourcecode
    }
  }
  */

  getMoveId() {
    return this.moveId;
  }
  getNextInsertElement() {
    return this.nextInsertElement;
  }
  renderAllViews() {
    for (const view of this.views) {
      view.render(this.model.getTree());
      // Update buttons if view supports it (for configuration changes)
      if (view.updateButtons) {
        view.updateButtons();
      }
      // Update code button visibility
      if (view.updateCodeButtonVisibility) {
        view.updateCodeButtonVisibility();
      }
    }
  }
  init() {
    this.renderAllViews();
  }

  /**
   * Start the tranformation of the model tree to sourcecode
   *
   * @param   lang   programming language to which the translation happens
   */
  startTransforming(event) {
    for (const view of this.views) {
      view.setLang(event.target.value);
    }
    this.renderAllViews();
  }

  /**
   * Get the current configuration
   *
   * @return   object   current configuration
   */
  getConfig() {
    return this.config.get();
  }

  /**
   * Toggle the rendering of sourcecode
   *
   * @param   buttonId   id of the sourcecode display button
   */
  alterSourcecodeDisplay(buttonId) {
    if (this.displaySourcecode) {
      this.displaySourcecode = false;
    } else {
      this.displaySourcecode = true;
    }
    //this.updateBrowserStore()
    for (const view of this.views) {
      view.displaySourcecode("ToggleSourcecode");
    }
  }

  /**
   * Prepare for inserting an element
   *
   * @param   buttonId   id of the selected button
   */
  insertNode(id, event) {
    switch (id) {
      case "InputButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "InputNode",
          text: "",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          }
        };
        break;
      case "OutputButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "OutputNode",
          text: "",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          }
        };
        break;
      case "TaskButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "TaskNode",
          text: "Anweisung",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          }
        };
        break;
      case "BranchButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "BranchNode",
          text: "Bedingung",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          trueChild: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          },
          falseChild: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          }
        };
        break;
      case "CaseButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "CaseNode",
          text: "Variable",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          defaultOn: true,
          defaultNode: {
            id: guidGenerator(),
            type: "InsertCase",
            text: "Sonst",
            followElement: {
              id: guidGenerator(),
              type: "InsertNode",
              followElement: {
                type: "Placeholder"
              }
            }
          },
          cases: [{
            id: guidGenerator(),
            type: "InsertCase",
            text: "Fall",
            followElement: {
              id: guidGenerator(),
              type: "InsertNode",
              followElement: {
                type: "Placeholder"
              }
            }
          }, {
            id: guidGenerator(),
            type: "InsertCase",
            text: "Fall",
            followElement: {
              id: guidGenerator(),
              type: "InsertNode",
              followElement: {
                type: "Placeholder"
              }
            }
          }]
        };
        break;
      case "CountLoopButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "CountLoopNode",
          text: "for i in range(n)",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          child: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          }
        };
        break;
      case "HeadLoopButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "HeadLoopNode",
          text: "Gültigkeitsbedingung",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          child: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          }
        };
        break;
      case "FunctionButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "FunctionNode",
          text: "",
          parameters: [],
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          child: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          }
        };
        this.settingFunctionMode = true;
        break;
      case "FootLoopButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "FootLoopNode",
          text: "Gültigkeitsbedingung",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          child: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          }
        };
        break;
      case "TryCatchButton":
        this.nextInsertElement = {
          id: guidGenerator(),
          type: "TryCatchNode",
          text: "",
          followElement: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: null
          },
          tryChild: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          },
          catchChild: {
            id: guidGenerator(),
            type: "InsertNode",
            followElement: {
              type: "Placeholder"
            }
          }
        };
        break;
    }
    if (event.dataTransfer !== undefined) {
      event.dataTransfer.effectAllowed = "move";
      event.dataTransfer.setData("text", id);
    }
    const button = document.getElementById(id);
    if (button.classList.contains("boldText")) {
      this.resetButtons();
      this.reset();
    } else {
      // prepare insert by updating the model data
      this.resetButtons();
      this.insertMode = true;
      button.classList.add("boldText");
    }
    // rerender the struktogramm
    this.renderAllViews();
  }

  /**
   * Helper function to correctly abort while using drag and drop
   */
  resetDrop() {
    // while drag and droping an inserting element, the user can drop everywhere
    // if the location is not valid, one step more must be done to abort everything
    if (this.insertMode) {
      this.reset();
      this.resetButtons();
      this.renderAllViews();
    } else {
      this.resetButtons();
    }
  }
  resetModel() {
    this.updateUndo();
    this.model.reset();
    this.checkUndo();
    //this.updateBrowserStore()
    this.renderAllViews();
    document.getElementById("IEModal").classList.remove("active");
  }

  /**
   * Switch the state of the default case
   *
   * @param   uid   id of the clicked element in the struktogramm
   */
  switchDefaultState(uid) {
    this.updateUndo();
    this.model.setTree(this.model.findAndAlterElement(uid, this.model.getTree(), this.model.switchDefaultCase, false, ""));
    this.checkUndo();
    //this.updateBrowserStore()
    this.renderAllViews();
  }

  /**
   * Add another new case
   *
   * @param   uid   id of the clicked element in the struktogramm
   */
  addCase(uid) {
    this.updateUndo();
    this.model.setTree(this.model.findAndAlterElement(uid, this.model.getTree(), this.model.insertNewCase, false, ""));
    this.checkUndo();
    //this.updateBrowserStore()
    this.renderAllViews();
  }

  /**
   * Remove the element from the tree
   *
   * @param   uid   id of the clicked element in the struktogramm
   */
  removeElement(uid) {
    const deleteElem = this.model.getElementInTree(uid, this.model.getTree());
    switch (deleteElem.type) {
      case "TaskNode":
      case "InputNode":
      case "OutputNode":
        this.removeNodeFromTree(uid);
        break;
      case "HeadLoopNode":
      case "CountLoopNode":
      case "FootLoopNode":
      case "FunctionNode":
        if (deleteElem.child.followElement.type !== "Placeholder") {
          this.prepareRemoveQuestion(uid);
        } else {
          this.removeNodeFromTree(uid);
        }
        break;
      case "BranchNode":
        if (deleteElem.trueChild.followElement.type !== "Placeholder" || deleteElem.falseChild.followElement.type !== "Placeholder") {
          this.prepareRemoveQuestion(uid);
        } else {
          this.removeNodeFromTree(uid);
        }
        break;
      case "TryCatchNode":
        if (deleteElem.tryChild.followElement.type !== "Placeholder" || deleteElem.catchChild.followElement.type !== "Placeholder") {
          this.prepareRemoveQuestion(uid);
        } else {
          this.removeNodeFromTree(uid);
        }
        break;
      case "CaseNode":
        {
          let check = false;
          for (const item of deleteElem.cases) {
            if (item.followElement.followElement.type !== "Placeholder") {
              check = true;
            }
          }
          if (deleteElem.defaultNode.followElement.followElement.type !== "Placeholder") {
            check = true;
          }
          if (check) {
            this.prepareRemoveQuestion(uid);
          } else {
            this.removeNodeFromTree(uid);
          }
          break;
        }
      case "InsertCase":
        if (deleteElem.followElement.followElement.type !== "Placeholder") {
          this.prepareRemoveQuestion(uid);
        } else {
          this.removeNodeFromTree(uid);
        }
        break;
    }
  }
  prepareRemoveQuestion(uid) {
    const content = document.getElementById("modal-content");
    const footer = document.getElementById("modal-footer");
    while (content.hasChildNodes()) {
      content.removeChild(content.lastChild);
    }
    while (footer.hasChildNodes()) {
      footer.removeChild(footer.lastChild);
    }
    content.appendChild(document.createTextNode("Dieses Element und alle darin erstellten Blöcke löschen?"));
    const doButton = document.createElement("div");
    doButton.classList.add("modal-buttons", "acceptIcon", "hand");
    doButton.addEventListener("click", () => this.removeNodeFromTree(uid, true));
    footer.appendChild(doButton);
    const cancelButton = document.createElement("div");
    cancelButton.classList.add("modal-buttons", "deleteIcon", "hand");
    cancelButton.addEventListener("click", () => document.getElementById("IEModal").classList.remove("active"));
    footer.appendChild(cancelButton);
    document.getElementById("IEModal").classList.add("active");
  }
  removeNodeFromTree(uid, closeModal = false) {
    this.updateUndo();
    this.model.setTree(this.model.findAndAlterElement(uid, this.model.getTree(), this.model.removeNode, false, ""));
    this.checkUndo();
    //this.updateBrowserStore()
    this.renderAllViews();
    if (closeModal) {
      document.getElementById("IEModal").classList.remove("active");
    }
  }

  /**
   * removes a parameter from the function parameters
   *
   * @param delPos   pos of the param in the dom list
   */
  removeParamFromParameters(delPos) {
    let editedTree = this.model.getTree();
    // search for the function box tree
    const followingElements = [];
    while (editedTree.type !== "FunctionNode") {
      followingElements.push(editedTree);
      editedTree = editedTree.followElement;
    }

    // find the respective parameter to remove it from the model
    const params = editedTree.parameters;
    for (const param of params) {
      const actPos = parseInt(param.pos);
      if (actPos === delPos) {
        let listIndex = actPos / 3; // convert the element position in the dom into the position in the array
        params.splice(listIndex, 1);

        // update all pos-values of the following param elements
        while (listIndex < params.length) {
          params[listIndex].pos -= 3;
          listIndex += 1;
        }
        editedTree.parameters = params;

        // set up the whole tree
        let index = followingElements.length - 1;
        while (index > -1) {
          const subTree = followingElements[index];
          subTree.followElement = editedTree;
          editedTree = subTree;
          index -= 1;
        }
        this.model.setTree(editedTree);
        //this.updateBrowserStore()
        this.renderAllViews();
        return;
      }
    }
  }

  /**
   * Prepare moving of an element of the struktogramm
   *
   * @param   uid   id of the clicked element in the struktogramm
   */
  moveElement(uid) {
    // prepare data
    this.moveId = uid;
    this.insertMode = true;
    this.nextInsertElement = this.model.getElementInTree(uid, this.model.getTree());
    this.nextInsertElement.followElement.followElement = null;
    // rerender
    this.renderAllViews();
  }

  // textType: only used for the distinction of function name and function parameters
  editElement(uid, textValue, textType = "") {
    this.updateUndo();
    this.model.setTree(this.model.findAndAlterElement(uid, this.model.getTree(), this.model.editElement, false, textType + textValue));
    this.checkUndo();
    //this.updateBrowserStore()
    this.renderAllViews();
  }

  /**
   * Append an element in the tree
   *
   * @param   uid   id of the clicked InsertNode in the struktogramm
   */
  appendElement(uid) {
    this.updateUndo();
    // remove old node, when moving is used
    const moveState = this.moveId;
    if (moveState) {
      this.model.setTree(this.model.findAndAlterElement(this.moveId, this.model.getTree(), this.model.removeNode, false, ""));
    }
    // insert the new node, on moving, its the removed
    const elemId = this.nextInsertElement.id;
    this.model.setTree(this.model.findAndAlterElement(uid, this.model.getTree(), this.model.insertElement, false, ""));
    // reset the buttons if moving occured
    if (moveState) {
      // TODO
      this.resetButtons();
    }
    // rerender
    this.reset();
    this.checkUndo();
    //this.updateBrowserStore()
    this.renderAllViews();
    // on new inserted elements start the editing mode of the element
    // start no editing mode for try catch blocks
    if (!moveState && this.getElementByUid(elemId).type !== "TryCatchNode") {
      this.switchEditState(elemId);
    }
  }

  /**
   * Switch an element in the struktogramm to the editing state
   *
   * @param   uid         id of the desired element in the struktogramm
   * @param   paramIndex  index (position) of the function parameter
   */
  switchEditState(uid, paramIndex = null) {
    let elem = document.getElementById(uid);

    // element is a function node
    if (elem.children[0].children[0].classList.contains("func-box-header")) {
      let funcTextNode = null;
      // click function name
      if (paramIndex === null) {
        funcTextNode = elem.children[0].children[0].children[1].children[0];
        // trigger click event to show input field
      } else {
        funcTextNode = elem.children[0].children[0].children[2].children[paramIndex].children[0];
      }
      if (funcTextNode) {
        funcTextNode.click();
      }
    } else {
      // get the input field and display it
      // work around for FootLoopNodes, duo to HTML structure, the last element has to be found and edited
      if (elem.getElementsByClassName("input-group editField " + uid).length) {
        if (elem.childNodes[0].classList.contains("tryCatchNode")) {
          elem = elem.getElementsByClassName("input-group editField " + uid)[1];
        } else {
          elem = elem.getElementsByClassName("input-group editField " + uid)[0];
        }
      } else {
        // in try catch block the input field of the catch block has not to be the first input field (if the try block has child nodes)
        if (elem.children[0].classList.contains("tryCatchNode")) {
          elem = elem.getElementsByClassName("tryCatchNode")[1].children[1].children[1];
        } else {
          elem = elem.getElementsByClassName("input-group editField")[0];
        }
      }
      elem.previousSibling.style.display = "none";
      elem.style.display = "inline-flex";
      // automatic set focus on the input
      elem.getElementsByTagName("input")[0].select();
    }
  }
  getStringifiedTree() {
    return JSON.stringify(this.model.getTree());
  }
  getStringifiedTreeWithConfig() {
    const data = {
      version: "1.4.0",
      config: this.getCurrentConfigName(),
      tree: this.model.getTree(),
      showCodeButton: this.config.get().showCodeButton
    };
    return JSON.stringify(data);
  }
  getCurrentConfigName() {
    // Get current config name by comparing with known configurations
    return this.config.getCurrentConfigName();
  }
  saveDialog() {
    // define the data url to start a download on click
    const dataUri = "data:application/json;charset=utf-8," + encodeURIComponent(this.getStringifiedTreeWithConfig());
    // create filename with current date in the name
    const exportFileDefaultName = "struktog_" + new Date(Date.now()).toJSON().substring(0, 10) + ".json";
    // generate the download button element and append it to the node
    const linkElement = document.createElement("a");
    linkElement.setAttribute("href", dataUri);
    linkElement.setAttribute("download", exportFileDefaultName);
    linkElement.click();
  }

  /**
   * Read input from a JSON file and replace the current model
   */
  readFile(event) {
    // create a FileReader instance
    const reader = new FileReader();
    // read file and parse JSON, then update model
    reader.onload = async event => {
      const loadedData = JSON.parse(event.target.result);
      this.updateUndo();

      // Check if it's the new format with configuration
      if (loadedData.version && loadedData.tree && loadedData.config) {
        // New format with configuration
        this.model.setTree(loadedData.tree);
        await this.applyConfiguration(loadedData.config);

        // Apply showCodeButton setting if present
        if (loadedData.showCodeButton !== undefined) {
          this.config.get().showCodeButton = loadedData.showCodeButton !== "false" && loadedData.showCodeButton !== false;
        }
      } else {
        // Old format - just the tree
        this.model.setTree(loadedData);
      }
      this.checkUndo();
      this.renderAllViews();
      console.log("Loaded with configuration:", loadedData.config || "none");
    };
    // start the reading process
    reader.readAsText(event.target.files[0]);
  }

  /**
   * Read input from a JSON file and replace the current model
   */
  async readUrl(file) {
    this.updateUndo();

    // Check if it's the new format with configuration
    if (file.version && file.tree && file.config) {
      // New format with configuration
      this.model.setTree(file.tree);
      await this.applyConfiguration(file.config);

      // Apply showCodeButton setting if present
      if (file.showCodeButton !== undefined) {
        this.config.get().showCodeButton = file.showCodeButton !== "false" && file.showCodeButton !== false;
      }
    } else {
      // Old format - just the tree
      this.model.setTree(file);
    }
    this.checkUndo();
    this.renderAllViews();
  }

  /**
   * Apply a configuration directly without reloading the page
   */
  async applyConfiguration(configName) {
    if (configName && configName !== "default") {
      // Import config module and apply configuration directly
      const {
        config
      } = await Promise.resolve(/* import() */).then(__webpack_require__.bind(__webpack_require__, 27));
      config.loadConfig(configName);
      console.log("Configuration applied:", configName);
      // Re-render views to apply new configuration
      this.renderAllViews();
    }
  }
  updateUndo() {
    this.undoList.push(this.getStringifiedTree());
    for (const item of document.getElementsByClassName("UndoIconButtonOverlay")) {
      item.classList.remove("disableIcon");
    }
    this.redoList = [];
    for (const item of document.getElementsByClassName("RedoIconButtonOverlay")) {
      item.classList.add("disableIcon");
    }
  }
  undo() {
    if (this.undoList.length) {
      this.redoList.unshift(this.getStringifiedTree());
      this.model.setTree(JSON.parse(this.undoList[this.undoList.length - 1]));
      this.undoList.pop();
      if (this.undoList === 0) {
        for (const item of document.getElementsByClassName("UndoIconButtonOverlay")) {
          item.classList.add("disableIcon");
        }
      }
      for (const item of document.getElementsByClassName("RedoIconButtonOverlay")) {
        item.classList.remove("disableIcon");
      }
      this.renderAllViews();
    }
  }
  checkUndo() {
    if (this.undoList[this.undoList.length - 1] === this.getStringifiedTree()) {
      this.undoList.pop();
      if (this.undoList === 0) {
        for (const item of document.getElementsByClassName("UndoIconButtonOverlay")) {
          item.classList.add("disableIcon");
        }
      }
    }
  }
  redo() {
    if (this.redoList.length) {
      this.undoList.push(this.getStringifiedTree());
      this.model.setTree(JSON.parse(this.redoList[0]));
      this.redoList.shift();
      if (this.redoList.length === 0) {
        for (const item of document.getElementsByClassName("RedoIconButtonOverlay")) {
          item.classList.add("disableIcon");
        }
      }
      for (const item of document.getElementsByClassName("UndoIconButtonOverlay")) {
        item.classList.remove("disableIcon");
      }
      this.renderAllViews();
    }
  }
}
;// ./src/helpers/domBuilding.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

function newElement(type, classes = [], parent = false) {
  const domElement = document.createElement(type);
  for (const item of classes) {
    domElement.classList.add(item);
  }
  if (parent) {
    parent.appendChild(domElement);
  }
  return domElement;
}
;// ./src/views/structogram.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */




class Structogram {
  constructor(presenter, domRoot) {
    this.presenter = presenter;
    this.domRoot = domRoot;
    this.size = 7;
    this.buttonList = ['InputNode', 'OutputNode', 'TaskNode', 'CountLoopNode', 'HeadLoopNode', 'FootLoopNode', 'BranchNode', 'CaseNode', 'TryCatchNode', 'FunctionNode'];
    this.preRender();
  }
  preRender() {
    const divInsert = document.createElement('div');
    divInsert.classList.add('columnEditorFull');
    const divHeader = document.createElement('div');
    // divHeader.classList.add('elementButtonColumns');
    const spanHeader = document.createElement('strong');
    spanHeader.classList.add('margin-small');
    spanHeader.appendChild(document.createTextNode('Element wählen:'));
    divHeader.appendChild(spanHeader);
    divInsert.appendChild(divHeader);
    const divButtons = document.createElement('div');
    divButtons.classList.add('container', 'justify-center');
    for (const item of this.buttonList) {
      if (config.config.get()[item].use) {
        divButtons.appendChild(this.createButton(item));
      }
    }
    divInsert.appendChild(divButtons);
    const divEditorHeadline = document.createElement('div');
    divEditorHeadline.classList.add('columnEditorFull', 'headerContainer');
    const editorHeadline = document.createElement('strong');
    editorHeadline.classList.add('margin-small', 'floatBottom');
    editorHeadline.appendChild(document.createTextNode('Editor:'));
    divEditorHeadline.appendChild(editorHeadline);
    const optionsContainer1 = document.createElement('div');
    optionsContainer1.id = 'struktoOptions1';
    optionsContainer1.classList.add('struktoOptions1');
    divEditorHeadline.appendChild(optionsContainer1);
    this.createStrukOptions(optionsContainer1);
    const divEditorContent = document.createElement('div');
    divEditorContent.classList.add('vcontainer', 'columnEditorStructogram');
    divEditorContent.id = 'editorContent';
    const divEditorContentSplitTop = document.createElement('div');
    divEditorContentSplitTop.classList.add('columnAuto', 'container');
    const divEditorContentSplitBottom = document.createElement('div');
    divEditorContentSplitBottom.classList.add('columnAuto-6');
    const divFixRightBorder = document.createElement('div');
    divFixRightBorder.classList.add('borderWidth', 'frameLeft');
    const divWorkingArea = document.createElement('div');
    divWorkingArea.classList.add('columnAuto');
    divWorkingArea.id = 'structogram';
    divEditorContent.appendChild(divEditorContentSplitTop);
    divEditorContentSplitTop.appendChild(divWorkingArea);
    divEditorContentSplitTop.appendChild(divFixRightBorder);
    divEditorContent.appendChild(divEditorContentSplitBottom);
    const editorOptions = document.createElement('div');
    editorOptions.classList.add('columnEditorOptions', 'columnFull');
    this.domRoot.appendChild(divInsert);
    this.domRoot.appendChild(editorOptions);
    this.domRoot.appendChild(divEditorHeadline);
    this.domRoot.appendChild(divEditorContent);
    const codeAndOptions = document.createElement('div');
    codeAndOptions.classList.add('columnEditorCode', 'vcontainer');
    this.domRoot.appendChild(codeAndOptions);
    const sourcecode = document.createElement('div');
    sourcecode.id = 'SourcecodeDisplay';
    sourcecode.classList.add('fullWidth', 'fullHeight', 'vcontainer');
    sourcecode.style.display = 'none';
    codeAndOptions.appendChild(sourcecode);
    this.domRoot = document.getElementById('structogram');
  }
  createStrukOptions(domNode) {
    this.generateUndoRedoButtons(this.presenter, domNode);
    generateResetButton(this.presenter, domNode);
  }
  generateUndoRedoButtons(presenter, domNode) {
    const undo = document.createElement('div');
    undo.classList.add('struktoOption', 'undoIcon', 'tooltip', 'tooltip-bottom', 'hand');
    undo.setAttribute('data-tooltip', 'Undo');
    domNode.appendChild(undo);
    const undoOverlay = document.createElement('div');
    undoOverlay.classList.add('fullWidth', 'fullHeight', 'UndoIconButtonOverlay', 'disableIcon');
    undoOverlay.addEventListener('click', () => presenter.undo());
    undo.appendChild(undoOverlay);
    const redo = document.createElement('div');
    redo.classList.add('struktoOption', 'redoIcon', 'tooltip', 'tooltip-bottom', 'hand');
    redo.setAttribute('data-tooltip', 'Redo');
    domNode.appendChild(redo);
    const redoOverlay = document.createElement('div');
    redoOverlay.classList.add('fullWidth', 'fullHeight', 'RedoIconButtonOverlay', 'disableIcon');
    redoOverlay.addEventListener('click', () => presenter.redo());
    redo.appendChild(redoOverlay);
  }
  createButton(button) {
    const div = document.createElement('div');
    div.classList.add('columnInput', 'insertButton', 'hand');
    div.style.backgroundColor = config.config.get()[button].color;
    div.id = config.config.get()[button].id;
    div.draggable = 'true';
    div.addEventListener('click', event => this.presenter.insertNode(config.config.get()[button].id, event));
    div.addEventListener('dragstart', event => this.presenter.insertNode(config.config.get()[button].id, event));
    div.addEventListener('dragend', () => this.presenter.resetDrop());
    const spanText = document.createElement('span');
    spanText.appendChild(document.createTextNode(config.config.get()[button].text));
    const divIcon = document.createElement('div');
    divIcon.classList.add(config.config.get()[button].icon, 'buttonLogo');
    div.append(divIcon);
    div.append(spanText);
    return div;
  }
  render(tree) {
    // remove content
    while (this.domRoot.hasChildNodes()) {
      this.domRoot.removeChild(this.domRoot.lastChild);
    }
    // this.domRoot.appendChild(this.prepareRenderTree(tree, false, false));
    for (const elem of this.renderElement(tree, false, false, this.presenter.getSettingFunctionMode())) {
      this.applyCodeEventListeners(elem);
      this.domRoot.appendChild(elem);
    }
    const lastLine = document.createElement('div');
    lastLine.classList.add('frameTop', 'borderHeight');
    this.domRoot.appendChild(lastLine);
  }
  updateButtons() {
    // Find the button container and recreate buttons
    const buttonContainer = document.querySelector('.container.justify-center');
    if (buttonContainer) {
      // Clear existing buttons
      while (buttonContainer.hasChildNodes()) {
        buttonContainer.removeChild(buttonContainer.lastChild);
      }

      // Recreate buttons with current configuration
      for (const item of this.buttonList) {
        if (config.config.get()[item].use) {
          buttonContainer.appendChild(this.createButton(item));
        }
      }
    }
  }

  /**
   * @param    divContainer         div containing the function parameters
   * @param    pos                  position in the function header-div
   * @param    fieldSize            size of the input field (only int values)
   * @param    uid                  id of the function node inside the model
   * @param    content              text of the param element
   * @returns  HTMLElement (Input Field)
   */
  createFunctionHeaderTextEl(divContainer, pos, fieldSize, placeHolder, uid, content = null) {
    // add text from input field as span-element to the header-div
    const textNodeSpan = document.createElement('span');
    textNodeSpan.classList.add('func-header-text-div');
    if (content === null || content === '') {
      textNodeSpan.appendChild(document.createTextNode(placeHolder));
    } else {
      textNodeSpan.appendChild(document.createTextNode(content));
    }
    const textNodeDiv = document.createElement('div');
    textNodeDiv.classList.add('function-elem');
    textNodeDiv.style.display = 'flex';
    textNodeDiv.style.flexDirection = 'row';
    textNodeDiv.appendChild(textNodeSpan);

    // delete option for parameters
    if (!divContainer.classList.contains('func-box-header')) {
      const removeParamBtn = document.createElement('button');
      removeParamBtn.classList.add('trashcan', 'optionIcon', 'hand', 'tooltip', 'tooltip-bottoml');
      removeParamBtn.style.minWidth = '1.2em';
      removeParamBtn.style.border = 'none';
      removeParamBtn.setAttribute('data-tooltip', 'Entfernen');
      removeParamBtn.addEventListener('click', () => {
        this.presenter.removeParamFromParameters(pos);
      });
      textNodeSpan.addEventListener('mouseover', () => {
        textNodeSpan.parentElement.appendChild(removeParamBtn);
      });
      textNodeSpan.parentElement.addEventListener('mouseleave', () => {
        removeParamBtn.remove();
      });
    }

    // text can be clicked and afterwards can be changed
    textNodeSpan.addEventListener('click', () => {
      textNodeDiv.remove();

      // div containing input field and field option
      const inputDiv = document.createElement('div');
      inputDiv.style.display = 'flex';
      inputDiv.style.flexDirection = 'row';

      // create Input Field
      const inputElement = newElement('input', ['function-elem', 'func-header-input'], inputDiv);
      inputElement.contentEditable = true;
      inputElement.style.border = 'solid 1px black';
      inputElement.style.margin = '0 0 0 0';
      inputElement.style.width = fieldSize + 'ch';
      inputElement.size = fieldSize;
      inputElement.type = 'text';
      inputElement.placeholder = placeHolder;
      inputElement.value = content;

      // function for creating the text node (function name or parameter name)
      const createTextNode = () => {
        const textNodeDiv = document.createElement('div');
        textNodeDiv.classList.add('function-elem');

        // add text from input field as span-element to the header-div
        const textNodeSpan = newElement('span', ['func-header-text-div'], textNodeDiv);
        textNodeSpan.appendChild(document.createTextNode(inputElement.value));

        // text can be clicked and afterwards can be changed
        textNodeSpan.addEventListener('click', () => {
          textNodeDiv.remove();
          divContainer.insertBefore(inputDiv, divContainer.childNodes[pos]);
        });
        inputElement.remove();
        divContainer.insertBefore(textNodeDiv, divContainer.childNodes[pos]);
      };

      // button to save function or parameter name
      const inputAccept = newElement('div', ['acceptIcon', 'hand'], inputDiv);
      inputAccept.style.minWidth = '1.4em';
      inputAccept.style.marginLeft = '0.2em';
      inputAccept.addEventListener('click', () => {
        // update function name and function parameters in the model tree
        if (divContainer.classList.contains('func-box-header')) {
          this.presenter.editElement(uid, inputElement.value, 'funcname|');
        } else {
          this.presenter.editElement(uid, inputElement.value, String(pos) + '|');
        }

        // change function name also in the model (tree)
        this.presenter.renderAllViews();
        createTextNode();
      });
      let editCancelled = false;
      const cancelEdit = event => {
        editCancelled = true;
        event.preventDefault();
        event.stopPropagation();
        inputElement.removeEventListener('blur', listenerFunction);
        this.presenter.renderAllViews();
      };
      const inputClose = newElement('div', ['deleteIcon', 'hand'], inputDiv);
      inputClose.style.minWidth = '1.4em';
      inputClose.style.marginLeft = '0.2em';
      inputClose.addEventListener('pointerdown', cancelEdit);
      inputClose.addEventListener('click', cancelEdit);
      divContainer.insertBefore(inputDiv, divContainer.childNodes[pos]);
      const listenerFunction = event => {
        if (editCancelled) {
          return;
        }
        if (event.code === 'Enter' || event.type === 'blur') {
          // remove the blur event listener in case of pressing-enter-event to avoid DOM exceptions
          if (event.code === 'Enter') {
            inputElement.removeEventListener('blur', listenerFunction);
          }

          // update function name and function parameters in the model tree
          if (divContainer.classList.contains('func-box-header')) {
            this.presenter.editElement(uid, inputElement.value, 'funcname|');
          } else {
            this.presenter.editElement(uid, inputElement.value, String(pos) + '|');
          }

          // change function name also in the model (tree)
          this.presenter.renderAllViews();
          createTextNode();
        }
      };

      // observed events (to change input field size)
      const events = 'keyup,keypress,focus,blur,change,input'.split(',');
      for (const e of events) {
        inputElement.addEventListener(e, listenerFunction);
      }
    });
    return textNodeDiv;
  }

  /**
   * Create some spacing
   */
  createSpacing(spacingSize) {
    // spacing between elements
    const spacing = document.createElement('div');
    spacing.style.marginRight = spacingSize + 'ch';
    return spacing;
  }

  /**
   * @param   countParam              count of variables inside the paramter div
   * @param   fpSize                  size for the input field
   * @param   paramDiv                div containing the function parameters
   * @param   spacingSize             spacing div between two DOM-elements
   * @param   uid                     id of the function node inside the model
   * create and append a interactable variable to the parameters div
   */
  renderParam(countParam, paramDiv, spacingSize, fpSize, uid, content = null) {
    const paramPos = 3 * countParam;
    // if there is already a function parameter, add some ", " before the next parameter
    if (countParam !== 0) {
      paramDiv.appendChild(document.createTextNode(','));
      paramDiv.appendChild(this.createSpacing(spacingSize));
    }
    countParam += 1;
    paramDiv.appendChild(this.createFunctionHeaderTextEl(paramDiv, paramPos, fpSize, 'par ' + countParam, uid, content));
    return content;
  }

  /**
   * @param    uid                id of the function node inside the model (tree)
   * @param    content            function name given from the model
   * @param    funcParams         variable names of the function paramers
   * Return a function header with function name and parameters for editing
   */
  renderFunctionBox(uid, content, funcParams) {
    // field attributes... ff: function name... fp: parameter name
    // size is field length
    const ffSize = 15;
    const fpSize = 5;
    const spacingSize = 1;

    // box header containing all elements describing the function header
    const functionBoxHeaderDiv = document.createElement('div');
    functionBoxHeaderDiv.classList.add('input-group', 'fixedHeight', 'func-box-header', 'padding');
    functionBoxHeaderDiv.style.display = 'flex';
    functionBoxHeaderDiv.style.flexDirection = 'row';
    functionBoxHeaderDiv.style.paddingTop = '6.5px';

    // header containing all param elements
    const paramDiv = document.createElement('div');
    paramDiv.classList.add('input-group');
    paramDiv.style.display = 'flex';
    paramDiv.style.flexDirection = 'row';
    paramDiv.style.flex = '0 0 ' + spacingSize + 'ch';
    let countParam = 0;
    for (const param of funcParams) {
      this.renderParam(countParam, paramDiv, spacingSize, fpSize, uid, param.parName);
      countParam += 1;
    }

    // append a button for adding new parameters at the end of the param div
    const addParamBtn = document.createElement('button');
    addParamBtn.type = 'button';
    addParamBtn.classList.add('addCaseIcon', 'hand', 'caseOptionsIcons', 'tooltip', 'tooltip-bottom');
    addParamBtn.style.marginTop = 'auto';
    addParamBtn.style.marginBottom = 'auto';
    addParamBtn.setAttribute('data-tooltip', 'Parameter hinzufügen');
    const addParam = event => {
      event.preventDefault();
      event.stopPropagation();
      addParamBtn.remove();
      const countParam = paramDiv.getElementsByClassName('function-elem').length;
      this.renderParam(countParam, paramDiv, spacingSize, fpSize, uid);
    };
    addParamBtn.addEventListener('pointerdown', addParam);

    // show adding-parameters-button when hovering
    functionBoxHeaderDiv.addEventListener('mouseover', () => {
      paramDiv.appendChild(addParamBtn);
    });
    functionBoxHeaderDiv.addEventListener('mouseleave', () => {
      addParamBtn.remove();
    });

    // add all box header elements
    functionBoxHeaderDiv.appendChild(document.createTextNode('function'));
    functionBoxHeaderDiv.appendChild(this.createSpacing(2 * spacingSize));
    functionBoxHeaderDiv.appendChild(this.createFunctionHeaderTextEl(functionBoxHeaderDiv, 2, ffSize, 'func name', uid, content));
    functionBoxHeaderDiv.appendChild(document.createTextNode('('));
    functionBoxHeaderDiv.appendChild(paramDiv);
    functionBoxHeaderDiv.appendChild(document.createTextNode(')'));
    functionBoxHeaderDiv.appendChild(this.createSpacing(spacingSize));
    functionBoxHeaderDiv.appendChild(document.createTextNode('{'));
    const spacer = document.createElement('div');
    spacer.style.marginRight = 'auto';
    functionBoxHeaderDiv.appendChild(spacer);
    return functionBoxHeaderDiv;
  }
  renderElement(subTree, parentIsMoving, noInsert, renderInsertNode = false) {
    const elemArray = [];
    if (subTree === null) {
      return elemArray;
    } else {
      if (!(this.presenter.getMoveId() === null) && subTree.id === this.presenter.getMoveId()) {
        parentIsMoving = true;
        noInsert = true;
      }
      const container = document.createElement('div');
      if (subTree.id) {
        container.id = subTree.id;
      }
      container.classList.add('vcontainer', 'frameTopLeft', 'columnAuto');
      container.style.backgroundColor = config.config.get()[subTree.type].color;
      // container.style.margin = '0 .75px';
      // const element = document.createElement('div');
      // element.classList.add('column', 'vcontainer', 'frameTop');
      // container.appendChild(element);

      switch (subTree.type) {
        case 'InsertNode':
          if (parentIsMoving) {
            return this.renderElement(subTree.followElement, false, false);
          } else {
            if (noInsert) {
              return this.renderElement(subTree.followElement, false, true);
            } else {
              // inserting any other object instead of a function block
              if (this.presenter.getInsertMode()) {
                if (!this.presenter.getSettingFunctionMode()) {
                  const div = document.createElement('div');
                  div.classList.add('container', 'fixedHalfHeight', 'symbol', 'hand', 'text-center');
                  container.addEventListener('dragover', function (event) {
                    event.preventDefault();
                  });
                  container.addEventListener('drop', event => {
                    event.preventDefault();
                    this.presenter.appendElement(subTree.id);
                  });
                  container.addEventListener('click', () => this.presenter.appendElement(subTree.id));
                  if (this.presenter.getMoveId() && subTree.followElement && subTree.followElement.id === this.presenter.getMoveId()) {
                    const bold = document.createElement('strong');
                    bold.classList.add('moveText');
                    bold.appendChild(document.createTextNode('Verschieben abbrechen'));
                    div.appendChild(bold);
                  } else {
                    const symbol = document.createElement('div');
                    symbol.classList.add('insertIcon', 'symbolHeight');
                    div.appendChild(symbol);
                  }
                  container.appendChild(div);
                  elemArray.push(container);
                  if (subTree.followElement === null || subTree.followElement.type === 'Placeholder') {
                    return elemArray;
                  } else {
                    return elemArray.concat(this.renderElement(subTree.followElement, false, noInsert));
                  }
                } else {
                  // container.classList.add('line');
                  if (renderInsertNode) {
                    const div = document.createElement('div');
                    div.classList.add('container', 'fixedHalfHeight', 'symbol', 'hand', 'text-center');
                    container.addEventListener('dragover', function (event) {
                      event.preventDefault();
                    });
                    container.addEventListener('drop', event => {
                      event.preventDefault();
                      this.presenter.appendElement(subTree.id);
                    });
                    container.addEventListener('click', () => this.presenter.appendElement(subTree.id));
                    if (this.presenter.getMoveId() && subTree.followElement && subTree.followElement.id === this.presenter.getMoveId()) {
                      const bold = document.createElement('strong');
                      bold.classList.add('moveText');
                      bold.appendChild(document.createTextNode('Verschieben abbrechen'));
                      div.appendChild(bold);
                    } else {
                      const symbol = document.createElement('div');
                      symbol.classList.add('insertIcon', 'symbolHeight');
                      div.appendChild(symbol);
                    }
                    container.appendChild(div);
                    elemArray.push(container);
                    if (subTree.followElement === null || subTree.followElement.type === 'Placeholder') {
                      return elemArray;
                    } else {
                      return elemArray.concat(this.renderElement(subTree.followElement, false, noInsert));
                    }
                  } else {
                    return this.renderElement(subTree.followElement, false, noInsert);
                  }
                }
              } else {
                return this.renderElement(subTree.followElement, parentIsMoving, noInsert);
              }
            }
          }
        case 'Placeholder':
          {
            const div = document.createElement('div');
            div.classList.add('container', 'fixedHeight');
            const symbol = document.createElement('div');
            symbol.classList.add('placeholder', 'symbolHeight', 'symbol');
            div.appendChild(symbol);
            container.appendChild(div);
            elemArray.push(container);
            return elemArray;
          }
        case 'InsertCase':
          {
            container.classList.remove('frameTopLeft', 'columnAuto');
            container.classList.add('frameLeft', 'fixedHeight');
            const divTaskNode = document.createElement('div');
            divTaskNode.classList.add('fixedHeight', 'container');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divTaskNode.appendChild(textDiv);
            divTaskNode.appendChild(optionDiv);

            // container.classList.add('line');
            container.appendChild(divTaskNode);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'InputNode':
        case 'OutputNode':
        case 'TaskNode':
          {
            const divTaskNode = document.createElement('div');
            divTaskNode.classList.add('fixedHeight', 'container');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divTaskNode.appendChild(textDiv);
            divTaskNode.appendChild(optionDiv);

            // container.classList.add('line');
            container.appendChild(divTaskNode);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'BranchNode':
          {
            // //container.classList.add('fix');
            const divBranchNode = document.createElement('div');
            divBranchNode.classList.add('columnAuto', 'vcontainer');
            const divHead = document.createElement('div');
            divHead.classList.add('branchSplit', 'vcontainer', 'fixedDoubleHeight');
            const divHeadTop = document.createElement('div');
            divHeadTop.classList.add('fixedHeight', 'container');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divHeadTop.appendChild(textDiv);
            divHeadTop.appendChild(optionDiv);
            const divHeadBottom = document.createElement('div');
            divHeadBottom.classList.add('fixedHeight', 'container', 'padding');
            const divHeaderTrue = document.createElement('div');
            divHeaderTrue.classList.add('columnAuto', 'text-left', 'bottomHeader');
            divHeaderTrue.appendChild(document.createTextNode('Wahr'));
            const divHeaderFalse = document.createElement('div');
            divHeaderFalse.classList.add('columnAuto', 'text-right', 'bottomHeader');
            divHeaderFalse.appendChild(document.createTextNode('Falsch'));
            divHeadBottom.appendChild(divHeaderTrue);
            divHeadBottom.appendChild(divHeaderFalse);
            divHead.appendChild(divHeadTop);
            divHead.appendChild(divHeadBottom);
            divBranchNode.appendChild(divHead);
            const divChildren = document.createElement('div');
            divChildren.classList.add('columnAuto', 'branchCenter', 'container');
            const divTrue = document.createElement('div');
            divTrue.classList.add('columnAuto', 'vcontainer', 'ov-hidden');
            for (const elem of this.renderElement(subTree.trueChild, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divTrue.appendChild(elem);
            }
            const divFalse = document.createElement('div');
            divFalse.classList.add('columnAuto', 'vcontainer', 'ov-hidden');
            for (const elem of this.renderElement(subTree.falseChild, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divFalse.appendChild(elem);
            }
            divChildren.appendChild(divTrue);
            divChildren.appendChild(divFalse);
            divBranchNode.appendChild(divChildren);
            container.appendChild(divBranchNode);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'TryCatchNode':
          {
            const divTryCatchNode = newElement('div', ['columnAuto', 'vcontainer', 'tryCatchNode'], container);
            const divTry = newElement('div', ['container', 'fixedHeight', 'padding'], divTryCatchNode);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divTry.appendChild(optionDiv);
            const textTry = newElement('div', ['symbol'], divTry);
            textTry.appendChild(document.createTextNode('Try'));
            const divTryContent = newElement('div', ['columnAuto', 'container', 'loopShift'], divTryCatchNode);
            const divTryContentBody = newElement('div', ['loopWidth', 'frameLeft', 'vcontainer'], divTryContent);
            for (const elem of this.renderElement(subTree.tryChild, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divTryContentBody.appendChild(elem);
            }

            // container for the vertical line to indent it correctly
            const vertLineContainer = newElement('div', ['container', 'columnAuto', 'loopShift'], divTryCatchNode);
            const vertLine2 = newElement('div', ['loopWidth', 'vcontainer'], vertLineContainer);
            const vertLine = newElement('div', ['frameLeftBottom'], vertLine2);
            vertLine.style.flex = '0 0 3px';
            const divCatch = newElement('div', ['container', 'fixedHeight', 'padding', 'tryCatchNode'], divTryCatchNode);
            const textCatch = newElement('div', ['symbol'], divCatch);
            textCatch.appendChild(document.createTextNode('Catch'));
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            divCatch.appendChild(textDiv);
            const divCatchContent = newElement('div', ['columnAuto', 'container', 'loopShift'], divTryCatchNode);
            const divCatchContentBody = newElement('div', ['loopWidth', 'frameLeft', 'vcontainer'], divCatchContent);
            for (const elem of this.renderElement(subTree.catchChild, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divCatchContentBody.appendChild(elem);
            }
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'HeadLoopNode':
        case 'CountLoopNode':
          {
            const div = document.createElement('div');
            div.classList.add('columnAuto', 'vcontainer');
            const divHead = document.createElement('div');
            divHead.classList.add('container', 'fixedHeight');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divHead.appendChild(textDiv);
            divHead.appendChild(optionDiv);
            div.appendChild(divHead);
            const divChild = document.createElement('div');
            divChild.classList.add('columnAuto', 'container', 'loopShift');
            const divLoop = document.createElement('div');
            divLoop.classList.add('loopWidth', 'frameLeft', 'vcontainer');
            for (const elem of this.renderElement(subTree.child, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divLoop.appendChild(elem);
            }
            divChild.appendChild(divLoop);
            div.appendChild(divChild);
            container.appendChild(div);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'FunctionNode':
          {
            const innerDiv = document.createElement('div');
            innerDiv.classList.add('columnAuto', 'vcontainer');
            const divFunctionHeader = this.renderFunctionBox(subTree.id, subTree.text, subTree.parameters);
            const divHead = document.createElement('div');
            divHead.classList.add('container', 'fixedHeight');
            const funcOptionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divHead.appendChild(funcOptionDiv);
            divFunctionHeader.appendChild(divHead);
            const divChild = document.createElement('div');
            divChild.classList.add('columnAuto', 'container', 'loopShift');

            // creates the inside of the functionf
            const divFunctionBody = document.createElement('div');
            divFunctionBody.classList.add('loopWidth', 'frameLeft', 'vcontainer');
            for (const elem of this.renderElement(subTree.child, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divFunctionBody.appendChild(elem);
            }
            divChild.appendChild(divFunctionBody);
            const divFuncFoot = document.createElement('div');
            divFuncFoot.classList.add('container', 'fixedHeight', 'padding');
            const textNode = document.createElement('div');
            textNode.classList.add('symbol');
            textNode.appendChild(document.createTextNode('}'));
            divFuncFoot.appendChild(textNode);
            const vertLine = document.createElement('div');
            vertLine.classList.add('frameLeftBottom');
            vertLine.style.flex = '0 0 3px';

            // container for the vertical line to indent it correctly
            const vertLineContainer = document.createElement('div');
            vertLineContainer.classList.add('container', 'columnAuto', 'loopShift');
            const vertLine2 = document.createElement('div');
            vertLine2.classList.add('loopWidth', 'vcontainer');
            vertLine2.appendChild(vertLine);
            vertLineContainer.appendChild(vertLine2);
            innerDiv.appendChild(divFunctionHeader);
            innerDiv.appendChild(divChild);
            innerDiv.appendChild(vertLineContainer);
            innerDiv.appendChild(divFuncFoot);
            container.appendChild(innerDiv);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'FootLoopNode':
          {
            const div = document.createElement('div');
            div.classList.add('columnAuto', 'vcontainer');
            const divChild = document.createElement('div');
            divChild.classList.add('columnAuto', 'container', 'loopShift');
            const divLoop = document.createElement('div');
            divLoop.classList.add('loopWidth', 'frameLeftBottom', 'vcontainer');
            for (const elem of this.renderElement(subTree.child, false, noInsert)) {
              this.applyCodeEventListeners(elem);
              divLoop.appendChild(elem);
            }
            // Fix for overlapped bottom line
            const lastLine = document.createElement('div');
            lastLine.classList.add('borderHeight');
            divLoop.appendChild(lastLine);
            divChild.appendChild(divLoop);
            div.appendChild(divChild);
            const divFoot = document.createElement('div');
            divFoot.classList.add('container', 'fixedHeight');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divFoot.appendChild(textDiv);
            divFoot.appendChild(optionDiv);
            div.appendChild(divFoot);
            container.appendChild(div);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
        case 'CaseNode':
          {
            const div = document.createElement('div');
            div.classList.add('columnAuto', 'vcontainer');
            const divHead = document.createElement('div');
            divHead.classList.add('vcontainer', 'fixedHeight');
            if (subTree.defaultOn) {
              divHead.classList.add('caseHead-' + subTree.cases.length);
            } else {
              divHead.classList.add('caseHead-noDefault-' + subTree.cases.length);
            }
            divHead.style.backgroundPosition = '1px 0px';
            let nrCases = subTree.cases.length;
            if (!subTree.defaultOn) {
              nrCases = nrCases + 2;
            }
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id, nrCases);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divHead.appendChild(textDiv);
            divHead.appendChild(optionDiv);
            div.appendChild(divHead);
            const divChildren = document.createElement('div');
            divChildren.classList.add('columnAuto', 'container');
            if (subTree.defaultOn) {
              divChildren.classList.add('caseBody-' + subTree.cases.length);
            } else {
              const level = subTree.cases.length - 1;
              divChildren.classList.add('caseBody-' + level);
            }
            for (const caseElem of subTree.cases) {
              const divCase = document.createElement('div');
              divCase.classList.add('columnAuto', 'vcontainer', 'ov-hidden');
              for (const elem of this.renderElement(caseElem, false, noInsert)) {
                this.applyCodeEventListeners(elem);
                divCase.appendChild(elem);
              }
              divChildren.appendChild(divCase);
            }
            if (subTree.defaultOn) {
              const divCase = document.createElement('div');
              divCase.classList.add('columnAuto', 'vcontainer', 'ov-hidden');
              for (const elem of this.renderElement(subTree.defaultNode, false, noInsert)) {
                this.applyCodeEventListeners(elem);
                divCase.appendChild(elem);
              }
              divChildren.appendChild(divCase);
            }
            div.appendChild(divChildren);
            container.appendChild(div);
            elemArray.push(container);
            return elemArray.concat(this.renderElement(subTree.followElement, parentIsMoving, noInsert));
          }
      }
    }
  }

  /**
   * Reset the buttons after an insert or false drop
   */
  resetButtons() {
    // remove color of buttons
    for (const button of this.buttonList) {
      if (config.config.get()[button].use) {
        document.getElementById(config.config.get()[button].id).classList.remove('boldText');
      }
    }
  }

  /**
   * Increase the size of the working area
   */
  increaseSize() {
    // only allow a max size of ten (flexbox)
    if (this.size < 10) {
      const element = document.getElementById('Sizelimiter');
      element.classList.remove('col-' + this.size);
      this.size = this.size + 1;
      element.classList.add('col-' + this.size);
    }
  }

  /**
   * Decrease the size of the working area
   */
  decreaseSize() {
    // only allow a minimal size of 6 (flexbox)
    if (this.size > 6) {
      const element = document.getElementById('Sizelimiter');
      element.classList.remove('col-' + this.size);
      this.size = this.size - 1;
      element.classList.add('col-' + this.size);
    }
  }

  /**
   * Create a HTML wrapper around a div element, to fully work with the flexbox grid
   *
   * @param    div          the HTML structure to be wrapped
   * @param    inserting    identifies the div as InsertNode
   * @param    moving       identifies the div as the original position while moving
   * @return   div          completly wrapped HTML element
   */
  addCssWrapper(div, inserting, moving) {
    const innerDiv = document.createElement('div');
    innerDiv.classList.add('column');
    innerDiv.classList.add('col-12');
    innerDiv.classList.add('lineTop');
    const box = document.createElement('div');
    box.classList.add('row');

    // element is a InsertNode
    if (inserting) {
      box.classList.add('bg-secondary');
      box.classList.add('simpleBorder');
    }
    // element is original InsertNode while moving a block
    if (moving) {
      box.classList.add('bg-primary');
      box.classList.add('simpleBorder');
    }
    innerDiv.appendChild(div);
    box.appendChild(innerDiv);
    return box;
  }
  openCaseOptions(uid) {
    const content = document.getElementById('modal-content');
    const footer = document.getElementById('modal-footer');
    while (content.hasChildNodes()) {
      content.removeChild(content.lastChild);
    }
    while (footer.hasChildNodes()) {
      footer.removeChild(footer.lastChild);
    }
    const element = this.presenter.getElementByUid(uid);
    const title = document.createElement('strong');
    title.appendChild(document.createTextNode('Einstellungen der ' + config.config.get().CaseNode.text + ': '));
    content.appendChild(title);
    const elementText = document.createElement('div');
    elementText.classList.add('caseTitle', 'boldText');
    elementText.appendChild(document.createTextNode(element.text));
    content.appendChild(elementText);
    const list = document.createElement('dl');
    list.classList.add('container');
    content.appendChild(list);
    const caseNumberTitle = document.createElement('dt');
    caseNumberTitle.classList.add('dtItem');
    caseNumberTitle.appendChild(document.createTextNode('Anzahl der Fälle:'));
    list.appendChild(caseNumberTitle);
    const caseNumber = document.createElement('dd');
    caseNumber.classList.add('ddItem', 'container');
    list.appendChild(caseNumber);
    const caseNr = document.createElement('div');
    caseNr.classList.add('text-center', 'shortenOnMobile');
    caseNr.appendChild(document.createTextNode(element.cases.length));
    caseNumber.appendChild(caseNr);
    const addCase = document.createElement('div');
    addCase.classList.add('addCaseIcon', 'hand', 'caseOptionsIcons', 'tooltip', 'tooltip-bottom');
    addCase.addEventListener('click', () => {
      this.presenter.addCase(uid);
      this.openCaseOptions(uid);
    });
    addCase.setAttribute('data-tooltip', 'Fall hinzufügen');
    caseNumber.appendChild(addCase);
    const defaultOnTitle = document.createElement('dt');
    defaultOnTitle.classList.add('dtItem');
    defaultOnTitle.appendChild(document.createTextNode('Sonst Zweig einschalten:'));
    list.appendChild(defaultOnTitle);
    const defaultOn = document.createElement('dd');
    defaultOn.classList.add('ddItem', 'container');
    defaultOn.addEventListener('click', () => {
      this.presenter.switchDefaultState(uid);
      this.openCaseOptions(uid);
    });
    list.appendChild(defaultOn);
    const defaultNo = document.createElement('div');
    defaultNo.classList.add('text-center', 'shortenOnMobile');
    defaultNo.setAttribute('data-abbr', 'N');
    defaultOn.appendChild(defaultNo);
    const defaultNoText = document.createElement('span');
    defaultNoText.appendChild(document.createTextNode('Nein'));
    defaultNo.appendChild(defaultNoText);
    const switchDefault = document.createElement('div');
    switchDefault.classList.add('hand', 'caseOptionsIcons');
    if (element.defaultOn) {
      switchDefault.classList.add('switchOn');
    } else {
      switchDefault.classList.add('switchOff');
    }
    defaultOn.appendChild(switchDefault);
    const defaultYes = document.createElement('div');
    defaultYes.classList.add('text-center', 'shortenOnMobile');
    defaultYes.setAttribute('data-abbr', 'J');
    defaultOn.appendChild(defaultYes);
    const defaultYesText = document.createElement('span');
    defaultYesText.appendChild(document.createTextNode('Ja'));
    defaultYes.appendChild(defaultYesText);
    const cancelButton = document.createElement('div');
    cancelButton.classList.add('modal-buttons', 'hand');
    cancelButton.appendChild(document.createTextNode('Schließen'));
    cancelButton.addEventListener('click', () => document.getElementById('IEModal').classList.remove('active'));
    footer.appendChild(cancelButton);
    document.getElementById('IEModal').classList.add('active');
  }

  /**
   * Create option elements and add them to the displayed element
   *
   * @param    type   type of the element
   * @param    uid    id of the current struktogramm element
   * @return   div    complete HTML structure of the options for the element
   */
  createOptionDiv(type, uid) {
    // create the container for all options
    const optionDiv = document.createElement('div');
    optionDiv.classList.add('optionContainer');

    // case nodes have additional options
    if (type === 'CaseNode') {
      const caseOptions = document.createElement('div');
      caseOptions.classList.add('gearIcon', 'optionIcon', 'hand', 'tooltip', 'tooltip-bottoml');
      caseOptions.setAttribute('data-tooltip', 'Einstellung');
      caseOptions.addEventListener('click', () => this.openCaseOptions(uid));
      optionDiv.appendChild(caseOptions);
    }

    // all elements can be moved, except InsertCases they are bind to the case node
    if (type !== 'InsertCase' && type !== 'FunctionNode') {
      const moveElem = document.createElement('div');
      moveElem.classList.add('moveIcon');
      moveElem.classList.add('optionIcon');
      moveElem.classList.add('hand');
      moveElem.classList.add('tooltip');
      moveElem.classList.add('tooltip-bottoml');
      moveElem.setAttribute('data-tooltip', 'Verschieben');
      moveElem.addEventListener('click', () => this.presenter.moveElement(uid));
      optionDiv.appendChild(moveElem);
    }

    // every element can be deleted
    const deleteElem = document.createElement('div');
    deleteElem.classList.add('trashcan');
    deleteElem.classList.add('optionIcon');
    deleteElem.classList.add('hand');
    deleteElem.classList.add('tooltip');
    deleteElem.classList.add('tooltip-bottoml');
    deleteElem.setAttribute('data-tooltip', 'Entfernen');
    deleteElem.addEventListener('click', () => this.presenter.removeElement(uid));
    optionDiv.appendChild(deleteElem);
    return optionDiv;
  }

  /**
   * Create the displayed text and edit input field
   *
   * @param    type      type of the element
   * @param    content   displayed text
   * @param    uid       id of the element
   * @return   div       complete build HTML structure
   */
  createTextDiv(type, content, uid, nrCases = null) {
    // create the parent container
    const textDiv = document.createElement('div');
    textDiv.classList.add('columnAuto', 'symbol');

    // this div contains the hidden inputfield
    const editDiv = document.createElement('div');
    editDiv.classList.add('input-group', 'editField');
    editDiv.style.display = 'none';
    if (type === 'FootLoopNode') {
      editDiv.classList.add(uid);
    }

    // inputfield with eventlisteners
    const editText = document.createElement('input');
    editText.type = 'text';
    editText.value = content;
    // TODO: move to presenter
    editText.addEventListener('keyup', event => {
      if (event.keyCode === 13) {
        this.presenter.editElement(uid, editText.value);
      }
      if (event.keyCode === 27) {
        this.presenter.renderAllViews();
      }
    });

    // add apply button
    const editApply = document.createElement('div');
    editApply.classList.add('acceptIcon', 'hand');
    editApply.addEventListener('click', () => this.presenter.editElement(uid, editText.value));

    // add dismiss button
    const editDismiss = document.createElement('div');
    editDismiss.classList.add('deleteIcon', 'hand');
    editDismiss.addEventListener('click', () => this.presenter.renderAllViews());

    // some types need additional text or a different position
    switch (type) {
      case 'InputNode':
        content = 'E: ' + content;
        break;
      case 'OutputNode':
        content = 'A: ' + content;
        break;
      case 'BranchNode':
      case 'InsertCase':
        textDiv.classList.add('text-center');
        break;
    }

    // add displayed text when not in editing mode
    const innerTextDiv = document.createElement('div');
    // innerTextDiv.classList.add('column');
    // innerTextDiv.classList.add('col-12');
    // special handling for the default case of case nodes
    if (!(type === 'InsertCase' && content === 'Sonst')) {
      innerTextDiv.classList.add('padding');
      if (!this.presenter.getInsertMode()) {
        innerTextDiv.classList.add('hand', 'fullHeight');
      }
      innerTextDiv.addEventListener('click', () => {
        this.presenter.renderAllViews();
        this.presenter.switchEditState(uid);
      });
    }

    // insert text
    const textSpan = document.createElement('span');
    if (type === 'CaseNode') {
      textSpan.style.marginLeft = 'calc(' + nrCases / (nrCases + 1) * 100 + '% - 2em)';
    }
    const text = document.createTextNode(content);
    editDiv.appendChild(editText);
    editDiv.appendChild(editApply);
    editDiv.appendChild(editDismiss);
    textSpan.appendChild(text);
    innerTextDiv.appendChild(textSpan);
    textDiv.appendChild(innerTextDiv);
    textDiv.appendChild(editDiv);
    return textDiv;
  }
  applyCodeEventListeners(obj) {
    // do not apply event listeners if obj is the function block
    if (!obj.firstChild.classList.contains('func-box-header')) {
      if (obj.firstChild.firstChild.classList.contains('loopShift')) {
        obj.firstChild.lastChild.addEventListener('mouseover', function () {
          const elemSpan = document.getElementById(obj.id + '-codeLine');
          if (elemSpan) {
            elemSpan.classList.add('highlight');
          }
        });
        obj.firstChild.lastChild.addEventListener('mouseout', function () {
          const elemSpan = document.getElementById(obj.id + '-codeLine');
          if (elemSpan) {
            elemSpan.classList.remove('highlight');
          }
        });
      } else {
        obj.firstChild.firstChild.addEventListener('mouseover', function () {
          const elemSpan = document.getElementById(obj.id + '-codeLine');
          if (elemSpan) {
            elemSpan.classList.add('highlight');
          }
        });
        obj.firstChild.firstChild.addEventListener('mouseout', function () {
          const elemSpan = document.getElementById(obj.id + '-codeLine');
          if (elemSpan) {
            elemSpan.classList.remove('highlight');
          }
        });
      }
    }
  }

  /**
   * Create an outer HTML structure before adding another element
   *
   * @param    subTree          part of the tree with all children of current element
   * @param    parentIsMoving   must be passed down to renderTree
   * @param    noInsert         must be passed down to renderTree
   * @return   div              complete wrapped HTML structure
   */
  prepareRenderTree(subTree, parentIsMoving, noInsert) {
    // end of recursion
    if (subTree === null || subTree.type === 'InsertNode' && subTree.followElement === null && !this.presenter.getInsertMode()) {
      return document.createTextNode('');
    } else {
      // create outlining structure
      const innerDiv = document.createElement('div');
      innerDiv.classList.add('column');
      innerDiv.classList.add('col-12');
      innerDiv.classList.add('lineTop');
      const box = document.createElement('div');
      box.classList.add('columns');
      if (subTree.type !== 'InsertCase') {
        box.classList.add('lineTop');
      }
      // render every element and append it to the outlining structure
      this.renderTree(subTree, parentIsMoving, noInsert).forEach(function (childElement) {
        innerDiv.appendChild(childElement);
      });
      box.appendChild(innerDiv);
      return box;
    }
  }

  /**
   * Create for every element a HTML representation and recursively render the next element
   *
   * @param    subTree          part of the tree with all children of current element
   * @param    parentIsMoving   get set to true, when the moving element is found in the tree
   * @param    noInsert         indicates a parent element is in the move state, so no InsertNodes should be displayed on the children
   * @return   []               array of div elements with the HTML representation of the element
   */
  renderTree(subTree, parentIsMoving, noInsert) {
    if (subTree === null) {
      return [];
    } else {
      if (!(this.presenter.getMoveId() === null) && subTree.id === this.presenter.getMoveId()) {
        parentIsMoving = true;
        noInsert = true;
      }
      switch (subTree.type) {
        case 'InsertNode':
          if (parentIsMoving) {
            return this.renderTree(subTree.followElement, false, false);
          } else {
            if (noInsert) {
              return this.renderTree(subTree.followElement, false, true);
            } else {
              if (this.presenter.getInsertMode()) {
                const div = document.createElement('div');
                div.id = subTree.id;
                // div.classList.add('c-hand');
                // div.classList.add('text-center');
                div.addEventListener('dragover', function (event) {
                  event.preventDefault();
                });
                div.addEventListener('drop', () => this.presenter.appendElement(subTree.id));
                div.addEventListener('click', () => this.presenter.appendElement(subTree.id));
                const text = document.createElement('div');
                if (this.presenter.getMoveId() && subTree.followElement && subTree.followElement.id === this.presenter.getMoveId()) {
                  const bold = document.createElement('strong');
                  bold.appendChild(document.createTextNode('Verschieben abbrechen'));
                  text.appendChild(bold);
                } else {
                  text.classList.add('insertIcon');
                }
                // text.classList.add('p-centered');
                div.appendChild(text);
                if (subTree.followElement === null || subTree.followElement.type === 'Placeholder') {
                  return [this.addCssWrapper(div, true, parentIsMoving)];
                } else {
                  return [this.addCssWrapper(div, true, parentIsMoving), this.prepareRenderTree(subTree.followElement, false, noInsert)];
                }
              } else {
                return this.renderTree(subTree.followElement, parentIsMoving, noInsert);
              }
            }
          }
        case 'Placeholder':
          if (this.presenter.getInsertMode()) {
            return [];
          } else {
            const div = document.createElement('div');
            div.classList.add('text-center');
            const text = document.createElement('div');
            text.classList.add('emptyStateIcon');
            text.classList.add('p-centered');
            div.appendChild(text);
            return [div];
          }
        case 'InputNode':
        case 'OutputNode':
        case 'TaskNode':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            div.classList.add('columns');
            div.classList.add('element');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            div.appendChild(textDiv);
            div.appendChild(optionDiv);
            return [this.addCssWrapper(div, false, parentIsMoving), this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        case 'FunctionNode':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            div.classList.add(['columns', 'element']);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            div.appendChild(optionDiv);
            return [this.addCssWrapper(div, false, parentIsMoving), this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        case 'BranchNode':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            const divHead = document.createElement('div');
            divHead.classList.add('columns');
            divHead.classList.add('element');
            divHead.classList.add('stBranch');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            const bufferDiv = document.createElement('div');
            bufferDiv.classList.add('column');
            bufferDiv.classList.add('col-1');
            divHead.appendChild(bufferDiv);
            divHead.appendChild(textDiv);
            divHead.appendChild(optionDiv);
            const divPreSubHeader = document.createElement('div');
            divPreSubHeader.classList.add('column');
            divPreSubHeader.classList.add('col-12');
            const divSubHeader = document.createElement('div');
            divSubHeader.classList.add('columns');
            const divSubHeaderTrue = document.createElement('div');
            divSubHeaderTrue.classList.add('column');
            divSubHeaderTrue.classList.add('col-6');
            divSubHeaderTrue.appendChild(document.createTextNode('Wahr'));
            const divSubHeaderFalse = document.createElement('div');
            divSubHeaderFalse.classList.add('column');
            divSubHeaderFalse.classList.add('col-6');
            divSubHeaderFalse.classList.add('text-right');
            divSubHeaderFalse.appendChild(document.createTextNode('Falsch'));
            divSubHeader.appendChild(divSubHeaderTrue);
            divSubHeader.appendChild(divSubHeaderFalse);
            divPreSubHeader.appendChild(divSubHeader);
            divHead.appendChild(divPreSubHeader);
            const divTrue = document.createElement('div');
            divTrue.classList.add('column');
            divTrue.classList.add('col-6');
            divTrue.appendChild(this.prepareRenderTree(subTree.trueChild, false, noInsert));
            const divFalse = document.createElement('div');
            divFalse.classList.add('column');
            divFalse.classList.add('col-6');
            divFalse.appendChild(this.prepareRenderTree(subTree.falseChild, false, noInsert));
            const divChildren = document.createElement('div');
            divChildren.classList.add('columns');
            divChildren.classList.add('middleBranch');
            divChildren.appendChild(divTrue);
            divChildren.appendChild(divFalse);
            div.appendChild(divHead);
            div.appendChild(divChildren);
            return [this.addCssWrapper(div, false, parentIsMoving), this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        case 'HeadLoopNode':
        case 'CountLoopNode':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            const divHead = document.createElement('div');
            divHead.classList.add('columns');
            divHead.classList.add('element');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divHead.appendChild(textDiv);
            divHead.appendChild(optionDiv);
            const divLoopSubSub = document.createElement('div');
            divLoopSubSub.classList.add('column');
            divLoopSubSub.classList.add('col-12');
            divLoopSubSub.appendChild(this.prepareRenderTree(subTree.child, false, noInsert));
            const divLoopSub = document.createElement('div');
            divLoopSub.classList.add('columns');
            divLoopSub.appendChild(divLoopSubSub);
            const divLoop = document.createElement('div');
            divLoop.classList.add('column');
            divLoop.classList.add('col-11');
            divLoop.classList.add('col-ml-auto');
            divLoop.classList.add('lineLeft');
            divLoop.appendChild(divLoopSub);
            const divChild = document.createElement('div');
            divChild.classList.add('columns');
            divChild.appendChild(divLoop);
            div.appendChild(divHead);
            div.appendChild(divChild);
            return [this.addCssWrapper(div, false, parentIsMoving), this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        case 'FootLoopNode':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            const divFoot = document.createElement('div');
            divFoot.classList.add('columns');
            divFoot.classList.add('element');
            divFoot.classList.add('lineTopFootLoop');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            divFoot.appendChild(textDiv);
            divFoot.appendChild(optionDiv);
            const divLoop = document.createElement('div');
            divLoop.classList.add('column');
            divLoop.classList.add('col-11');
            divLoop.classList.add('col-ml-auto');
            divLoop.classList.add('lineLeft');
            divLoop.appendChild(this.prepareRenderTree(subTree.child, false, noInsert));
            const divChild = document.createElement('div');
            divChild.classList.add('columns');
            divChild.appendChild(divLoop);
            div.appendChild(divChild);
            div.appendChild(divFoot);
            return [this.addCssWrapper(div, false, parentIsMoving), this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        case 'CaseNode':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            const divHead = document.createElement('div');
            divHead.classList.add('columns');
            divHead.classList.add('element');
            if (subTree.defaultOn) {
              divHead.classList.add('caseHead-' + subTree.cases.length);
            } else {
              divHead.classList.add('caseHead-noDefault-' + subTree.cases.length);
            }
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            const bufferDiv = document.createElement('div');
            bufferDiv.classList.add('column');
            bufferDiv.classList.add('col-1');
            divHead.appendChild(bufferDiv);
            divHead.appendChild(textDiv);
            divHead.appendChild(optionDiv);
            const divPreSubHeader = document.createElement('div');
            divPreSubHeader.classList.add('column');
            divPreSubHeader.classList.add('col-12');
            const divChildren = document.createElement('div');
            divChildren.classList.add('columns');
            if (subTree.defaultOn) {
              divChildren.classList.add('caseBody-' + subTree.cases.length);
            } else {
              const level = subTree.cases.length - 1;
              divChildren.classList.add('caseBody-' + level);
            }
            for (const caseElem of subTree.cases) {
              const divCase = document.createElement('div');
              divCase.classList.add('column');
              divCase.appendChild(this.prepareRenderTree(caseElem, false, noInsert));
              divChildren.appendChild(divCase);
            }
            if (subTree.defaultOn) {
              const divCase = document.createElement('div');
              divCase.classList.add('column');
              divCase.appendChild(this.prepareRenderTree(subTree.defaultNode, false, noInsert));
              divChildren.appendChild(divCase);
            }
            div.appendChild(divHead);
            div.appendChild(divChildren);
            return [this.addCssWrapper(div, false, parentIsMoving), this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        case 'InsertCase':
          {
            const div = document.createElement('div');
            div.id = subTree.id;
            div.classList.add('columns');
            div.classList.add('element');
            const bufferDiv = document.createElement('div');
            bufferDiv.classList.add('column');
            bufferDiv.classList.add('col-1');
            const textDiv = this.createTextDiv(subTree.type, subTree.text, subTree.id);
            const optionDiv = this.createOptionDiv(subTree.type, subTree.id);
            div.appendChild(bufferDiv);
            div.appendChild(textDiv);
            div.appendChild(optionDiv);
            return [div, this.prepareRenderTree(subTree.followElement, parentIsMoving, noInsert)];
          }
        default:
          return this.renderTree(subTree.followElement, parentIsMoving, noInsert);
      }
    }
  }
  displaySourcecode(buttonId) {}
  setLang() {}
}
;// ./src/views/code.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


class CodeView {
  constructor(presenter, domRoot) {
    this.presenter = presenter;
    this.domRoot = domRoot;
    this.lang = '--';
    this.generatedCode = ''; // Store generated code locally for copy function
    this.translationMap = {
      Python: {
        untranslatable: [],
        InputNode: {
          pre: '',
          post: ' = input("Eingabe")\n'
        },
        OutputNode: {
          pre: 'print(',
          post: ')\n'
        },
        TaskNode: {
          pre: '',
          post: '\n'
        },
        BranchNode: {
          pre: 'if ',
          post: ':\n',
          between: 'else:\n'
        },
        TryCatchNode: {
          pre: 'try:\n',
          between: 'except',
          post: ':\n'
        },
        CountLoopNode: {
          pre: 'for ',
          post: ':\n'
        },
        HeadLoopNode: {
          pre: 'while ',
          post: ':\n'
        },
        FunctionNode: {
          pre: 'def ',
          between: '(',
          post: '):\n'
        },
        FootLoopNode: {
          prepre: 'while True:\n',
          pre: '    if not ',
          post: ':\n        break\n'
        },
        CaseNode: {
          pre: 'if ',
          post: ':\n'
        },
        InsertCase: {
          preNormal: 'elif ',
          preDefault: 'default',
          post: ':\n',
          postpost: '\n'
        },
        leftBracket: '',
        rightBracket: '',
        pseudoSwitch: true
      },
      'Python ab v3.10': {
        untranslatable: [],
        InputNode: {
          pre: '',
          post: ' = input("Eingabe")\n'
        },
        OutputNode: {
          pre: 'print(',
          post: ')\n'
        },
        TaskNode: {
          pre: '',
          post: '\n'
        },
        BranchNode: {
          pre: 'if ',
          post: ':\n',
          between: 'else:\n'
        },
        TryCatchNode: {
          pre: 'try:\n',
          between: 'except',
          post: ':\n'
        },
        CountLoopNode: {
          pre: 'for ',
          post: ':\n'
        },
        HeadLoopNode: {
          pre: 'while ',
          post: ':\n'
        },
        FunctionNode: {
          pre: 'def ',
          between: '(',
          post: '):\n'
        },
        FootLoopNode: {
          prepre: 'while True:\n',
          pre: '    if not ',
          post: ':\n        break\n'
        },
        CaseNode: {
          pre: 'match ',
          post: ':\n'
        },
        InsertCase: {
          preNormal: 'case ',
          preDefault: 'case _',
          post: ':\n',
          postpost: '\n'
        },
        leftBracket: '',
        rightBracket: '',
        pseudoSwitch: false
      },
      PHP: {
        untranslatable: [],
        InputNode: {
          pre: '',
          post: ' = readline("Eingabe");\n'
        },
        OutputNode: {
          pre: 'echo ',
          post: ';\n'
        },
        TaskNode: {
          pre: '',
          post: ';\n'
        },
        BranchNode: {
          pre: 'if (',
          post: ')\n',
          between: '} else {\n'
        },
        TryCatchNode: {
          pre: 'try\n',
          between: 'catch (',
          post: ')\n'
        },
        CountLoopNode: {
          pre: 'for (',
          post: ')\n'
        },
        HeadLoopNode: {
          pre: 'while (',
          post: ')\n'
        },
        FootLoopNode: {
          prepre: 'do\n',
          pre: 'while (',
          post: ');\n'
        },
        FunctionNode: {
          pre: 'function ',
          between: '(',
          post: ')\n'
        },
        CaseNode: {
          pre: 'switch (',
          post: ')\n'
        },
        InsertCase: {
          preNormal: 'case ',
          preDefault: 'default',
          post: ':\n',
          postpost: 'break;\n'
        },
        leftBracket: '{',
        rightBracket: '}',
        pseudoSwitch: false
      },
      Java: {
        untranslatable: [],
        InputNode: {
          pre: '',
          post: ' = System.console().readLine();\n'
        },
        OutputNode: {
          pre: 'System.out.println(',
          post: ');\n'
        },
        TaskNode: {
          pre: '',
          post: ';\n'
        },
        BranchNode: {
          pre: 'if (',
          post: ')\n',
          between: '} else {\n'
        },
        TryCatchNode: {
          pre: 'try\n',
          between: 'catch (',
          post: ')\n'
        },
        CountLoopNode: {
          pre: 'for (',
          post: ')\n'
        },
        HeadLoopNode: {
          pre: 'while (',
          post: ')\n'
        },
        FootLoopNode: {
          prepre: 'do\n',
          pre: 'while (',
          post: ');\n'
        },
        FunctionNode: {
          pre: 'public void ',
          between: '(',
          post: ')\n'
        },
        CaseNode: {
          pre: 'switch (',
          post: ')\n'
        },
        InsertCase: {
          preNormal: 'case ',
          preDefault: 'default',
          post: ':\n',
          postpost: 'break;\n'
        },
        leftBracket: '{',
        rightBracket: '}',
        pseudoSwitch: false
      },
      'C#': {
        untranslatable: [],
        InputNode: {
          pre: '',
          post: ' = Console.ReadLine();\n'
        },
        OutputNode: {
          pre: 'Console.WriteLine(',
          post: ');\n'
        },
        TaskNode: {
          pre: '',
          post: ';\n'
        },
        BranchNode: {
          pre: 'if (',
          post: ')\n',
          between: '} else {\n'
        },
        TryCatchNode: {
          pre: 'try\n',
          between: 'catch (',
          post: ')\n'
        },
        CountLoopNode: {
          pre: 'for (',
          post: ')\n'
        },
        HeadLoopNode: {
          pre: 'while (',
          post: ')\n'
        },
        FootLoopNode: {
          prepre: 'do\n',
          pre: 'while (',
          post: ');\n'
        },
        FunctionNode: {
          pre: 'public void ',
          between: '(',
          post: ')\n'
        },
        CaseNode: {
          pre: 'switch (',
          post: ')\n'
        },
        InsertCase: {
          preNormal: 'case ',
          preDefault: 'default',
          post: ':\n',
          postpost: 'break;\n'
        },
        leftBracket: '{',
        rightBracket: '}',
        pseudoSwitch: false
      },
      'C++': {
        untranslatable: [],
        InputNode: {
          pre: 'std::cin >> ',
          post: ';\n'
        },
        OutputNode: {
          pre: 'std::cout << ',
          post: ';\n'
        },
        TaskNode: {
          pre: '',
          post: ';\n'
        },
        BranchNode: {
          pre: 'if (',
          post: ')\n',
          between: '} else {\n'
        },
        TryCatchNode: {
          pre: 'try\n',
          between: 'catch (',
          post: ')\n'
        },
        CountLoopNode: {
          pre: 'for (',
          post: ')\n'
        },
        HeadLoopNode: {
          pre: 'while (',
          post: ')\n'
        },
        FootLoopNode: {
          prepre: 'do\n',
          pre: 'while (',
          post: ');\n'
        },
        FunctionNode: {
          pre: 'void ',
          between: '(',
          post: ')\n'
        },
        CaseNode: {
          pre: 'switch (',
          post: ')\n'
        },
        InsertCase: {
          preNormal: 'case ',
          preDefault: 'default',
          post: ':\n',
          postpost: 'break;\n'
        },
        leftBracket: '{',
        rightBracket: '}',
        pseudoSwitch: false
      },
      C: {
        untranslatable: ['TryCatchNode'],
        InputNode: {
          pre: 'scanf(',
          post: ');\n'
        },
        OutputNode: {
          pre: 'printf(',
          post: ');\n'
        },
        TaskNode: {
          pre: '',
          post: ';\n'
        },
        BranchNode: {
          pre: 'if (',
          post: ')\n',
          between: '} else {\n'
        },
        TryCatchNode: {
          pre: '',
          between: '',
          post: ''
        },
        CountLoopNode: {
          pre: 'for (',
          post: ')\n'
        },
        HeadLoopNode: {
          pre: 'while (',
          post: ')\n'
        },
        FootLoopNode: {
          prepre: 'do\n',
          pre: 'while (',
          post: ');\n'
        },
        FunctionNode: {
          pre: 'void ',
          between: '(',
          post: ')\n'
        },
        CaseNode: {
          pre: 'switch (',
          post: ')\n'
        },
        InsertCase: {
          preNormal: 'case ',
          preDefault: 'default',
          post: ':\n',
          postpost: 'break;\n'
        },
        leftBracket: '{',
        rightBracket: '}',
        pseudoSwitch: false
      }
    };
    this.preRender();
  }
  preRender() {
    const sourcecode = document.getElementById('SourcecodeDisplay');
    const sourcecodeDisplay = document.createElement('div');
    sourcecodeDisplay.classList.add('fixFullWidth', 'margin-top-small');
    const sourcecodeHeader = document.createElement('div');
    sourcecodeHeader.classList.add('columnAuto', 'container');
    const sourcecodeTitle = document.createElement('strong');
    sourcecodeTitle.classList.add('center');
    sourcecodeTitle.appendChild(document.createTextNode('Übersetzen in:'));
    const sourcecodeForm = document.createElement('div');
    sourcecodeForm.classList.add('center');
    const sourcecodeSelect = document.createElement('select');
    sourcecodeSelect.classList.add('form-select');
    sourcecodeSelect.id = 'SourcecodeSelect';
    sourcecodeSelect.addEventListener('change', event => this.presenter.startTransforming(event));
    const sourcecodeOption = document.createElement('option');
    sourcecodeOption.value = '--';
    sourcecodeOption.appendChild(document.createTextNode('--'));
    sourcecodeSelect.appendChild(sourcecodeOption);
    for (const lang in this.translationMap) {
      const langDiv = document.createElement('option');
      langDiv.value = lang;
      langDiv.appendChild(document.createTextNode(lang));
      if (lang === 'Python ab v3.10') {
        langDiv.selected = true;
        this.lang = lang; // Set the current language
      }
      sourcecodeSelect.appendChild(langDiv);
    }
    const sourcecodeCopy = document.createElement('div');
    sourcecodeCopy.setAttribute('data-tooltip', 'Kopiere Code');
    sourcecodeCopy.classList.add('center', 'copyIcon', 'struktoOption', 'sourcecodeHeader', 'hand', 'tooltip');
    sourcecodeCopy.addEventListener('click', event => {
      navigator.clipboard.writeText(this.generatedCode);
    });
    sourcecodeForm.appendChild(sourcecodeSelect);
    sourcecodeHeader.appendChild(sourcecodeTitle);
    sourcecodeHeader.appendChild(sourcecodeForm);
    sourcecodeHeader.appendChild(sourcecodeCopy);
    const sourcecodeWorkingArea = document.createElement('div');
    sourcecodeWorkingArea.classList.add('columnAuto');
    sourcecodeWorkingArea.id = 'Sourcecode';
    sourcecodeDisplay.appendChild(sourcecodeHeader);
    sourcecodeDisplay.appendChild(sourcecodeWorkingArea);
    sourcecode.appendChild(sourcecodeDisplay);
    this.domRoot = document.getElementById('Sourcecode');

    // Always generate code switch button
    this.generateCodeSwitch(this.presenter, document.getElementById('struktoOptions1'));

    // Update button visibility based on config
    this.updateCodeButtonVisibility();

    // Set sourcecode to be hidden by default
    this.presenter.setSourcecodeDisplay(false);

    // if (typeof (Storage) !== 'undefined') {
    //   if ('displaySourcecode' in localStorage && 'lang' in localStorage) {
    //     const possibleLang = localStorage.lang
    //     if (possibleLang in this.translationMap) {
    //       this.lang = possibleLang
    //       sourcecodeSelect.value = this.lang
    //       this.presenter.setSourcecodeDisplay(JSON.parse(localStorage.displaySourcecode))
    //       this.displaySourcecode('ToggleSourcecode')
    //     }
    //   }
    // }

    // Ensure sourcecode is hidden initially
    this.displaySourcecode('ToggleSourcecode');
  }
  render(model) {
    // remove content
    while (this.domRoot.hasChildNodes()) {
      this.domRoot.removeChild(this.domRoot.lastChild);
    }

    // only translate, if some language is selected
    if (this.lang !== '--') {
      // check if translation is possible with current tree
      let isTranslatable = false;
      for (const nodeType of this.translationMap[this.lang].untranslatable) {
        isTranslatable = isTranslatable || this.checkForUntranslatable(model, nodeType);
      }

      // create container for the spans
      const preBlock = document.createElement('pre');
      preBlock.classList.add('code');
      // set the language attribute
      preBlock.setAttribute('data-lang', this.lang);
      const codeBlock = document.createElement('code');

      // start appending the translated elements
      let codeText = '';
      if (!isTranslatable) {
        const content = this.transformToCode(model, 0, this.lang);
        content.forEach(function (i) {
          codeBlock.appendChild(i);
          codeText = codeText + i.textContent;
        });
      } else {
        codeBlock.appendChild(document.createTextNode('Das Struktogramm enthält Elemente, \nwelche in der gewählten Programmiersprache \nnicht direkt zur Verfügung stehen.\nDeshalb bitte manuell in Code überführen.'));
      }
      this.generatedCode = codeText; // Store locally for copy function

      preBlock.appendChild(codeBlock);
      this.domRoot.appendChild(preBlock);
    }
  }
  generateCodeSwitch(presenter, domNode) {
    const option = document.createElement('div');
    option.classList.add('struktoOption', 'codeIcon', 'tooltip', 'tooltip-bottomCode', 'hand', 'ToggleSourcecode');
    option.setAttribute('data-tooltip', 'Quellcode einblenden');
    option.addEventListener('click', event => presenter.alterSourcecodeDisplay(event));
    domNode.appendChild(option);
  }
  setLang(lang) {
    // if (typeof (Storage) !== 'undefined') {
    //   localStorage.lang = lang
    //   localStorage.displaySourcecode = true
    // }
    this.lang = lang;
  }
  resetButtons() {}

  /**
     * Add indentations to a text element
     *
     * @param    indentLevel   number of how many levels deep the node is
     * @return   string        multiple indentations, times the level
     */
  addIndentations(indentLevel) {
    let text = '';
    const defaultIndent = '    ';
    for (let i = 0; i < indentLevel; i++) {
      text = text + defaultIndent;
    }
    return text;
  }

  /**
     * Create a span with text and a highlight class
     *
     * @param    text   text to be displayed
     * @return   span   complete HTML structure with text and class
     */
  createHighlightedSpan(text) {
    const span = document.createElement('span');
    span.classList.add('text-code');
    span.appendChild(document.createTextNode(text));
    return span;
  }

  /**
     * Check recursively elements if they match a given type
     *
     * @param    subTree    part of the tree with all children of current element
     * @param    nodeType   type of the translation map element to be checked against
     * @return   boolean    true, if the current element type is the given type
     */
  checkForUntranslatable(subTree, nodeType) {
    // end recursion
    if (subTree.type === 'Placeholder' || subTree.type === 'InsertNode' && subTree.followElement === null) {
      return false;
    } else {
      // compare the types1
      if (subTree.type === nodeType) {
        return true;
      } else {
        // different recursive steps, depending on child structure
        switch (subTree.type) {
          case 'InsertNode':
          case 'InputNode':
          case 'OutputNode':
          case 'TaskNode':
            return  false || this.checkForUntranslatable(subTree.followElement, nodeType);
          case 'BranchNode':
            return  false || this.checkForUntranslatable(subTree.trueChild, nodeType) || this.checkForUntranslatable(subTree.falseChild, nodeType) || this.checkForUntranslatable(subTree.followElement, nodeType);
          case 'TryCatchNode':
            return  false || this.checkForUntranslatable(subTree.tryChild, nodeType) || this.checkForUntranslatable(subTree.catchChild, nodeType) || this.checkForUntranslatable(subTree.followElement, nodeType);
          case 'CountLoopNode':
          case 'HeadLoopNode':
          case 'FootLoopNode':
          case 'FunctionNode':
            return  false || this.checkForUntranslatable(subTree.child, nodeType) || this.checkForUntranslatable(subTree.followElement, nodeType);
          case 'CaseNode':
            {
              let state = false;
              for (let i = 0; i < subTree.length; i++) {
                state = state || this.checkForUntranslatable(subTree.cases[i], nodeType);
              }
              state = state || this.checkForUntranslatable(subTree.defaultNode, nodeType);
              return state || this.checkForUntranslatable(subTree.followElement, nodeType);
            }
        }
      }
    }
  }

  /**
     * Tranform an element to sourcecode with a translation mapping
     *
     * @param    subTree       part of the tree with all children of current element
     * @param    indentLevel   number of indentation levels
     * @param    lang          current sourcecode language
     * @return   []            array of span elements with the tranformed element
     */
  transformToCode(subTree, indentLevel, lang, switchVar = false) {
    // end recursion
    if (subTree.type === 'Placeholder' || subTree.type === 'InsertNode' && subTree.followElement === null) {
      return [];
    } else {
      // create the span
      const elemSpan = document.createElement('span');
      elemSpan.id = subTree.id + '-codeLine';
      // add eventlisteners for mouseover and click events
      // highlight equivalent element in struktogramm on mouseover
      elemSpan.addEventListener('mouseover', function () {
        const node = document.getElementById(subTree.id);
        node.firstChild.classList.add('highlight');
      });
      elemSpan.addEventListener('mouseout', function () {
        const node = document.getElementById(subTree.id);
        node.firstChild.classList.remove('highlight');
      });
      // switch to edit mode of equivalent element in the struktogramm
      const text = this.createHighlightedSpan(subTree.text);
      text.classList.add('hand');
      text.addEventListener('click', () => this.presenter.switchEditState(subTree.id));
      switch (subTree.type) {
        case 'InsertNode':
          return this.transformToCode(subTree.followElement, indentLevel, lang);
        case 'InputNode':
          {
            const inputPre = document.createElement('span');
            inputPre.classList.add('keyword');
            inputPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].InputNode.pre));
            elemSpan.appendChild(inputPre);
            elemSpan.appendChild(text);
            const inputPost = document.createElement('span');
            inputPost.classList.add('keyword');
            inputPost.appendChild(document.createTextNode(this.translationMap[lang].InputNode.post));
            elemSpan.appendChild(inputPost);
            return [elemSpan].concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'OutputNode':
          {
            const outputPre = document.createElement('span');
            outputPre.classList.add('keyword');
            outputPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].OutputNode.pre));
            elemSpan.appendChild(outputPre);
            elemSpan.appendChild(text);
            const outputPost = document.createElement('span');
            outputPost.classList.add('keyword');
            outputPost.appendChild(document.createTextNode(this.translationMap[lang].OutputNode.post));
            elemSpan.appendChild(outputPost);
            return [elemSpan].concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'TaskNode':
          {
            const taskPre = document.createElement('span');
            taskPre.classList.add('keyword');
            taskPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].TaskNode.pre));
            elemSpan.appendChild(taskPre);
            elemSpan.appendChild(text);
            const taskPost = document.createElement('span');
            taskPost.classList.add('keyword');
            taskPost.appendChild(document.createTextNode(this.translationMap[lang].TaskNode.post));
            elemSpan.appendChild(taskPost);
            return [elemSpan].concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'BranchNode':
          {
            const branchHeaderPre = document.createElement('span');
            branchHeaderPre.classList.add('keyword');
            branchHeaderPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].BranchNode.pre));
            elemSpan.appendChild(branchHeaderPre);
            elemSpan.appendChild(text);
            const branchHeaderPost = document.createElement('span');
            branchHeaderPost.classList.add('keyword');
            branchHeaderPost.appendChild(document.createTextNode(this.translationMap[lang].BranchNode.post));
            elemSpan.appendChild(branchHeaderPost);
            let branch = [elemSpan];
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              branch.push(leftBracket);
            }
            const trueContent = this.transformToCode(subTree.trueChild, indentLevel + 1, lang);
            const falseContent = this.transformToCode(subTree.falseChild, indentLevel + 1, lang);
            branch = branch.concat(trueContent);
            if (falseContent.length > 0) {
              const between = document.createElement('span');
              between.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].BranchNode.between));
              branch.push(between);
            }
            branch = branch.concat(falseContent);
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              branch.push(rightBracket);
            }
            return branch.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'TryCatchNode':
          {
            const tryHeaderPre = newElement('span', ['keyword'], elemSpan);
            tryHeaderPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].TryCatchNode.pre));
            let trycatch = [elemSpan];
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              trycatch.push(leftBracket);
            }
            const tryContent = this.transformToCode(subTree.tryChild, indentLevel + 1, lang); // try Content
            trycatch = trycatch.concat(tryContent);
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              trycatch.push(rightBracket);
            }
            const catchBetween = newElement('span', ['keyword']);
            catchBetween.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].TryCatchNode.between));
            // only insert space if a parameter is set for the catch block
            if (text.innerText !== '' && lang.includes('Python')) {
              catchBetween.appendChild(document.createTextNode(' '));
            }
            catchBetween.appendChild(text);
            trycatch.push(catchBetween);
            const catchBetweenPost = newElement('span', ['keyword']);
            catchBetweenPost.appendChild(document.createTextNode(this.translationMap[lang].TryCatchNode.post));
            trycatch.push(catchBetweenPost);
            const catchContent = this.transformToCode(subTree.catchChild, indentLevel + 1, lang); // catch content
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              trycatch.push(leftBracket);
            }
            trycatch = trycatch.concat(catchContent);
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              trycatch.push(rightBracket);
            }
            return trycatch.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'CountLoopNode':
          {
            const loopHeaderPre = document.createElement('span');
            loopHeaderPre.classList.add('keyword');
            loopHeaderPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].CountLoopNode.pre));
            elemSpan.appendChild(loopHeaderPre);
            elemSpan.appendChild(text);
            const loopHeaderPost = document.createElement('span');
            loopHeaderPost.classList.add('keyword');
            loopHeaderPost.appendChild(document.createTextNode(this.translationMap[lang].CountLoopNode.post));
            elemSpan.appendChild(loopHeaderPost);
            let loop = [elemSpan];
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              loop.push(leftBracket);
            }
            loop = loop.concat(this.transformToCode(subTree.child, indentLevel + 1, lang));
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              loop.push(rightBracket);
            }
            return loop.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'HeadLoopNode':
          {
            const loopHeaderPre = document.createElement('span');
            loopHeaderPre.classList.add('keyword');
            loopHeaderPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].HeadLoopNode.pre));
            elemSpan.appendChild(loopHeaderPre);
            elemSpan.appendChild(text);
            const loopHeaderPost = document.createElement('span');
            loopHeaderPost.classList.add('keyword');
            loopHeaderPost.appendChild(document.createTextNode(this.translationMap[lang].HeadLoopNode.post));
            elemSpan.appendChild(loopHeaderPost);
            let loop = [elemSpan];
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              loop.push(leftBracket);
            }
            loop = loop.concat(this.transformToCode(subTree.child, indentLevel + 1, lang));
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              loop.push(rightBracket);
            }
            return loop.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'FootLoopNode':
          {
            const loopContent = document.createElement('span');
            loopContent.classList.add('keyword');
            loopContent.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].FootLoopNode.prepre));
            elemSpan.appendChild(loopContent);
            let loop = [elemSpan];
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              loop.push(leftBracket);
            }
            const child = this.transformToCode(subTree.child, indentLevel + 1, lang);
            loop = loop.concat(child);
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              loop.push(rightBracket);
            }
            const subContent = document.createElement('span');
            subContent.id = subTree.id + '-codeLine';
            elemSpan.id = '';
            const subContentPre = document.createElement('span');
            subContentPre.classList.add('keyword');
            subContentPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].FootLoopNode.pre));
            subContent.appendChild(subContentPre);
            subContent.appendChild(text);
            const subContentPost = document.createElement('span');
            subContentPost.classList.add('keyword');
            subContentPost.appendChild(document.createTextNode(this.translationMap[lang].FootLoopNode.post));
            subContent.appendChild(subContentPost);
            subContent.addEventListener('mouseover', function () {
              const node = document.getElementById(subTree.id);
              node.firstChild.classList.add('highlight');
            });
            subContent.addEventListener('mouseout', function () {
              const node = document.getElementById(subTree.id);
              node.firstChild.classList.remove('highlight');
            });
            loop.push(subContent);
            return loop.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'FunctionNode':
          {
            const functionContent = document.createElement('span');
            functionContent.classList.add('keyword');
            functionContent.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].FunctionNode.pre));
            elemSpan.appendChild(functionContent);
            elemSpan.appendChild(text);
            const funcHeaderBetween = document.createElement('span');
            funcHeaderBetween.classList.add('keyword');
            funcHeaderBetween.appendChild(document.createTextNode(this.translationMap[lang].FunctionNode.between));
            elemSpan.appendChild(funcHeaderBetween);

            // add parameters
            const params = subTree.parameters;
            let parCount = 0;
            for (const par of params) {
              if (parCount !== 0) {
                elemSpan.appendChild(this.createHighlightedSpan(', '));
              }
              const paramName = this.createHighlightedSpan(par.parName);
              paramName.classList.add('hand');
              // mapping the stored positions (0, 3, 6, ...) to new positions (0, 2, 4, ...)
              paramName.addEventListener('click', () => this.presenter.switchEditState(subTree.id, par.pos / 3 * 2));
              elemSpan.appendChild(paramName);
              parCount += 1;
            }
            const funcHeaderPost = document.createElement('span');
            funcHeaderPost.classList.add('keyword');
            funcHeaderPost.appendChild(document.createTextNode(this.translationMap[lang].FunctionNode.post));
            elemSpan.appendChild(funcHeaderPost);
            let loop = [elemSpan];
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              loop.push(leftBracket);
            }
            loop = loop.concat(this.transformToCode(subTree.child, indentLevel + 1, lang));
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              loop.push(rightBracket);
            }
            return loop.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'CaseNode':
          {
            if (!this.translationMap[lang].pseudoSwitch) {
              const caseHeadPre = document.createElement('span');
              caseHeadPre.classList.add('keyword');
              caseHeadPre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].CaseNode.pre));
              elemSpan.appendChild(caseHeadPre);
              elemSpan.appendChild(text);
              const caseHeadPost = document.createElement('span');
              caseHeadPost.classList.add('keyword');
              caseHeadPost.appendChild(document.createTextNode(this.translationMap[lang].CaseNode.post));
              elemSpan.appendChild(caseHeadPost);
            }
            let cases = [elemSpan];
            if (this.translationMap[lang].pseudoSwitch) {
              cases = [];
            }
            if (this.translationMap[lang].leftBracket !== '') {
              const leftBracket = document.createElement('span');
              leftBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].leftBracket + '\n'));
              cases.push(leftBracket);
            }
            for (const element of subTree.cases) {
              if (this.translationMap[lang].pseudoSwitch) {
                const switchVarSpan = this.createHighlightedSpan(subTree.text);
                switchVarSpan.classList.add('hand');
                switchVarSpan.addEventListener('click', () => this.presenter.switchEditState(subTree.id));
                cases = cases.concat(this.transformToCode(element, indentLevel, lang, switchVarSpan));
              } else {
                cases = cases.concat(this.transformToCode(element, indentLevel + 1, lang));
              }
            }
            if (this.translationMap[lang].pseudoSwitch) {
              cases[0].firstChild.innerText = 'if ';
            }
            if (subTree.defaultOn) {
              const defaultCase = document.createElement('span');
              defaultCase.classList.add('keyword');
              defaultCase.id = subTree.defaultNode.id + '-codeLine';
              if (this.translationMap[lang].pseudoSwitch) {
                defaultCase.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].InsertCase.preDefault + this.translationMap[lang].InsertCase.post));
              } else {
                defaultCase.appendChild(document.createTextNode(this.addIndentations(indentLevel + 1) + this.translationMap[lang].InsertCase.preDefault + this.translationMap[lang].InsertCase.post));
              }
              defaultCase.addEventListener('mouseover', function () {
                const node = document.getElementById(subTree.defaultNode.id);
                node.firstChild.classList.add('highlight');
              });
              defaultCase.addEventListener('mouseout', function () {
                const node = document.getElementById(subTree.defaultNode.id);
                node.firstChild.classList.remove('highlight');
              });
              cases.push(defaultCase);
              if (this.translationMap[lang].pseudoSwitch) {
                cases = cases.concat(this.transformToCode(subTree.defaultNode.followElement, indentLevel + 1, lang));
              } else {
                cases = cases.concat(this.transformToCode(subTree.defaultNode.followElement, indentLevel + 2, lang));
              }
              if (!this.translationMap[lang].pseudoSwitch && (lang === 'C#' || lang === 'Java')) {
                const endContent = document.createElement('span');
                endContent.classList.add('keyword');
                endContent.appendChild(document.createTextNode(this.addIndentations(indentLevel + 2) + this.translationMap[lang].InsertCase.postpost));
                cases.push(endContent);
              }
            }
            if (this.translationMap[lang].rightBracket !== '') {
              const rightBracket = document.createElement('span');
              rightBracket.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].rightBracket + '\n'));
              cases.push(rightBracket);
            }
            return cases.concat(this.transformToCode(subTree.followElement, indentLevel, lang));
          }
        case 'InsertCase':
          {
            const casePre = document.createElement('span');
            casePre.classList.add('keyword');
            casePre.appendChild(document.createTextNode(this.addIndentations(indentLevel) + this.translationMap[lang].InsertCase.preNormal));
            elemSpan.appendChild(casePre);
            if (switchVar) {
              elemSpan.appendChild(switchVar);
              const equals = document.createElement('span');
              equals.appendChild(document.createTextNode(' == '));
              elemSpan.appendChild(equals);
            }
            elemSpan.appendChild(text);
            const casePost = document.createElement('span');
            casePost.classList.add('keyword');
            casePost.appendChild(document.createTextNode(this.translationMap[lang].InsertCase.post));
            elemSpan.appendChild(casePost);
            let content = [elemSpan];
            content = content.concat(this.transformToCode(subTree.followElement, indentLevel + 1, lang));
            if (!this.translationMap[lang].pseudoSwitch) {
              const endContent = document.createElement('span');
              endContent.classList.add('keyword');
              endContent.appendChild(document.createTextNode(this.addIndentations(indentLevel + 1) + this.translationMap[lang].InsertCase.postpost));
              content.push(endContent);
            }
            return content;
          }
      }
    }
  }

  /**
     * Get the currently selected code language
     */
  prepareTransforming() {
    const lang = document.getElementById('SourcecodeSelect').value;
    // start the transformation
    presenter.startTransforming(lang);
  }
  updateCodeButtonVisibility() {
    const config = this.presenter.getConfig();
    const showCodeButton = config && config.showCodeButton !== false;

    // Hide/show the code button based on configuration
    const codeButtons = document.getElementsByClassName('ToggleSourcecode');
    for (const button of codeButtons) {
      button.style.display = showCodeButton ? 'block' : 'none';
    }

    // Hide/show the sourcecode display based on configuration
    const sourcecodeDisplay = document.getElementById('SourcecodeDisplay');
    if (!showCodeButton) {
      sourcecodeDisplay.style.display = 'none';
    }
  }

  /**
     * Toggle the state of the sourcecode display button
     *
     * @param   buttonId   id of the sourcecode display toggle button
     */
  displaySourcecode(buttonClass) {
    const config = this.presenter.getConfig();
    const showCodeButton = config && config.showCodeButton !== false;

    // Hide/show the code button based on configuration
    const codeButtons = document.getElementsByClassName('ToggleSourcecode');
    for (const button of codeButtons) {
      button.style.display = showCodeButton ? 'block' : 'none';
    }

    // Hide/show the sourcecode display based on configuration
    const sourcecodeDisplay = document.getElementById('SourcecodeDisplay');
    if (!showCodeButton) {
      sourcecodeDisplay.style.display = 'none';
      return;
    }
    const fields = document.getElementsByClassName(buttonClass);
    if (this.presenter.getSourcecodeDisplay()) {
      for (const item of fields) {
        item.setAttribute('data-tooltip', 'Quellcode ausblenden');
      }
      sourcecodeDisplay.style.display = 'block';
      if (window.matchMedia('(max-width: 1200px)')) {
        document.getElementById('editorContent').style.flexBasis = '75%';
      }
    } else {
      for (const item of fields) {
        item.setAttribute('data-tooltip', 'Quellcode einblenden');
      }
      sourcecodeDisplay.style.display = 'none';
      if (window.matchMedia('(max-width: 1200px)')) {
        document.getElementById('editorContent').style.flexBasis = '100%';
      }
    }
  }
}
;// ./node_modules/html-to-image/es/util.js
function resolveUrl(url, baseUrl) {
  // url is absolute already
  if (url.match(/^[a-z]+:\/\//i)) {
    return url;
  }
  // url is absolute already, without protocol
  if (url.match(/^\/\//)) {
    return window.location.protocol + url;
  }
  // dataURI, mailto:, tel:, etc.
  if (url.match(/^[a-z]+:/i)) {
    return url;
  }
  const doc = document.implementation.createHTMLDocument();
  const base = doc.createElement('base');
  const a = doc.createElement('a');
  doc.head.appendChild(base);
  doc.body.appendChild(a);
  if (baseUrl) {
    base.href = baseUrl;
  }
  a.href = url;
  return a.href;
}
const uuid = (() => {
  // generate uuid for className of pseudo elements.
  // We should not use GUIDs, otherwise pseudo elements sometimes cannot be captured.
  let counter = 0;
  // ref: http://stackoverflow.com/a/6248722/2519373
  const random = () =>
  // eslint-disable-next-line no-bitwise
  `0000${(Math.random() * 36 ** 4 << 0).toString(36)}`.slice(-4);
  return () => {
    counter += 1;
    return `u${random()}${counter}`;
  };
})();
function delay(ms) {
  return args => new Promise(resolve => {
    setTimeout(() => resolve(args), ms);
  });
}
function toArray(arrayLike) {
  const arr = [];
  for (let i = 0, l = arrayLike.length; i < l; i++) {
    arr.push(arrayLike[i]);
  }
  return arr;
}
let styleProps = null;
function getStyleProperties(options = {}) {
  if (styleProps) {
    return styleProps;
  }
  if (options.includeStyleProperties) {
    styleProps = options.includeStyleProperties;
    return styleProps;
  }
  styleProps = toArray(window.getComputedStyle(document.documentElement));
  return styleProps;
}
function px(node, styleProperty) {
  const win = node.ownerDocument.defaultView || window;
  const val = win.getComputedStyle(node).getPropertyValue(styleProperty);
  return val ? parseFloat(val.replace('px', '')) : 0;
}
function getNodeWidth(node) {
  const leftBorder = px(node, 'border-left-width');
  const rightBorder = px(node, 'border-right-width');
  return node.clientWidth + leftBorder + rightBorder;
}
function getNodeHeight(node) {
  const topBorder = px(node, 'border-top-width');
  const bottomBorder = px(node, 'border-bottom-width');
  return node.clientHeight + topBorder + bottomBorder;
}
function util_getImageSize(targetNode, options = {}) {
  const width = options.width || getNodeWidth(targetNode);
  const height = options.height || getNodeHeight(targetNode);
  return {
    width,
    height
  };
}
function getPixelRatio() {
  let ratio;
  let FINAL_PROCESS;
  try {
    FINAL_PROCESS = process;
  } catch (e) {
    // pass
  }
  const val = FINAL_PROCESS && FINAL_PROCESS.env ? FINAL_PROCESS.env.devicePixelRatio : null;
  if (val) {
    ratio = parseInt(val, 10);
    if (Number.isNaN(ratio)) {
      ratio = 1;
    }
  }
  return ratio || window.devicePixelRatio || 1;
}
// @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/canvas#maximum_canvas_size
const canvasDimensionLimit = 16384;
function checkCanvasDimensions(canvas) {
  if (canvas.width > canvasDimensionLimit || canvas.height > canvasDimensionLimit) {
    if (canvas.width > canvasDimensionLimit && canvas.height > canvasDimensionLimit) {
      if (canvas.width > canvas.height) {
        canvas.height *= canvasDimensionLimit / canvas.width;
        canvas.width = canvasDimensionLimit;
      } else {
        canvas.width *= canvasDimensionLimit / canvas.height;
        canvas.height = canvasDimensionLimit;
      }
    } else if (canvas.width > canvasDimensionLimit) {
      canvas.height *= canvasDimensionLimit / canvas.width;
      canvas.width = canvasDimensionLimit;
    } else {
      canvas.width *= canvasDimensionLimit / canvas.height;
      canvas.height = canvasDimensionLimit;
    }
  }
}
function util_canvasToBlob(canvas, options = {}) {
  if (canvas.toBlob) {
    return new Promise(resolve => {
      canvas.toBlob(resolve, options.type ? options.type : 'image/png', options.quality ? options.quality : 1);
    });
  }
  return new Promise(resolve => {
    const binaryString = window.atob(canvas.toDataURL(options.type ? options.type : undefined, options.quality ? options.quality : undefined).split(',')[1]);
    const len = binaryString.length;
    const binaryArray = new Uint8Array(len);
    for (let i = 0; i < len; i += 1) {
      binaryArray[i] = binaryString.charCodeAt(i);
    }
    resolve(new Blob([binaryArray], {
      type: options.type ? options.type : 'image/png'
    }));
  });
}
function createImage(url) {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => {
      img.decode().then(() => {
        requestAnimationFrame(() => resolve(img));
      });
    };
    img.onerror = reject;
    img.crossOrigin = 'anonymous';
    img.decoding = 'async';
    img.src = url;
  });
}
async function svgToDataURL(svg) {
  return Promise.resolve().then(() => new XMLSerializer().serializeToString(svg)).then(encodeURIComponent).then(html => `data:image/svg+xml;charset=utf-8,${html}`);
}
async function nodeToDataURL(node, width, height) {
  const xmlns = 'http://www.w3.org/2000/svg';
  const svg = document.createElementNS(xmlns, 'svg');
  const foreignObject = document.createElementNS(xmlns, 'foreignObject');
  svg.setAttribute('width', `${width}`);
  svg.setAttribute('height', `${height}`);
  svg.setAttribute('viewBox', `0 0 ${width} ${height}`);
  foreignObject.setAttribute('width', '100%');
  foreignObject.setAttribute('height', '100%');
  foreignObject.setAttribute('x', '0');
  foreignObject.setAttribute('y', '0');
  foreignObject.setAttribute('externalResourcesRequired', 'true');
  svg.appendChild(foreignObject);
  foreignObject.appendChild(node);
  return svgToDataURL(svg);
}
const isInstanceOfElement = (node, instance) => {
  if (node instanceof instance) return true;
  const nodePrototype = Object.getPrototypeOf(node);
  if (nodePrototype === null) return false;
  return nodePrototype.constructor.name === instance.name || isInstanceOfElement(nodePrototype, instance);
};
;// ./node_modules/html-to-image/es/clone-pseudos.js

function formatCSSText(style) {
  const content = style.getPropertyValue('content');
  return `${style.cssText} content: '${content.replace(/'|"/g, '')}';`;
}
function formatCSSProperties(style, options) {
  return getStyleProperties(options).map(name => {
    const value = style.getPropertyValue(name);
    const priority = style.getPropertyPriority(name);
    return `${name}: ${value}${priority ? ' !important' : ''};`;
  }).join(' ');
}
function getPseudoElementStyle(className, pseudo, style, options) {
  const selector = `.${className}:${pseudo}`;
  const cssText = style.cssText ? formatCSSText(style) : formatCSSProperties(style, options);
  return document.createTextNode(`${selector}{${cssText}}`);
}
function clonePseudoElement(nativeNode, clonedNode, pseudo, options) {
  const style = window.getComputedStyle(nativeNode, pseudo);
  const content = style.getPropertyValue('content');
  if (content === '' || content === 'none') {
    return;
  }
  const className = uuid();
  try {
    clonedNode.className = `${clonedNode.className} ${className}`;
  } catch (err) {
    return;
  }
  const styleElement = document.createElement('style');
  styleElement.appendChild(getPseudoElementStyle(className, pseudo, style, options));
  clonedNode.appendChild(styleElement);
}
function clonePseudoElements(nativeNode, clonedNode, options) {
  clonePseudoElement(nativeNode, clonedNode, ':before', options);
  clonePseudoElement(nativeNode, clonedNode, ':after', options);
}
;// ./node_modules/html-to-image/es/mimes.js
const WOFF = 'application/font-woff';
const JPEG = 'image/jpeg';
const mimes = {
  woff: WOFF,
  woff2: WOFF,
  ttf: 'application/font-truetype',
  eot: 'application/vnd.ms-fontobject',
  png: 'image/png',
  jpg: JPEG,
  jpeg: JPEG,
  gif: 'image/gif',
  tiff: 'image/tiff',
  svg: 'image/svg+xml',
  webp: 'image/webp'
};
function getExtension(url) {
  const match = /\.([^./]*?)$/g.exec(url);
  return match ? match[1] : '';
}
function getMimeType(url) {
  const extension = getExtension(url).toLowerCase();
  return mimes[extension] || '';
}
;// ./node_modules/html-to-image/es/dataurl.js
function getContentFromDataUrl(dataURL) {
  return dataURL.split(/,/)[1];
}
function isDataUrl(url) {
  return url.search(/^(data:)/) !== -1;
}
function makeDataUrl(content, mimeType) {
  return `data:${mimeType};base64,${content}`;
}
async function fetchAsDataURL(url, init, process) {
  const res = await fetch(url, init);
  if (res.status === 404) {
    throw new Error(`Resource "${res.url}" not found`);
  }
  const blob = await res.blob();
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = reject;
    reader.onloadend = () => {
      try {
        resolve(process({
          res,
          result: reader.result
        }));
      } catch (error) {
        reject(error);
      }
    };
    reader.readAsDataURL(blob);
  });
}
const cache = {};
function getCacheKey(url, contentType, includeQueryParams) {
  let key = url.replace(/\?.*/, '');
  if (includeQueryParams) {
    key = url;
  }
  // font resource
  if (/ttf|otf|eot|woff2?/i.test(key)) {
    key = key.replace(/.*\//, '');
  }
  return contentType ? `[${contentType}]${key}` : key;
}
async function resourceToDataURL(resourceUrl, contentType, options) {
  const cacheKey = getCacheKey(resourceUrl, contentType, options.includeQueryParams);
  if (cache[cacheKey] != null) {
    return cache[cacheKey];
  }
  // ref: https://developer.mozilla.org/en/docs/Web/API/XMLHttpRequest/Using_XMLHttpRequest#Bypassing_the_cache
  if (options.cacheBust) {
    // eslint-disable-next-line no-param-reassign
    resourceUrl += (/\?/.test(resourceUrl) ? '&' : '?') + new Date().getTime();
  }
  let dataURL;
  try {
    const content = await fetchAsDataURL(resourceUrl, options.fetchRequestInit, ({
      res,
      result
    }) => {
      if (!contentType) {
        // eslint-disable-next-line no-param-reassign
        contentType = res.headers.get('Content-Type') || '';
      }
      return getContentFromDataUrl(result);
    });
    dataURL = makeDataUrl(content, contentType);
  } catch (error) {
    dataURL = options.imagePlaceholder || '';
    let msg = `Failed to fetch resource: ${resourceUrl}`;
    if (error) {
      msg = typeof error === 'string' ? error : error.message;
    }
    if (msg) {
      console.warn(msg);
    }
  }
  cache[cacheKey] = dataURL;
  return dataURL;
}
;// ./node_modules/html-to-image/es/clone-node.js




async function cloneCanvasElement(canvas) {
  const dataURL = canvas.toDataURL();
  if (dataURL === 'data:,') {
    return canvas.cloneNode(false);
  }
  return createImage(dataURL);
}
async function cloneVideoElement(video, options) {
  if (video.currentSrc) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    canvas.width = video.clientWidth;
    canvas.height = video.clientHeight;
    ctx === null || ctx === void 0 ? void 0 : ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    const dataURL = canvas.toDataURL();
    return createImage(dataURL);
  }
  const poster = video.poster;
  const contentType = getMimeType(poster);
  const dataURL = await resourceToDataURL(poster, contentType, options);
  return createImage(dataURL);
}
async function cloneIFrameElement(iframe, options) {
  var _a;
  try {
    if ((_a = iframe === null || iframe === void 0 ? void 0 : iframe.contentDocument) === null || _a === void 0 ? void 0 : _a.body) {
      return await cloneNode(iframe.contentDocument.body, options, true);
    }
  } catch (_b) {
    // Failed to clone iframe
  }
  return iframe.cloneNode(false);
}
async function cloneSingleNode(node, options) {
  if (isInstanceOfElement(node, HTMLCanvasElement)) {
    return cloneCanvasElement(node);
  }
  if (isInstanceOfElement(node, HTMLVideoElement)) {
    return cloneVideoElement(node, options);
  }
  if (isInstanceOfElement(node, HTMLIFrameElement)) {
    return cloneIFrameElement(node, options);
  }
  return node.cloneNode(isSVGElement(node));
}
const isSlotElement = node => node.tagName != null && node.tagName.toUpperCase() === 'SLOT';
const isSVGElement = node => node.tagName != null && node.tagName.toUpperCase() === 'SVG';
async function cloneChildren(nativeNode, clonedNode, options) {
  var _a, _b;
  if (isSVGElement(clonedNode)) {
    return clonedNode;
  }
  let children = [];
  if (isSlotElement(nativeNode) && nativeNode.assignedNodes) {
    children = toArray(nativeNode.assignedNodes());
  } else if (isInstanceOfElement(nativeNode, HTMLIFrameElement) && ((_a = nativeNode.contentDocument) === null || _a === void 0 ? void 0 : _a.body)) {
    children = toArray(nativeNode.contentDocument.body.childNodes);
  } else {
    children = toArray(((_b = nativeNode.shadowRoot) !== null && _b !== void 0 ? _b : nativeNode).childNodes);
  }
  if (children.length === 0 || isInstanceOfElement(nativeNode, HTMLVideoElement)) {
    return clonedNode;
  }
  await children.reduce((deferred, child) => deferred.then(() => cloneNode(child, options)).then(clonedChild => {
    if (clonedChild) {
      clonedNode.appendChild(clonedChild);
    }
  }), Promise.resolve());
  return clonedNode;
}
function cloneCSSStyle(nativeNode, clonedNode, options) {
  const targetStyle = clonedNode.style;
  if (!targetStyle) {
    return;
  }
  const sourceStyle = window.getComputedStyle(nativeNode);
  if (sourceStyle.cssText) {
    targetStyle.cssText = sourceStyle.cssText;
    targetStyle.transformOrigin = sourceStyle.transformOrigin;
  } else {
    getStyleProperties(options).forEach(name => {
      let value = sourceStyle.getPropertyValue(name);
      if (name === 'font-size' && value.endsWith('px')) {
        const reducedFont = Math.floor(parseFloat(value.substring(0, value.length - 2))) - 0.1;
        value = `${reducedFont}px`;
      }
      if (isInstanceOfElement(nativeNode, HTMLIFrameElement) && name === 'display' && value === 'inline') {
        value = 'block';
      }
      if (name === 'd' && clonedNode.getAttribute('d')) {
        value = `path(${clonedNode.getAttribute('d')})`;
      }
      targetStyle.setProperty(name, value, sourceStyle.getPropertyPriority(name));
    });
  }
}
function cloneInputValue(nativeNode, clonedNode) {
  if (isInstanceOfElement(nativeNode, HTMLTextAreaElement)) {
    clonedNode.innerHTML = nativeNode.value;
  }
  if (isInstanceOfElement(nativeNode, HTMLInputElement)) {
    clonedNode.setAttribute('value', nativeNode.value);
  }
}
function cloneSelectValue(nativeNode, clonedNode) {
  if (isInstanceOfElement(nativeNode, HTMLSelectElement)) {
    const clonedSelect = clonedNode;
    const selectedOption = Array.from(clonedSelect.children).find(child => nativeNode.value === child.getAttribute('value'));
    if (selectedOption) {
      selectedOption.setAttribute('selected', '');
    }
  }
}
function decorate(nativeNode, clonedNode, options) {
  if (isInstanceOfElement(clonedNode, Element)) {
    cloneCSSStyle(nativeNode, clonedNode, options);
    clonePseudoElements(nativeNode, clonedNode, options);
    cloneInputValue(nativeNode, clonedNode);
    cloneSelectValue(nativeNode, clonedNode);
  }
  return clonedNode;
}
async function ensureSVGSymbols(clone, options) {
  const uses = clone.querySelectorAll ? clone.querySelectorAll('use') : [];
  if (uses.length === 0) {
    return clone;
  }
  const processedDefs = {};
  for (let i = 0; i < uses.length; i++) {
    const use = uses[i];
    const id = use.getAttribute('xlink:href');
    if (id) {
      const exist = clone.querySelector(id);
      const definition = document.querySelector(id);
      if (!exist && definition && !processedDefs[id]) {
        // eslint-disable-next-line no-await-in-loop
        processedDefs[id] = await cloneNode(definition, options, true);
      }
    }
  }
  const nodes = Object.values(processedDefs);
  if (nodes.length) {
    const ns = 'http://www.w3.org/1999/xhtml';
    const svg = document.createElementNS(ns, 'svg');
    svg.setAttribute('xmlns', ns);
    svg.style.position = 'absolute';
    svg.style.width = '0';
    svg.style.height = '0';
    svg.style.overflow = 'hidden';
    svg.style.display = 'none';
    const defs = document.createElementNS(ns, 'defs');
    svg.appendChild(defs);
    for (let i = 0; i < nodes.length; i++) {
      defs.appendChild(nodes[i]);
    }
    clone.appendChild(svg);
  }
  return clone;
}
async function cloneNode(node, options, isRoot) {
  if (!isRoot && options.filter && !options.filter(node)) {
    return null;
  }
  return Promise.resolve(node).then(clonedNode => cloneSingleNode(clonedNode, options)).then(clonedNode => cloneChildren(node, clonedNode, options)).then(clonedNode => decorate(node, clonedNode, options)).then(clonedNode => ensureSVGSymbols(clonedNode, options));
}
;// ./node_modules/html-to-image/es/embed-resources.js



const URL_REGEX = /url\((['"]?)([^'"]+?)\1\)/g;
const URL_WITH_FORMAT_REGEX = /url\([^)]+\)\s*format\((["']?)([^"']+)\1\)/g;
const FONT_SRC_REGEX = /src:\s*(?:url\([^)]+\)\s*format\([^)]+\)[,;]\s*)+/g;
function toRegex(url) {
  // eslint-disable-next-line no-useless-escape
  const escaped = url.replace(/([.*+?^${}()|\[\]\/\\])/g, '\\$1');
  return new RegExp(`(url\\(['"]?)(${escaped})(['"]?\\))`, 'g');
}
function parseURLs(cssText) {
  const urls = [];
  cssText.replace(URL_REGEX, (raw, quotation, url) => {
    urls.push(url);
    return raw;
  });
  return urls.filter(url => !isDataUrl(url));
}
async function embed_resources_embed(cssText, resourceURL, baseURL, options, getContentFromUrl) {
  try {
    const resolvedURL = baseURL ? resolveUrl(resourceURL, baseURL) : resourceURL;
    const contentType = getMimeType(resourceURL);
    let dataURL;
    if (getContentFromUrl) {
      const content = await getContentFromUrl(resolvedURL);
      dataURL = makeDataUrl(content, contentType);
    } else {
      dataURL = await resourceToDataURL(resolvedURL, contentType, options);
    }
    return cssText.replace(toRegex(resourceURL), `$1${dataURL}$3`);
  } catch (error) {
    // pass
  }
  return cssText;
}
function filterPreferredFontFormat(str, {
  preferredFontFormat
}) {
  return !preferredFontFormat ? str : str.replace(FONT_SRC_REGEX, match => {
    // eslint-disable-next-line no-constant-condition
    while (true) {
      const [src,, format] = URL_WITH_FORMAT_REGEX.exec(match) || [];
      if (!format) {
        return '';
      }
      if (format === preferredFontFormat) {
        return `src: ${src};`;
      }
    }
  });
}
function shouldEmbed(url) {
  return url.search(URL_REGEX) !== -1;
}
async function embedResources(cssText, baseUrl, options) {
  if (!shouldEmbed(cssText)) {
    return cssText;
  }
  const filteredCSSText = filterPreferredFontFormat(cssText, options);
  const urls = parseURLs(filteredCSSText);
  return urls.reduce((deferred, url) => deferred.then(css => embed_resources_embed(css, url, baseUrl, options)), Promise.resolve(filteredCSSText));
}
;// ./node_modules/html-to-image/es/embed-images.js




async function embedProp(propName, node, options) {
  var _a;
  const propValue = (_a = node.style) === null || _a === void 0 ? void 0 : _a.getPropertyValue(propName);
  if (propValue) {
    const cssString = await embedResources(propValue, null, options);
    node.style.setProperty(propName, cssString, node.style.getPropertyPriority(propName));
    return true;
  }
  return false;
}
async function embedBackground(clonedNode, options) {
  ;
  (await embedProp('background', clonedNode, options)) || (await embedProp('background-image', clonedNode, options));
  (await embedProp('mask', clonedNode, options)) || (await embedProp('-webkit-mask', clonedNode, options)) || (await embedProp('mask-image', clonedNode, options)) || (await embedProp('-webkit-mask-image', clonedNode, options));
}
async function embedImageNode(clonedNode, options) {
  const isImageElement = isInstanceOfElement(clonedNode, HTMLImageElement);
  if (!(isImageElement && !isDataUrl(clonedNode.src)) && !(isInstanceOfElement(clonedNode, SVGImageElement) && !isDataUrl(clonedNode.href.baseVal))) {
    return;
  }
  const url = isImageElement ? clonedNode.src : clonedNode.href.baseVal;
  const dataURL = await resourceToDataURL(url, getMimeType(url), options);
  await new Promise((resolve, reject) => {
    clonedNode.onload = resolve;
    clonedNode.onerror = options.onImageErrorHandler ? (...attributes) => {
      try {
        resolve(options.onImageErrorHandler(...attributes));
      } catch (error) {
        reject(error);
      }
    } : reject;
    const image = clonedNode;
    if (image.decode) {
      image.decode = resolve;
    }
    if (image.loading === 'lazy') {
      image.loading = 'eager';
    }
    if (isImageElement) {
      clonedNode.srcset = '';
      clonedNode.src = dataURL;
    } else {
      clonedNode.href.baseVal = dataURL;
    }
  });
}
async function embedChildren(clonedNode, options) {
  const children = toArray(clonedNode.childNodes);
  const deferreds = children.map(child => embedImages(child, options));
  await Promise.all(deferreds).then(() => clonedNode);
}
async function embedImages(clonedNode, options) {
  if (isInstanceOfElement(clonedNode, Element)) {
    await embedBackground(clonedNode, options);
    await embedImageNode(clonedNode, options);
    await embedChildren(clonedNode, options);
  }
}
;// ./node_modules/html-to-image/es/apply-style.js
function applyStyle(node, options) {
  const {
    style
  } = node;
  if (options.backgroundColor) {
    style.backgroundColor = options.backgroundColor;
  }
  if (options.width) {
    style.width = `${options.width}px`;
  }
  if (options.height) {
    style.height = `${options.height}px`;
  }
  const manual = options.style;
  if (manual != null) {
    Object.keys(manual).forEach(key => {
      style[key] = manual[key];
    });
  }
  return node;
}
;// ./node_modules/html-to-image/es/embed-webfonts.js



const cssFetchCache = {};
async function fetchCSS(url) {
  let cache = cssFetchCache[url];
  if (cache != null) {
    return cache;
  }
  const res = await fetch(url);
  const cssText = await res.text();
  cache = {
    url,
    cssText
  };
  cssFetchCache[url] = cache;
  return cache;
}
async function embedFonts(data, options) {
  let cssText = data.cssText;
  const regexUrl = /url\(["']?([^"')]+)["']?\)/g;
  const fontLocs = cssText.match(/url\([^)]+\)/g) || [];
  const loadFonts = fontLocs.map(async loc => {
    let url = loc.replace(regexUrl, '$1');
    if (!url.startsWith('https://')) {
      url = new URL(url, data.url).href;
    }
    return fetchAsDataURL(url, options.fetchRequestInit, ({
      result
    }) => {
      cssText = cssText.replace(loc, `url(${result})`);
      return [loc, result];
    });
  });
  return Promise.all(loadFonts).then(() => cssText);
}
function parseCSS(source) {
  if (source == null) {
    return [];
  }
  const result = [];
  const commentsRegex = /(\/\*[\s\S]*?\*\/)/gi;
  // strip out comments
  let cssText = source.replace(commentsRegex, '');
  // eslint-disable-next-line prefer-regex-literals
  const keyframesRegex = new RegExp('((@.*?keyframes [\\s\\S]*?){([\\s\\S]*?}\\s*?)})', 'gi');
  // eslint-disable-next-line no-constant-condition
  while (true) {
    const matches = keyframesRegex.exec(cssText);
    if (matches === null) {
      break;
    }
    result.push(matches[0]);
  }
  cssText = cssText.replace(keyframesRegex, '');
  const importRegex = /@import[\s\S]*?url\([^)]*\)[\s\S]*?;/gi;
  // to match css & media queries together
  const combinedCSSRegex = '((\\s*?(?:\\/\\*[\\s\\S]*?\\*\\/)?\\s*?@media[\\s\\S]' + '*?){([\\s\\S]*?)}\\s*?})|(([\\s\\S]*?){([\\s\\S]*?)})';
  // unified regex
  const unifiedRegex = new RegExp(combinedCSSRegex, 'gi');
  // eslint-disable-next-line no-constant-condition
  while (true) {
    let matches = importRegex.exec(cssText);
    if (matches === null) {
      matches = unifiedRegex.exec(cssText);
      if (matches === null) {
        break;
      } else {
        importRegex.lastIndex = unifiedRegex.lastIndex;
      }
    } else {
      unifiedRegex.lastIndex = importRegex.lastIndex;
    }
    result.push(matches[0]);
  }
  return result;
}
async function getCSSRules(styleSheets, options) {
  const ret = [];
  const deferreds = [];
  // First loop inlines imports
  styleSheets.forEach(sheet => {
    if ('cssRules' in sheet) {
      try {
        toArray(sheet.cssRules || []).forEach((item, index) => {
          if (item.type === CSSRule.IMPORT_RULE) {
            let importIndex = index + 1;
            const url = item.href;
            const deferred = fetchCSS(url).then(metadata => embedFonts(metadata, options)).then(cssText => parseCSS(cssText).forEach(rule => {
              try {
                sheet.insertRule(rule, rule.startsWith('@import') ? importIndex += 1 : sheet.cssRules.length);
              } catch (error) {
                console.error('Error inserting rule from remote css', {
                  rule,
                  error
                });
              }
            })).catch(e => {
              console.error('Error loading remote css', e.toString());
            });
            deferreds.push(deferred);
          }
        });
      } catch (e) {
        const inline = styleSheets.find(a => a.href == null) || document.styleSheets[0];
        if (sheet.href != null) {
          deferreds.push(fetchCSS(sheet.href).then(metadata => embedFonts(metadata, options)).then(cssText => parseCSS(cssText).forEach(rule => {
            inline.insertRule(rule, inline.cssRules.length);
          })).catch(err => {
            console.error('Error loading remote stylesheet', err);
          }));
        }
        console.error('Error inlining remote css file', e);
      }
    }
  });
  return Promise.all(deferreds).then(() => {
    // Second loop parses rules
    styleSheets.forEach(sheet => {
      if ('cssRules' in sheet) {
        try {
          toArray(sheet.cssRules || []).forEach(item => {
            ret.push(item);
          });
        } catch (e) {
          console.error(`Error while reading CSS rules from ${sheet.href}`, e);
        }
      }
    });
    return ret;
  });
}
function getWebFontRules(cssRules) {
  return cssRules.filter(rule => rule.type === CSSRule.FONT_FACE_RULE).filter(rule => shouldEmbed(rule.style.getPropertyValue('src')));
}
async function parseWebFontRules(node, options) {
  if (node.ownerDocument == null) {
    throw new Error('Provided element is not within a Document');
  }
  const styleSheets = toArray(node.ownerDocument.styleSheets);
  const cssRules = await getCSSRules(styleSheets, options);
  return getWebFontRules(cssRules);
}
function normalizeFontFamily(font) {
  return font.trim().replace(/["']/g, '');
}
function getUsedFonts(node) {
  const fonts = new Set();
  function traverse(node) {
    const fontFamily = node.style.fontFamily || getComputedStyle(node).fontFamily;
    fontFamily.split(',').forEach(font => {
      fonts.add(normalizeFontFamily(font));
    });
    Array.from(node.children).forEach(child => {
      if (child instanceof HTMLElement) {
        traverse(child);
      }
    });
  }
  traverse(node);
  return fonts;
}
async function embed_webfonts_getWebFontCSS(node, options) {
  const rules = await parseWebFontRules(node, options);
  const usedFonts = getUsedFonts(node);
  const cssTexts = await Promise.all(rules.filter(rule => usedFonts.has(normalizeFontFamily(rule.style.fontFamily))).map(rule => {
    const baseUrl = rule.parentStyleSheet ? rule.parentStyleSheet.href : null;
    return embedResources(rule.cssText, baseUrl, options);
  }));
  return cssTexts.join('\n');
}
async function embedWebFonts(clonedNode, options) {
  const cssText = options.fontEmbedCSS != null ? options.fontEmbedCSS : options.skipFonts ? null : await embed_webfonts_getWebFontCSS(clonedNode, options);
  if (cssText) {
    const styleNode = document.createElement('style');
    const sytleContent = document.createTextNode(cssText);
    styleNode.appendChild(sytleContent);
    if (clonedNode.firstChild) {
      clonedNode.insertBefore(styleNode, clonedNode.firstChild);
    } else {
      clonedNode.appendChild(styleNode);
    }
  }
}
;// ./node_modules/html-to-image/es/index.js





async function toSvg(node, options = {}) {
  const {
    width,
    height
  } = util_getImageSize(node, options);
  const clonedNode = await cloneNode(node, options, true);
  await embedWebFonts(clonedNode, options);
  await embedImages(clonedNode, options);
  applyStyle(clonedNode, options);
  const datauri = await nodeToDataURL(clonedNode, width, height);
  return datauri;
}
async function toCanvas(node, options = {}) {
  const {
    width,
    height
  } = util_getImageSize(node, options);
  const svg = await toSvg(node, options);
  const img = await createImage(svg);
  const canvas = document.createElement('canvas');
  const context = canvas.getContext('2d');
  const ratio = options.pixelRatio || getPixelRatio();
  const canvasWidth = options.canvasWidth || width;
  const canvasHeight = options.canvasHeight || height;
  canvas.width = canvasWidth * ratio;
  canvas.height = canvasHeight * ratio;
  if (!options.skipAutoScale) {
    checkCanvasDimensions(canvas);
  }
  canvas.style.width = `${canvasWidth}`;
  canvas.style.height = `${canvasHeight}`;
  if (options.backgroundColor) {
    context.fillStyle = options.backgroundColor;
    context.fillRect(0, 0, canvas.width, canvas.height);
  }
  context.drawImage(img, 0, 0, canvas.width, canvas.height);
  return canvas;
}
async function toPixelData(node, options = {}) {
  const {
    width,
    height
  } = getImageSize(node, options);
  const canvas = await toCanvas(node, options);
  const ctx = canvas.getContext('2d');
  return ctx.getImageData(0, 0, width, height).data;
}
async function toPng(node, options = {}) {
  const canvas = await toCanvas(node, options);
  return canvas.toDataURL();
}
async function toJpeg(node, options = {}) {
  const canvas = await toCanvas(node, options);
  return canvas.toDataURL('image/jpeg', options.quality || 1);
}
async function toBlob(node, options = {}) {
  const canvas = await toCanvas(node, options);
  const blob = await canvasToBlob(canvas);
  return blob;
}
async function getFontEmbedCSS(node, options = {}) {
  return getWebFontCSS(node, options);
}
;// ./src/views/importExport.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

class ImportExport {
  constructor(presenter, domRoot) {
    this.presenter = presenter;
    this.domRoot = domRoot;
    this.printHeight = 32;
    this.hasUnsavedChanges = false;
    this.originalModel = null;
    this.preRender();
  }
  render(model) {
    // Store original model on first render
    if (this.originalModel === null) {
      this.originalModel = JSON.stringify(model);
      this.updateSaveStatus("ready");
    } else {
      // Check if model has changed
      const currentModel = JSON.stringify(model);
      if (currentModel !== this.originalModel && !this.hasUnsavedChanges) {
        this.hasUnsavedChanges = true;
        this.updateSaveStatus("changed");
      }
    }
  }
  preRender() {
    // Create button group container
    const buttonGroup = document.createElement("div");
    buttonGroup.classList.add("options-element");
    buttonGroup.style.display = "flex";
    buttonGroup.style.alignItems = "center";
    buttonGroup.style.gap = "0.3rem";
    buttonGroup.style.marginRight = "0.5em";

    // Create status icon
    const statusIcon = document.createElement("span");
    statusIcon.id = "save-status-icon";
    statusIcon.innerHTML = "<i class='fas fa-circle'></i>"; // Unicode circle
    statusIcon.style.color = "#6c757d"; // muted color
    statusIcon.style.fontSize = "0.875rem";
    statusIcon.style.cursor = "help";
    statusIcon.setAttribute("title", "Bereit zum Speichern");
    statusIcon.classList.add("tooltip", "tooltip-bottom");
    statusIcon.setAttribute("data-tooltip", "Bereit zum Speichern");

    // Create save button
    const saveButton = document.createElement("button");
    saveButton.type = "button";
    saveButton.style.backgroundColor = "#28a745";
    saveButton.style.color = "white";
    saveButton.style.border = "1px solid #28a745";
    saveButton.style.borderRadius = "0.25rem";
    saveButton.style.padding = "0.25rem 0.5rem";
    saveButton.style.fontSize = "0.875rem";
    saveButton.style.cursor = "pointer";
    saveButton.style.display = "flex";
    saveButton.style.alignItems = "center";
    saveButton.style.gap = "0.2rem";
    saveButton.id = "save-btn";
    saveButton.innerHTML = "<i class='fas fa-save'></i> Speichern";
    saveButton.addEventListener("click", () => {
      this.saveContent();
    });

    // Create submit button
    const submitButton = document.createElement("button");
    submitButton.type = "button";
    submitButton.style.backgroundColor = "#007bff";
    submitButton.style.color = "white";
    submitButton.style.border = "1px solid #007bff";
    submitButton.style.borderRadius = "0.25rem";
    submitButton.style.padding = "0.25rem 0.5rem";
    submitButton.style.fontSize = "0.875rem";
    submitButton.style.cursor = "pointer";
    submitButton.style.display = "flex";
    submitButton.style.alignItems = "center";
    submitButton.style.gap = "0.2rem";
    submitButton.id = "submit-btn";
    submitButton.innerHTML = "<i class='fas fa-paper-plane'></i> Abgeben";
    submitButton.addEventListener("click", () => {
      this.submitContent();
    });

    // Create export button styled like save/submit buttons
    const exportButton = document.createElement("button");
    exportButton.type = "button";
    exportButton.style.backgroundColor = "#17a2b8";
    exportButton.style.color = "white";
    exportButton.style.border = "1px solid #17a2b8";
    exportButton.style.borderRadius = "0.25rem";
    exportButton.style.padding = "0.25rem 0.5rem";
    exportButton.style.fontSize = "0.875rem";
    exportButton.style.cursor = "pointer";
    exportButton.style.display = "flex";
    exportButton.style.alignItems = "center";
    exportButton.style.gap = "0.2rem";
    exportButton.classList.add("tooltip", "tooltip-bottom");
    exportButton.setAttribute("data-tooltip", "Bildexport");
    exportButton.innerHTML = "<i class='fas fa-file-image'></i>";
    exportButton.addEventListener("click", () => this.exportAsPngWithPackage());

    // Create Dashboard button
    const dashboardButton = document.createElement("button");
    dashboardButton.type = "button";
    dashboardButton.style.backgroundColor = "#ffffff";
    dashboardButton.style.color = "#6c757d";
    dashboardButton.style.border = "1px solid #6c757d";
    dashboardButton.style.borderRadius = "0.25rem";
    dashboardButton.style.padding = "0.25rem 0.5rem";
    dashboardButton.style.fontSize = "0.875rem";
    dashboardButton.style.cursor = "pointer";
    dashboardButton.style.display = "flex";
    dashboardButton.style.alignItems = "center";
    dashboardButton.style.gap = "0.2rem";
    dashboardButton.classList.add("tooltip", "tooltip-bottom");
    dashboardButton.setAttribute("data-tooltip", "Dashboard");
    dashboardButton.innerHTML = "<i class='fas fa-house'></i>";
    dashboardButton.addEventListener("click", () => {
      window.location.href = "/dashboard";
    });

    // Create task description button
    const taskButton = document.createElement("button");
    taskButton.type = "button";
    taskButton.style.backgroundColor = "#6c757d";
    taskButton.style.color = "white";
    taskButton.style.border = "1px solid #6c757d";
    taskButton.style.borderRadius = "0.25rem";
    taskButton.style.padding = "0.25rem 0.5rem";
    taskButton.style.fontSize = "0.875rem";
    taskButton.style.cursor = "pointer";
    taskButton.style.display = "flex";
    taskButton.style.alignItems = "center";
    taskButton.style.gap = "0.2rem";
    taskButton.classList.add("tooltip", "tooltip-bottom");
    taskButton.setAttribute("data-tooltip", "Aufgabenbeschreibung");
    taskButton.innerHTML = "<i class='fas fa-pen-to-square'></i>";
    taskButton.addEventListener("click", () => this.showTaskDescription());

    // Add all elements to the button group
    buttonGroup.appendChild(statusIcon);
    buttonGroup.appendChild(saveButton);
    buttonGroup.appendChild(submitButton);
    buttonGroup.appendChild(taskButton);
    buttonGroup.appendChild(exportButton);
    buttonGroup.appendChild(dashboardButton);
    document.getElementById("optionButtons").appendChild(buttonGroup);

    // Create task description modal
    this.createTaskDescriptionModal();

    // ugly fix for HTMLToImage package
    // first creation of the image misses the lines in the image
    toPng(document.getElementById("structogram")).then(function (dataUrl) {});
  }

  /**
   * Render the current tree element on a canvas position and call to render childs
   *
   * @param    subTree        object of the current element / sub tree of the struktogramm
   * @param    ctx            instance of the canvas
   * @param    x              current x position on the canvas to start drawing
   * @param    xmax           absolute x position until then may be drawn
   * @param    y              current y position on the canvas to start drawing
   * @param    overhead       overhead of the current element, used to calculate the y position of the next element
   * @param    oneLineNodes   number of nodes that are drawn on one line, used to calculate the y position of the next element
   * @return   int            max y positon to which was drawn already, so the parent element knows where to draw the next element
   */
  renderTreeAsCanvas(subTree, ctx, x, xmax, y, givenStepSize = 1) {
    // uses a recursive structure, termination condition is no definied element to be drawn
    if (subTree === null) {
      return y;
    } else {
      const defaultMargin = 22;
      // use for every possible element type a different drawing strategie
      switch (subTree.type) {
        case "InsertNode":
          return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, y, givenStepSize);
        case "Placeholder":
          {
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(xmax, y);
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + this.printHeight);
            ctx.moveTo(xmax, y);
            ctx.lineTo(xmax, y + this.printHeight);
            ctx.stroke();
            ctx.beginPath();
            const centerX = x + (xmax - x) / 2;
            const centerY = y + this.printHeight / 2;
            ctx.arc(centerX, centerY, 8, 0, 2 * Math.PI);
            ctx.moveTo(centerX - 11, centerY + 11);
            ctx.lineTo(centerX + 11, centerY - 11);
            ctx.stroke();
            return y + this.printHeight;
          }
        case "InputNode":
          {
            const stepSize = this.printHeight * givenStepSize;
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(xmax, y);
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + stepSize);
            ctx.moveTo(xmax, y);
            ctx.lineTo(xmax, y + stepSize);
            ctx.stroke();
            ctx.fillStyle = "#fcedce";
            ctx.rect(x, y, xmax, stepSize);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            ctx.fillText("E: " + subTree.text, x + 15, y + defaultMargin);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, y + stepSize, givenStepSize);
          }
        case "OutputNode":
          {
            const stepSize = this.printHeight * givenStepSize;
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(xmax, y);
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + stepSize);
            ctx.moveTo(xmax, y);
            ctx.lineTo(xmax, y + stepSize);
            ctx.stroke();
            ctx.fillStyle = "#fcedce";
            ctx.rect(x, y, xmax, stepSize);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            ctx.fillText("A: " + subTree.text, x + 15, y + defaultMargin);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, y + stepSize, givenStepSize);
          }
        case "TaskNode":
          {
            const stepSize = this.printHeight * givenStepSize;
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(xmax, y);
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + stepSize);
            ctx.moveTo(xmax, y);
            ctx.lineTo(xmax, y + stepSize);
            ctx.stroke();
            ctx.fillStyle = "#fcedce";
            ctx.rect(x, y, xmax - x, stepSize);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            ctx.fillText(subTree.text, x + 15, y + defaultMargin);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, y + stepSize, givenStepSize);
          }
        case "BranchNode":
          {
            ctx.fillStyle = "rgb(250, 218, 209)";
            ctx.beginPath(); // to end open paths
            ctx.rect(x, y, xmax - x, 2 * this.printHeight);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.stroke();
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(x + (xmax - x) / 2, y + 2 * this.printHeight);
            ctx.moveTo(xmax, y);
            ctx.lineTo(x + (xmax - x) / 2, y + 2 * this.printHeight);
            ctx.stroke();
            // center the text
            const textWidth = ctx.measureText(subTree.text);
            ctx.beginPath();
            ctx.fillText(subTree.text, x + Math.abs(xmax - x - textWidth.width) / 2, y + defaultMargin);
            ctx.stroke();
            ctx.beginPath();
            ctx.fillText("Wahr", x + 15, y + this.printHeight + defaultMargin);
            ctx.fillText("Falsch", xmax - 15 - ctx.measureText("Falsch").width, y + this.printHeight + defaultMargin);
            ctx.stroke();
            let trueChildY = 0;
            let falseChildY = 0;
            // render the child sub trees
            const trueDepth = this.preCountTreeDepth(subTree.trueChild);
            const falseDepth = this.preCountTreeDepth(subTree.falseChild);
            if (trueDepth > falseDepth) {
              trueChildY = this.renderTreeAsCanvas(subTree.trueChild, ctx, x, x + (xmax - x) / 2, y + 2 * this.printHeight, givenStepSize);
              falseChildY = this.renderTreeAsCanvas(subTree.falseChild, ctx, x + (xmax - x) / 2, xmax, y + 2 * this.printHeight, (this.preCountTreeDepth(subTree.trueChild) - this.preCountNonOneLiners(subTree.falseChild)) / this.preCountOneLiners(subTree.falseChild) * givenStepSize);
            } else {
              trueChildY = this.renderTreeAsCanvas(subTree.trueChild, ctx, x, x + (xmax - x) / 2, y + 2 * this.printHeight, (this.preCountTreeDepth(subTree.falseChild) - this.preCountNonOneLiners(subTree.trueChild)) / this.preCountOneLiners(subTree.trueChild) * givenStepSize);
              falseChildY = this.renderTreeAsCanvas(subTree.falseChild, ctx, x + (xmax - x) / 2, xmax, y + 2 * this.printHeight, givenStepSize);
            }

            // determine which child sub tree is deeper y wise
            let valueY, followY;
            if (trueChildY < falseChildY) {
              valueY = falseChildY;
              followY = trueChildY;
            } else {
              valueY = trueChildY;
              followY = falseChildY;
            }
            ctx.rect(x, y, xmax - x, valueY - y);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, followY, givenStepSize);
          }
        case "CountLoopNode":
        case "HeadLoopNode":
          {
            const childY = this.renderTreeAsCanvas(subTree.child, ctx, x + (xmax - x) / 12, xmax, y + this.printHeight, givenStepSize);
            ctx.rect(x, y, xmax - x, childY - y);
            ctx.stroke();
            ctx.beginPath();
            ctx.fillStyle = "rgb(220, 239, 231)";
            ctx.rect(x, y, xmax, this.printHeight - 1);
            ctx.rect(x, y, (xmax - x) / 12 - 1, childY - y);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            ctx.fillText(subTree.text, x + 15, y + defaultMargin);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, childY, givenStepSize);
          }
        case "FootLoopNode":
          {
            const childY = this.renderTreeAsCanvas(subTree.child, ctx, x + (xmax - x) / 12, xmax, y, givenStepSize);
            ctx.rect(x, y, xmax - x, childY - y + this.printHeight);
            ctx.stroke();
            ctx.beginPath();
            ctx.fillStyle = "rgb(220, 239, 231)";
            ctx.rect(x, y, (xmax - x) / 12, childY - y + this.printHeight);
            ctx.rect(x, childY, xmax, this.printHeight);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            ctx.fillText(subTree.text, x + 15, childY + defaultMargin);
            ctx.stroke();
            ctx.beginPath();
            ctx.moveTo(x + (xmax - x) / 12, childY);
            ctx.lineTo(xmax, childY);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, childY + this.printHeight, givenStepSize);
          }
        case "CaseNode":
          {
            ctx.fillStyle = "rgb(250, 218, 209)";
            ctx.beginPath();
            ctx.rect(x, y, xmax - x, 2 * this.printHeight);
            ctx.fill();
            ctx.fillStyle = "black";
            let caseCount = subTree.cases.length;
            if (subTree.defaultOn) {
              caseCount = caseCount + 1;
            }
            // calculate the x and y distance between each case
            // yStep ist used for the positioning of the vertical lines on the diagonal line
            const xStep = (xmax - x) / caseCount;
            const yStep = this.printHeight / subTree.cases.length;
            ctx.stroke();
            ctx.beginPath();
            ctx.moveTo(x, y);
            if (subTree.defaultOn) {
              ctx.lineTo(xmax - xStep, y + this.printHeight);
              ctx.lineTo(xmax, y);
              ctx.moveTo(xmax - xStep, y + this.printHeight);
              ctx.lineTo(xmax - xStep, y + 2 * this.printHeight);
              ctx.stroke();
              const textWidth = ctx.measureText(subTree.text);
              ctx.beginPath();
              ctx.fillText(subTree.text, xmax - xStep - textWidth.width * 1.3 / 2, y + defaultMargin * 0.7);
              ctx.stroke();
            } else {
              ctx.lineTo(xmax, y + this.printHeight);
              ctx.stroke();
              const textWidth = ctx.measureText(subTree.text);
              ctx.beginPath();
              ctx.fillText(subTree.text, xmax - textWidth.width, y + defaultMargin * 0.7);
              ctx.stroke();
            }
            let xPos = x;
            // determine the deepest tree by the y coordinate
            const maxDepth = this.preCountTreeDepth(subTree) - 2;
            const maxCase = this.getDeepestCase(subTree);
            let yFinally = y + 3 * this.printHeight;
            for (const element of subTree.cases) {
              let childY;
              if (maxCase === element) {
                // is the deepest tree
                childY = this.renderTreeAsCanvas(element, ctx, xPos, xPos + xStep, y + this.printHeight, givenStepSize);
              } else {
                if (maxDepth === this.preCountTreeDepth(element)) {
                  // is not the deepest tree but has the same depth as the deepest tree
                  const newStepSize = (this.preCountTreeDepth(element) * givenStepSize - this.preCountNonOneLiners(element)) / this.preCountOneLiners(element);
                  childY = this.renderTreeAsCanvas(element, ctx, xPos, xPos + xStep, y + this.printHeight, newStepSize);
                } else {
                  // is not the deepest tree
                  const newStepSize = (maxDepth - this.preCountNonOneLiners(element)) / this.preCountOneLiners(element) * givenStepSize;
                  childY = this.renderTreeAsCanvas(element, ctx, xPos, xPos + xStep, y + this.printHeight, newStepSize);
                }
              }
              if (childY > yFinally) {
                yFinally = childY;
              }
              xPos = xPos + xStep;
            }
            if (subTree.defaultOn) {
              let childY;
              if (maxCase === subTree.defaultNode) {
                // is the deepest tree
                childY = this.renderTreeAsCanvas(subTree.defaultNode, ctx, xPos, xPos + xStep, y + this.printHeight, givenStepSize);
              } else {
                if (maxDepth === this.preCountTreeDepth(subTree.defaultNode)) {
                  // is not the deepest tree but has the same depth as the deepest tree
                  const newStepSize = (this.preCountTreeDepth(subTree.defaultNode) * givenStepSize - this.preCountNonOneLiners(subTree.defaultNode)) / this.preCountOneLiners(subTree.defaultNode);
                  childY = this.renderTreeAsCanvas(subTree.defaultNode, ctx, xPos, xPos + xStep, y + this.printHeight, newStepSize);
                } else {
                  // is not the deepest tree
                  const newStepSize = (maxDepth - this.preCountNonOneLiners(subTree.defaultNode)) / this.preCountOneLiners(subTree.defaultNode) * givenStepSize;
                  childY = this.renderTreeAsCanvas(subTree.defaultNode, ctx, xPos, xPos + xStep, y + this.printHeight, newStepSize);
                }
              }
              if (childY > yFinally) {
                yFinally = childY;
              }
            }
            // draw the vertical lines
            for (let i = 1; i <= subTree.cases.length; i++) {
              ctx.beginPath();
              ctx.moveTo(x + i * xStep, y + i * yStep);
              ctx.lineTo(x + i * xStep, yFinally);
              ctx.stroke();
            }
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, yFinally, givenStepSize);
          }
        case "InsertCase":
          {
            const textWidth = ctx.measureText(subTree.text);
            ctx.beginPath();
            ctx.fillText(subTree.text, x + Math.abs(xmax - x - textWidth.width) / 2, y + defaultMargin);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, y + this.printHeight, givenStepSize);
          }
        case "FunctionNode":
          {
            const childY = this.renderTreeAsCanvas(subTree.child, ctx, x + (xmax - x) / 12, xmax, y + this.printHeight);
            ctx.rect(x, y, xmax - x, childY - y);
            ctx.stroke();
            ctx.beginPath();
            ctx.fillStyle = "white";
            ctx.rect(x, y, xmax, this.printHeight - 1);
            ctx.rect(x, y, (xmax - x) / 12 - 1, childY - y + this.printHeight);
            ctx.rect(x, childY, xmax, this.printHeight - 2);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            let paramsText = "";
            for (let index = 0; index < subTree.parameters.length; index++) {
              if (subTree.parameters.length === 0 || index === subTree.parameters.length - 1) {
                paramsText += subTree.parameters[index].parName;
              } else {
                paramsText += subTree.parameters[index].parName + ", ";
              }
            }
            ctx.fillText("function " + subTree.text + "(" + paramsText + ") {", x + 15, y + defaultMargin);
            ctx.fillText("}", x + 15, childY + defaultMargin);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, childY + this.printHeight);
          }
        case "TryCatchNode":
          {
            const trychildY = this.renderTreeAsCanvas(subTree.tryChild, ctx, x + (xmax - x) / 12, xmax, y + this.printHeight, givenStepSize);
            const catchchildY = this.renderTreeAsCanvas(subTree.catchChild, ctx, x + (xmax - x) / 12, xmax, trychildY + this.printHeight, givenStepSize);
            ctx.rect(x, y, xmax - x, catchchildY - y);
            ctx.stroke();
            ctx.beginPath();
            ctx.fillStyle = "rgb(250, 218, 209)";
            ctx.rect(x, y, xmax, this.printHeight - 1);
            ctx.rect(x, trychildY, xmax, this.printHeight - 1);
            ctx.rect(x, y, (xmax - x) / 12 - 1, catchchildY - y);
            ctx.fill();
            ctx.fillStyle = "black";
            ctx.beginPath();
            ctx.fillText("Try", x + 15, y + defaultMargin);
            ctx.fillText("Catch", x + 15, trychildY + defaultMargin);
            ctx.stroke();
            ctx.beginPath();
            ctx.moveTo(x + (xmax - x) / 12, trychildY);
            ctx.lineTo(xmax, trychildY);
            ctx.stroke();
            return this.renderTreeAsCanvas(subTree.followElement, ctx, x, xmax, catchchildY, givenStepSize);
          }
      }
    }
  }

  /**
   * Count the depth of the current tree element
   *
   * @param    subTree   object of the current element / sub tree of the struktogramm
   * @return   int       depth of the current tree element
   */
  preCountTreeDepth(subTree) {
    if (subTree === null) {
      return 0;
    } else {
      switch (subTree.type) {
        case "FunctionNode":
        case "InsertNode":
        case "InsertCase":
          return this.preCountTreeDepth(subTree.followElement);
        case "Placeholder":
          {
            return 1;
          }
        case "InputNode":
        case "OutputNode":
        case "TaskNode":
          {
            return 1 + this.preCountTreeDepth(subTree.followElement);
          }
        case "BranchNode":
          {
            const trueChild = this.preCountTreeDepth(subTree.trueChild);
            const falseChild = this.preCountTreeDepth(subTree.falseChild);
            if (trueChild < falseChild) {
              return 2 + falseChild;
            } else {
              return 2 + trueChild;
            }
          }
        case "CountLoopNode":
        case "HeadLoopNode":
        case "FootLoopNode":
          {
            return 1 + this.preCountTreeDepth(subTree.child) + this.preCountTreeDepth(subTree.followElement);
          }
        case "TryCatchNode":
          {
            return 2 + this.preCountTreeDepth(subTree.tryChild) + this.preCountTreeDepth(subTree.catchChild) + this.preCountTreeDepth(subTree.followElement);
          }
        case "CaseNode":
          {
            const maxList = [];
            for (const element of subTree.cases) {
              maxList.push(this.preCountTreeDepth(element));
            }
            if (subTree.defaultOn) {
              maxList.push(this.preCountTreeDepth(subTree.defaultNode));
            }
            return 2 + Math.max(...maxList);
          }
      }
    }
  }

  /**
   * Return the case with the deepest depth
   *
   * @param {*} subTree
   * @returns
   */
  getDeepestCase(subTree) {
    const maxList = [];
    const normalNodes = [];
    for (const element of subTree.cases) {
      maxList.push(this.preCountTreeDepth(element));
      normalNodes.push(this.preCountOneLiners(element));
    }
    if (subTree.defaultOn) {
      maxList.push(this.preCountTreeDepth(subTree.defaultNode));
      normalNodes.push(this.preCountOneLiners(subTree.defaultNode));
    }
    const maxDeph = Math.max(...maxList);
    for (let index = 0; index < maxList.length; index++) {
      if (maxList[index] === maxDeph) {
        maxList[index] += normalNodes[index];
      }
    }
    const index = maxList.indexOf(Math.max(...maxList));
    if (index === maxList.length - 1) {
      return subTree.defaultNode;
    } else {
      return subTree.cases[index];
    }
  }

  /**
   * Count the depth of the current tree element
   *
   * @param    subTree   object of the current element / sub tree of the struktogramm
   * @return   int       depth of the current tree element
   */
  preCountNonOneLiners(subTree) {
    if (subTree === null) {
      return 0;
    } else {
      switch (subTree.type) {
        case "FunctionNode":
        case "InsertNode":
        case "InsertCase":
          return this.preCountNonOneLiners(subTree.followElement);
        case "Placeholder":
          {
            return 0;
          }
        case "InputNode":
        case "OutputNode":
        case "TaskNode":
          {
            return this.preCountNonOneLiners(subTree.followElement);
          }
        case "BranchNode":
          {
            const trueChild = this.preCountNonOneLiners(subTree.trueChild);
            const falseChild = this.preCountNonOneLiners(subTree.falseChild);
            if (trueChild < falseChild) {
              return 2 + falseChild;
            } else {
              return 2 + trueChild;
            }
          }
        case "CountLoopNode":
        case "HeadLoopNode":
        case "FootLoopNode":
          {
            return 1 + this.preCountNonOneLiners(subTree.child) + this.preCountNonOneLiners(subTree.followElement);
          }
        case "TryCatchNode":
          {
            return 2 + this.preCountNonOneLiners(subTree.tryChild) + this.preCountNonOneLiners(subTree.catchChild) + this.preCountNonOneLiners(subTree.followElement);
          }
        case "CaseNode":
          {
            const maxList = [];
            for (const element of subTree.cases) {
              maxList.push(this.preCountNonOneLiners(element));
            }
            if (subTree.defaultOn) {
              maxList.push(this.preCountNonOneLiners(subTree.defaultNode));
            }
            return 2 +
            // Math.max(...maxList)
            this.preCountNonOneLiners(this.getDeepestCase(subTree));
          }
      }
    }
  }

  /**
   * Count the depth of the current tree element
   *
   * @param    subTree   object of the current element / sub tree of the struktogramm
   * @return   int       depth of the current tree element
   */
  preCountOneLiners(subTree) {
    if (subTree === null) {
      return 0;
    } else {
      switch (subTree.type) {
        case "FunctionNode":
        case "InsertNode":
        case "InsertCase":
          return this.preCountOneLiners(subTree.followElement);
        case "Placeholder":
          {
            return 1;
          }
        case "InputNode":
        case "OutputNode":
        case "TaskNode":
          {
            return 1 + this.preCountOneLiners(subTree.followElement);
          }
        case "BranchNode":
          {
            const trueChild = this.preCountOneLiners(subTree.trueChild);
            const falseChild = this.preCountOneLiners(subTree.falseChild);
            if (trueChild < falseChild) {
              return falseChild;
            } else {
              return trueChild;
            }
          }
        case "CountLoopNode":
        case "HeadLoopNode":
        case "FootLoopNode":
          {
            return this.preCountOneLiners(subTree.child) + this.preCountOneLiners(subTree.followElement);
          }
        case "TryCatchNode":
          {
            return this.preCountOneLiners(subTree.tryChild) + this.preCountOneLiners(subTree.catchChild) + this.preCountOneLiners(subTree.followElement);
          }
        case "CaseNode":
          {
            const maxList = [];
            for (const element of subTree.cases) {
              maxList.push(this.preCountOneLiners(element));
            }
            if (subTree.defaultOn) {
              maxList.push(this.preCountOneLiners(subTree.defaultNode));
            }
            return Math.max(...maxList);
          }
      }
    }
  }

  /**
   * Create a PNG file of the current model and append a button for downloading
   */
  exportAsPng(model) {
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");
    const width = document.getElementById("structogram").parentElement.parentElement.clientWidth;
    canvas.width = width;
    canvas.height = document.getElementById("structogram").clientHeight;
    ctx.font = "16px sans-serif";
    ctx.lineWidth = "1";
    // render the tree on the canvas
    const lastY = this.renderTreeAsCanvas(model, ctx, 0, width, 0);
    ctx.rect(0, 0, width, lastY + 1);
    ctx.strokeStyle = "black";
    ctx.lineWidth = 1;
    ctx.stroke();

    // define filename
    const exportFileDefaultName = "struktog_" + new Date(Date.now()).toJSON().substring(0, 10) + ".png";

    // create button / anker element
    const linkElement = document.createElement("a");
    linkElement.setAttribute("href", canvas.toDataURL("image/png"));
    linkElement.setAttribute("download", exportFileDefaultName);
    linkElement.click();
  }

  /**
   * Create a PNG file of the current model with htmtToImage and append a button for downloading
   */
  // Simple Markdown to HTML converter
  markdownToHtml(markdown) {
    return markdown
    // Headers
    .replace(/^### (.*$)/gim, "<h3>$1</h3>").replace(/^## (.*$)/gim, "<h2>$1</h2>").replace(/^# (.*$)/gim, "<h1>$1</h1>")
    // Bold
    .replace(/\*\*(.*)\*\*/gim, "<strong>$1</strong>")
    // Italic
    .replace(/\*(.*)\*/gim, "<em>$1</em>")
    // Code blocks
    .replace(/```([\s\S]*?)```/gim, "<pre><code>$1</code></pre>")
    // Inline code
    .replace(/`([^`]*)`/gim, "<code>$1</code>")
    // Line breaks
    .replace(/\n/gim, "<br>");
  }
  createTaskDescriptionModal() {
    // Create modal backdrop
    const modalBackdrop = document.createElement("div");
    modalBackdrop.id = "taskDescriptionModal";
    modalBackdrop.style.display = "none";
    modalBackdrop.style.position = "fixed";
    modalBackdrop.style.zIndex = "1000";
    modalBackdrop.style.left = "0";
    modalBackdrop.style.top = "0";
    modalBackdrop.style.width = "100%";
    modalBackdrop.style.height = "100%";
    modalBackdrop.style.backgroundColor = "rgba(0,0,0,0.5)";

    // Create modal content
    const modalContent = document.createElement("div");
    modalContent.style.backgroundColor = "#fefefe";
    modalContent.style.margin = "5% auto";
    modalContent.style.padding = "20px";
    modalContent.style.border = "1px solid #888";
    modalContent.style.borderRadius = "8px";
    modalContent.style.width = "80%";
    modalContent.style.maxWidth = "600px";
    modalContent.style.maxHeight = "80vh";
    modalContent.style.overflow = "auto";
    modalContent.style.position = "relative";

    // Create modal header
    const modalHeader = document.createElement("div");
    modalHeader.style.display = "flex";
    modalHeader.style.justifyContent = "space-between";
    modalHeader.style.alignItems = "center";
    modalHeader.style.marginBottom = "20px";
    modalHeader.style.borderBottom = "1px solid #ddd";
    modalHeader.style.paddingBottom = "10px";
    const modalTitle = document.createElement("h2");
    modalTitle.textContent = "Aufgabenbeschreibung";
    modalTitle.style.margin = "0";
    modalTitle.style.color = "#264040";
    const closeButton = document.createElement("span");
    closeButton.innerHTML = "&times;";
    closeButton.style.color = "#aaa";
    closeButton.style.fontSize = "28px";
    closeButton.style.fontWeight = "bold";
    closeButton.style.cursor = "pointer";
    closeButton.addEventListener("click", () => this.hideTaskDescription());
    closeButton.addEventListener("mouseenter", () => {
      closeButton.style.color = "#000";
    });
    closeButton.addEventListener("mouseleave", () => {
      closeButton.style.color = "#aaa";
    });
    modalHeader.appendChild(modalTitle);
    modalHeader.appendChild(closeButton);

    // Create modal body
    const modalBody = document.createElement("div");
    modalBody.id = "taskDescriptionContent";
    modalBody.style.lineHeight = "1.6";
    modalBody.style.color = "#333";
    modalContent.appendChild(modalHeader);
    modalContent.appendChild(modalBody);
    modalBackdrop.appendChild(modalContent);
    document.body.appendChild(modalBackdrop);

    // Close modal when clicking outside
    modalBackdrop.addEventListener("click", e => {
      if (e.target === modalBackdrop) {
        this.hideTaskDescription();
      }
    });

    // Close modal with Escape key
    document.addEventListener("keydown", e => {
      if (e.key === "Escape" && modalBackdrop.style.display === "block") {
        this.hideTaskDescription();
      }
    });
  }
  showTaskDescription() {
    const modal = document.getElementById("taskDescriptionModal");
    const content = document.getElementById("taskDescriptionContent");
    const descriptionDiv = document.getElementById("description");
    if (descriptionDiv && descriptionDiv.textContent) {
      const markdownText = descriptionDiv.textContent.trim();
      const htmlContent = this.markdownToHtml(markdownText);
      content.innerHTML = htmlContent;
    } else {
      content.innerHTML = "<p>Keine Aufgabenbeschreibung verfügbar.</p>";
    }
    modal.style.display = "block";
  }
  hideTaskDescription() {
    const modal = document.getElementById("taskDescriptionModal");
    modal.style.display = "none";
  }
  updateSaveStatus(status) {
    const statusIcon = document.getElementById("save-status-icon");
    switch (status) {
      case "ready":
        statusIcon.style.color = "#6c757d"; // grau
        statusIcon.setAttribute("title", "Bereit zum Speichern");
        statusIcon.setAttribute("data-tooltip", "Bereit zum Speichern");
        break;
      case "changed":
        statusIcon.style.color = "#ffc107"; // gelb
        statusIcon.setAttribute("title", "Ungespeicherte Änderungen");
        statusIcon.setAttribute("data-tooltip", "Ungespeicherte Änderungen");
        break;
      case "saving":
        statusIcon.style.color = "#007bff"; // blau
        statusIcon.setAttribute("title", "Speichere...");
        statusIcon.setAttribute("data-tooltip", "Speichere...");
        break;
      case "submitting":
        statusIcon.style.color = "#007bff"; // blau
        statusIcon.setAttribute("title", "Gebe ab...");
        statusIcon.setAttribute("data-tooltip", "Gebe ab...");
        break;
      case "saved":
        statusIcon.style.color = "#28a745"; // grün
        statusIcon.setAttribute("title", "Änderungen gespeichert");
        statusIcon.setAttribute("data-tooltip", "Änderungen gespeichert");
        break;
      case "submitted":
        statusIcon.style.color = "#28a745"; // grün
        statusIcon.setAttribute("title", "Aufgabe abgegeben");
        statusIcon.setAttribute("data-tooltip", "Aufgabe abgegeben");
        break;
      case "error":
        statusIcon.style.color = "#dc3545"; // rot
        statusIcon.setAttribute("title", "Fehler beim Speichern/Abgeben");
        statusIcon.setAttribute("data-tooltip", "Fehler beim Speichern/Abgeben");
        break;
    }
  }
  getSaveUrl() {
    return document.getElementById("task-save-url").getAttribute("data-url");
  }
  getSubmitUrl() {
    return document.getElementById("task-submit-url").getAttribute("data-url");
  }
  getContentToSave() {
    // Erstelle das gleiche JSON-Format wie beim Download mit Konfigurationsname
    const data = {
      version: "1.4.0",
      config: this.presenter.getCurrentConfigName(),
      tree: this.presenter.getModelTree(),
      showCodeButton: this.presenter.getConfig().showCodeButton
    };
    return JSON.stringify(data);
  }
  saveContent() {
    this.updateSaveStatus("saving");
    const content = this.getContentToSave();
    const saveUrl = this.getSaveUrl();
    fetch(saveUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        content: content
      })
    }).then(response => {
      if (response.ok) {
        this.updateSaveStatus("saved");
        this.hasUnsavedChanges = false;
        this.originalModel = JSON.stringify(this.presenter.getModelTree());

        // Notify parent window about save if in iframe
        if (window.parent && window.parent !== window) {
          window.parent.postMessage("content-saved", "*");
        }
      } else {
        this.updateSaveStatus("error");
        console.error("Save failed:", response.statusText);
      }
    }).catch(error => {
      this.updateSaveStatus("error");
      console.error("Save error:", error);
    });
  }
  submitContent() {
    this.updateSaveStatus("submitting");
    const content = this.getContentToSave();
    const submitUrl = this.getSubmitUrl();
    fetch(submitUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        content: content
      })
    }).then(response => {
      if (response.ok) {
        this.updateSaveStatus("submitted");
        this.hasUnsavedChanges = false;
        this.originalModel = JSON.stringify(this.presenter.getModelTree());
      } else {
        this.updateSaveStatus("error");
        console.error("Submit failed:", response.statusText);
      }
    }).catch(error => {
      this.updateSaveStatus("error");
      console.error("Submit error:", error);
    });
  }
  exportAsPngWithPackage() {
    toPng(document.getElementById("structogram")).then(function (dataUrl) {
      const linkElement = document.createElement("a");
      linkElement.setAttribute("href", dataUrl);
      // define filename
      const exportFileDefaultName = "struktog_" + new Date(Date.now()).toJSON().substring(0, 10) + ".png";
      linkElement.setAttribute("download", exportFileDefaultName);
      linkElement.click();
    }).catch(function (error) {
      console.error("oops, something went wrong!", error);
    });
  }
  resetButtons() {}
  displaySourcecode() {}
  setLang() {}
}
;// ./src/index.js
/*
 Copyright (C) 2019-2023 Thiemo Leonhardt, Klaus Ramm, Tom-Maurice Schreiber, Sören Schwab

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */










window.onload = async function () {
  // manipulate the localStorage before loading the presenter
  if (typeof Storage !== 'undefined') {
    const url = new URL(window.location.href);
    const externJson = url.searchParams.get('url');
    if (externJson !== null) {
      fetch(externJson).then(response => response.json()).then(json => {
        console.log(json);
        presenter.readUrl(json);
      });
    }
    const configId = url.searchParams.get('config');
    config.config.loadConfig(configId);
  }
  generateHtmltree();
  generateFooter();
  // create presenter object
  const presenter = new Presenter(model, config.config);
  // TODO: this should not be necessary, but some functions depend on moveId and nextInsertElement
  model.setPresenter(presenter);

  // create our view objects
  const structogram = new Structogram(presenter, document.getElementById('editorDisplay'));
  presenter.addView(structogram);
  const code = new CodeView(presenter, document.getElementById('editorDisplay'));
  presenter.addView(code);
  const importExport = new ImportExport(presenter, document.getElementById('Export'));
  presenter.addView(importExport);

  // generateInfoButton(document.getElementById('optionButtons'))

  // Load default submission if present
  const defaultSubmissionElement = document.getElementById('defaultSubmission');
  if (defaultSubmissionElement && defaultSubmissionElement.textContent.trim()) {
    try {
      const defaultData = JSON.parse(defaultSubmissionElement.textContent.trim());
      console.log('Loading default submission:', defaultData);
      await presenter.readUrl(defaultData);
    } catch (error) {
      console.error('Error parsing default submission:', error);
      presenter.init();
    }
  } else {
    presenter.init();
  }
};
})();

/******/ })()
;
