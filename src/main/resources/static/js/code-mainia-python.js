// Version
const APP_VERSION = "1.0.12";

const mod = (a, b) => ((a % b) + b) % b;

// Kartendefinitionen
const cards = [
    // Anfangskarten
    {
        id: "start-1",
        text: "1",
        category: "Anfangskarten",
        calculate: () => 1,
    },
    {
        id: "start-2",
        text: "2",
        category: "Anfangskarten",
        calculate: () => 2,
    },
    {
        id: "start-3",
        text: "3",
        category: "Anfangskarten",
        calculate: () => 3,
    },
    {
        id: "start-0",
        text: "3",
        category: "Anfangskarten",
        calculate: () => 3,
    },
    {
        id: "start-m1",
        text: "-1",
        category: "Anfangskarten",
        calculate: () => -1,
    },
    {
        id: "start-m2",
        text: "-2",
        category: "Anfangskarten",
        calculate: () => -2,
    },
    {
        id: "start-m3",
        text: "-3",
        category: "Anfangskarten",
        calculate: () => -3,
    },

    // Wertzuweisungen
    {
        id: "wertzuweisungen-1",
        text: "zahl = 8",
        category: "Wertzuweisungen",
        calculate: (v) => 8,
    },
    {
        id: "wertzuweisungen-2",
        text: "zahl = 3",
        category: "Wertzuweisungen",
        calculate: (v) => 3,
    },
    {
        id: "wertzuweisungen-3",
        text: "zahl = 2",
        category: "Wertzuweisungen",
        calculate: (v) => 2,
    },
    {
        id: "wertzuweisungen-4",
        text: "zahl = zahl - 1",
        category: "Wertzuweisungen",
        calculate: (v) => v - 1,
    },
    {
        id: "wertzuweisungen-5",
        text: "zahl = zahl - 1",
        category: "Wertzuweisungen",
        calculate: (v) => v - 1,
    },
    {
        id: "wertzuweisungen-6",
        text: "zahl = zahl + 1",
        category: "Wertzuweisungen",
        calculate: (v) => v + 1,
    },
    {
        id: "wertzuweisungen-7",
        text: "zahl = zahl + 1",
        category: "Wertzuweisungen",
        calculate: (v) => v + 1,
    },
    {
        id: "wertzuweisungen-8",
        text: "zahl = zahl - 4",
        category: "Wertzuweisungen",
        calculate: (v) => v - 4,
    },
    {
        id: "wertzuweisungen-9",
        text: "zahl = zahl - 3",
        category: "Wertzuweisungen",
        calculate: (v) => v - 3,
    },
    {
        id: "wertzuweisungen-10",
        text: "zahl = zahl + 2",
        category: "Wertzuweisungen",
        calculate: (v) => v + 2,
    },
    {
        id: "wertzuweisungen-11",
        text: "zahl = zahl + 3",
        category: "Wertzuweisungen",
        calculate: (v) => v + 3,
    },
    {
        id: "wertzuweisungen-12",
        text: "zahl = zahl * 2",
        category: "Wertzuweisungen",
        calculate: (v) => v * 2,
    },
    {
        id: "wertzuweisungen-13",
        text: "zahl = zahl * 0",
        category: "Wertzuweisungen",
        calculate: (v) => 0,
    },
    {
        id: "wertzuweisungen-14",
        text: "zahl = zahl * 0",
        category: "Wertzuweisungen",
        calculate: (v) => 0,
    },
    {
        id: "wertzuweisungen-15",
        text: "zahl = zahl * 1",
        category: "Wertzuweisungen",
        calculate: (v) => v,
    },
    {
        id: "wertzuweisungen-16",
        text: "zahl = 3*zahl - 1",
        category: "Wertzuweisungen",
        calculate: (v) => 3 * v - 1,
    },
    {
        id: "wertzuweisungen-17",
        text: "zahl = 3*zahl + 1",
        category: "Wertzuweisungen",
        calculate: (v) => 3 * v + 1,
    },
    {
        id: "wertzuweisungen-18",
        text: "zahl = 2 + 3*zahl",
        category: "Wertzuweisungen",
        calculate: (v) => 3 * v + 2,
    },
    {
        id: "wertzuweisungen-19",
        text: "zahl = 3*zahl",
        category: "Wertzuweisungen",
        calculate: (v) => 3 * v,
    },

    // Verwirrungskarten und Vergleichsoperatoren
    {
        id: "verwirrung-1",
        text: "zah = 8",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-2",
        text: "x = 4",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-3",
        text: "ZahL = 8",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-4",
        text: "zahl == 5",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-5",
        text: "zahl == 7",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-6",
        text: "zahl == 2",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-7",
        text: "zahl = zahl",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-8",
        text: "zahl >= 2",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-9",
        text: "zahl < 0",
        category: "Verwirrung",
        calculate: (v) => v,
    },
    {
        id: "verwirrung-10",
        text: "zahl = zahl - zahl",
        category: "Verwirrung",
        calculate: (v) => 0,
    },
    {
        id: "verwirrung-11",
        text: "zahl = 5\nzahl = zahl + 2*zahl",
        category: "Verwirrung",
        calculate: (v) => 15,
    },
    {
        id: "verwirrung-12",
        text: "zahl = zahl*0 + 3",
        category: "Verwirrung",
        calculate: (v) => 3,
    },

    // Ganzzahlige Division
    {
        id: "ganzzahlDivision-1",
        text: "zahl = zahl%2",
        category: "GanzzahlDivision",
        calculate: (v) => mod(v, 2),
    },
    {
        id: "ganzzahlDivision-2",
        text: "zahl = zahl%3",
        category: "GanzzahlDivision",
        calculate: (v) => mod(v, 3),
    },
    {
        id: "ganzzahlDivision-3",
        text: "zahl = zahl%4",
        category: "GanzzahlDivision",
        calculate: (v) => mod(v, 4),
    },
    {
        id: "ganzzahlDivision-4",
        text: "zahl = zahl%5",
        category: "GanzzahlDivision",
        calculate: (v) => mod(v, 5),
    },
    {
        id: "ganzzahlDivision-5",
        text: "zahl = zahl // 2",
        category: "GanzzahlDivision",
        calculate: (v) => Math.trunc(v / 2),
    },
    {
        id: "ganzzahlDivision-6",
        text: "zahl = zahl // 3",
        category: "GanzzahlDivision",
        calculate: (v) => Math.trunc(v / 3),
    },
    {
        id: "ganzzahlDivision-7",
        text: "zahl = zahl // 4",
        category: "GanzzahlDivision",
        calculate: (v) => Math.trunc(v / 4),
    },
    {
        id: "ganzzahlDivision-8",
        text: "zahl = zahl // 5",
        category: "GanzzahlDivision",
        calculate: (v) => Math.trunc(v / 5),
    },
    {
        id: "ganzzahlDivision-9",
        text: "x = 102%5\nzahl = zahl + x",
        category: "GanzzahlDivision",
        calculate: (v) => v + 2,
    },
    {
        id: "ganzzahlDivision-10",
        text: "x = 102 // 5\nzahl = zahl - x",
        category: "GanzzahlDivision",
        calculate: (v) => v - 20,
    },

    // Bedingte Anweisungen und Verzweigungen
    {
        id: "if-1",
        text: "*if* zahl <= 4:\n   zahl = zahl + 2",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v <= 4) {
                return v + 2;
            } else {
                return v;
            }
        },
    },
    {
        id: "if-2",
        text: "*if* zahl <= 10:\n   zahl = zahl*2\n*else*:\n   zahl = 5",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v <= 10) {
                return v * 2;
            } else {
                return 5;
            }
        },
    },
    {
        id: "if-3",
        text: "*if* zahl > 7:\n   zahl = 6\n*else*:\n   zahl = zahl * (-1)",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v <= 4) {
                return v + 2;
            } else {
                return v;
            }
        },
    },
    {
        id: "if-4",
        text: "*if* zahl > 38:\n   zahl = 5\n*elif* zahl < 30:\n   zahl = zahl + 9\n*else*:\n   zahl = zahl - 5",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v > 38) {
                return 5;
            } else {
                if (v < 30) {
                    return v + 9;
                } else {
                    return v - 5;
                }
            }
        },
    },
    {
        id: "if-5",
        text: "*if* zahl <= 8:\n   zahl = zahl - 1\n*elif* zahl == zahl:\n   zahl = zahl + 1\n*else*:\n   zahl = 3",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v <= 8) {
                return v - 1;
            } else {
                return v + 1;
            }
        },
    },
    {
        id: "if-6",
        text: "*if* zahl < 0:\n   zahl = zahl + 1\n*if* zahl >= 0:\n   zahl = zahl - 1",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v < 0) {
                v = v + 1;
            }
            if (v >= 0) {
                return v - 1;
            } else {
                return v;
            }
        },
    },
    {
        id: "if-7",
        text: "*if* zahl < 5:\n   zahl = zahl + 7\n*elif* zahl > 10:\n   zahl = zahl - 7\n*else*:\n   zahl = zahl*2",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v < 5) {
                return v + 7;
            } else {
                if (v > 10) {
                    return v - 7;
                } else {
                    return v * 2;
                }
            }
        },
    },
    {
        id: "if-8",
        text: "*if* zahl > 4:\n   zahl = zahl + 5\n*elif* zahl > 20:\n   zahl = zahl - 10\n*else*:\n   zahl = zahl + 10",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v > 4) {
                return v + 5;
            } else {
                if (v > 20) {
                    return v - 10;
                } else {
                    return v + 10;
                }
            }
        },
    },
    {
        id: "if-9",
        text: "*if* zahl > 4:\n   zahl = zahl + 5\n*elif* zahl > 20:\n   zahl = zahl - 10\n*else*:\n   zahl = zahl + 10",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v > 4) {
                return v + 5;
            } else {
                if (v > 20) {
                    return v - 10;
                } else {
                    return v + 10;
                }
            }
        },
    },
    {
        id: "if-10",
        text: "x = zahl - 9\n*if* x > 0:\n   zahl = zahl - 10\n*else*:\n   zahl = zahl + 10",
        category: "BedingteAnweisung",
        calculate: (v) => {
            if (v - 9 > 0) {
                return v - 10;
            } else {
                return v + 10;
            }
        },
    },

    // If-Anweisungen mit ganzzahliger Division

    {
        id: "ifgz-1",
        text: "*if* zahl % 2 == 1:\n   zahl = zahl + 1",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (v % 2 == 1) {
                return v + 1;
            } else {
                return v;
            }
        },
    },
    {
        id: "ifgz-2",
        text: "*if* zahl % 2 == 0:\n   zahl = zahl + 1",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (v % 2 == 0) {
                return Math.trunc(v / 2);
            } else {
                return v;
            }
        },
    },
    {
        id: "ifgz-3",
        text: "*if* zahl // 10 > 2:\n   zahl = 4\n*else*:\n   zahl = 3",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (Math.trunc(v / 10) > 2) {
                return 4;
            } else {
                return 3;
            }
        },
    },
    {
        id: "ifgz-4",
        text: "*if* zahl % 2 == 0:\n   zahl = zahl + 1\n*else*:\n   zahl = zahl - 1",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (mod(v, 2) == 0) {
                return v + 1;
            } else {
                return v - 1;
            }
        },
    },
    {
        id: "ifgz-5",
        text: "*if* zahl % 2 != 0:\n   zahl = zahl + 1\n*else*:\n   zahl = zahl - 1",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (mod(v, 2) != 0) {
                return v + 1;
            } else {
                return v - 1;
            }
        },
    },
    {
        id: "ifgz-6",
        text: "*if* zahl % 3 == 0:\n   zahl = 5",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (mod(v, 3) == 0) {
                return 5;
            } else {
                return v;
            }
        },
    },
    {
        id: "ifgz-7",
        text: "*if* zahl % 3 == 0:\n   zahl = 5\n*elif* zahl % 2 != 0:\n   zahl = 4",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (mod(v, 3) == 0) {
                return 5;
            } else {
                if (mod(v, 2) != 0) {
                    return 4;
                } else {
                    return v;
                }
            }
        },
    },
    {
        id: "ifgz-8",
        text: "*if* zahl % 3 == 0:\n   zahl = 5\n*elif* zahl % 2 != 0:\n   zahl = 4",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (mod(v, 3) == 0) {
                return 5;
            } else {
                if (mod(v, 2) != 0) {
                    return 4;
                } else {
                    return v;
                }
            }
        },
    },
    {
        id: "ifgz-9",
        text: "*if* zahl % 2 == 0:\n   zahl = zahl + 1",
        category: "IfGanzzahlDivision",
        calculate: (v) => {
            if (mod(v, 2) == 0) {
                return v + 1;
            } else {
                return v;
            }
        },
    },

    // If-Anweisungen mit logischen Operatoren
    {
        id: "iflo-1",
        text: "*if* zahl > 0 *and* zahl <= 20:\n   zahl = zahl + 10",
        category: "IfLogischeOperatoren",
        calculate: (v) => {
            if (v > 0 && v <= 20) {
                return v + 10;
            } else {
                return v;
            }
        },
    },
    {
        id: "iflo-2",
        text: "*if* zahl < 5 *and* zahl > 10:\n   zahl = 7",
        category: "IfLogischeOperatoren",
        calculate: (v) => {
            return v;
        },
    },
    {
        id: "iflo-3",
        text: "*if* zahl % 2 == 0 *and* zahl % 3 == 0:\n   zahl = zahl - 1\n*else*:\n   zahl = zahl + 1",
        category: "IfLogischeOperatoren",
        calculate: (v) => {
            if (mod(v, 2) == 0 && mod(v, 3) == 0) {
                return v - 1;
            } else {
                return v + 1;
            }
        },
    },
    {
        id: "iflo-4",
        text: "*if* zahl < 0 *or* zahl > 10:\n   zahl = 4\n*else*:\n   zahl = 2",
        category: "IfLogischeOperatoren",
        calculate: (v) => {
            if (v < 0 || v > 10) {
                return 4;
            } else {
                return 2;
            }
        },
    },
    {
        id: "iflo-5",
        text: "*if* zahl > 5 *or* zahl < 0:\n   zahl = 3\n*else*:\n   zahl = 5",
        category: "IfLogischeOperatoren",
        calculate: (v) => {
            if (v > 5 || v < 0) {
                return 3;
            } else {
                return 5;
            }
        },
    },

    // For-Schleife
    {
        id: "for-1",
        text: "*for* i *in* range(5):\n   zahl = zahl + 1",
        category: "ForSchleife",
        calculate: (v) => {
            return v + 5;
        },
    },
    {
        id: "for-2",
        text: "*for* i *in* range(0,3):\n   zahl = zahl + i",
        category: "ForSchleife",
        calculate: (v) => {
            return v + 3;
        },
    },
    {
        id: "for-3",
        text: "*for* i *in* range(5,8):\n   zahl = zahl - 3",
        category: "ForSchleife",
        calculate: (v) => {
            return v - 9;
        },
    },
    {
        id: "for-4",
        text: "*for* i *in* range(3,0,-1):\n   zahl = zahl - 2",
        category: "ForSchleife",
        calculate: (v) => {
            return v - 6;
        },
    },
    {
        id: "for-5",
        text: "*for* i *in* range(1,3):\n   zahl = zahl - i",
        category: "ForSchleife",
        calculate: (v) => {
            return v - 3;
        },
    },
    {
        id: "for-6",
        text: "*for* i *in* range(5,0,-1):\n   zahl = zahl - 1",
        category: "ForSchleife",
        calculate: (v) => {
            return v - 5;
        },
    },
    {
        id: "for-7",
        text: "*for* i *in* range(4):\n   zahl = zahl - 1",
        category: "ForSchleife",
        calculate: (v) => {
            return v - 4;
        },
    },
    {
        id: "for-8",
        text: "*for* i *in* range(3):\n   zahl = zahl + 2",
        category: "ForSchleife",
        calculate: (v) => {
            return v + 6;
        },
    },
    {
        id: "for-9",
        text: "*for* i *in* range(0,1):\n   zahl = zahl + 2",
        category: "ForSchleife",
        calculate: (v) => {
            return v + 2;
        },
    },

    // While-Schleife
    {
        id: "while-1",
        text: "i = 0\n*while* i < 3:\n   zahl = zahl + 1\n   i = i + 1",
        category: "WhileSchleife",
        calculate: (v) => {
            return v + 3;
        },
    },
    {
        id: "while-2",
        text: "i = 0\n*while* i * i < zahl:\n   i = i + 1\nzahl = zahl + i",
        category: "WhileSchleife",
        calculate: (v) => {
            i = 0;
            while (i * i < v) {
                i = i + 1;
            }
            return v + i;
        },
    },
    {
        id: "while-3",
        text: "i = 0\n*while* 2 * i < zahl:\n   i = i + 1\nzahl = zahl + i",
        category: "WhileSchleife",
        calculate: (v) => {
            i = 0;
            while (2 * i < v) {
                i = i + 1;
            }
            return v + i;
        },
    },
    {
        id: "while-4",
        text: "i = 0\n*while* i < 3:\n   i = i + 1\nzahl = zahl + 2",
        category: "WhileSchleife",
        calculate: (v) => {
            return v + 2;
        },
    },
    {
        id: "while-5",
        text: "*while* zahl > 0:\n   zahl = zahl - 1",
        category: "WhileSchleife",
        calculate: (v) => {
            return 0;
        },
    },
    {
        id: "while-6",
        text: "*while* zahl < 10 and zahl > 0:\n   zahl = zahl - 2",
        category: "WhileSchleife",
        calculate: (v) => {
            while (v < 10 && v > 0) {
                v = v - 2;
            }
            return v;
        },
    },
    {
        id: "while-7",
        text: "i = 5\n*while* i > zahl:\n   zahl = zahl + 2\ni = i - 1",
        category: "WhileSchleife",
        calculate: (v) => {
            i = 5;
            while (i > v) {
                v = v + 2;
            }
            return v;
        },
    },

    // For Schleife mit if-Anweisungen im Schleifenrumpf
    {
        id: "forif-1",
        text: "*for* i *in* range(1,5):\n   if i > 2:\n      zahl = zahl + 1\n*else*:\n      zahl = zahl - 2",
        category: "SchleifenMitIf",
        calculate: (v) => {
            return v - 2;
        },
    },
    {
        id: "forif-2",
        text: "*for* i *in* range(4,8):\n   if i <= 6:\n      zahl = zahl + 1\n*else*:\n      zahl = zahl - 2",
        category: "SchleifenMitIf",
        calculate: (v) => {
            return v + 1;
        },
    },
    {
        id: "forif-3",
        text: "*for* i *in* range(3,7):\n   if i % 2 == 0:\n      zahl = zahl + 2\n*else*:\n      zahl = zahl - 1",
        category: "SchleifenMitIf",
        calculate: (v) => {
            return v + 2;
        },
    },
    {
        id: "forif-4",
        text: "*for* i *in* range(3,7):\n   if i % 2 != 0:\n      zahl = zahl + 2\n*else*:\n      zahl = zahl - 1",
        category: "SchleifenMitIf",
        calculate: (v) => {
            return v + 2;
        },
    },
    {
        id: "forif-5",
        text: "x = 0\n*for* i *in* range(30):\n   if i % 5 == 0:\n      x = x + 1\nzahl = zahl + x",
        category: "SchleifenMitIf",
        calculate: (v) => {
            return v + 6;
        },
    },

    // Listen
    {
        id: "list-1",
        text: "a = [15,12,7,10,5,6,8]\nzahl = a[3]",
        category: "Listen",
        calculate: (v) => {
            return 10;
        },
    },
    {
        id: "list-2",
        text: "a = [15,12,7,10,5,6,8]\nzahl = a[4]",
        category: "Listen",
        calculate: (v) => {
            return 5;
        },
    },
    {
        id: "list-3",
        text: "a = [13,6,10,1,9,8,12]\nzahl = zahl + a[3]",
        category: "Listen",
        calculate: (v) => {
            return v + 1;
        },
    },
    {
        id: "list-4",
        text: "a = [0]\*10\n*for* i *in* range(len(a)):\n   a[i] = i * i\nzahl = a[3]",
        category: "Listen",
        calculate: (v) => {
            return 9;
        },
    },
    {
        id: "list-5",
        text: "a = [1,2,3,4,5]\nx = 0\n*for* i *in* rang(len(a)):\n   x = x + a[i]\nzahl = zahl + x",
        category: "Listen",
        calculate: (v) => {
            return v + 15;
        },
    },
    {
        id: "list-6",
        text: "a = [1,2,3,4,5,6]\nx = 0\n*for* i *in* range(len(a)):\n   *if* i % 2 == 0:\n      x = x + a[i]\nzahl = zahl + x",
        category: "Listen",
        calculate: (v) => {
            return v + 9;
        },
    },
];

// Configuration
const config = {
    totalCards: 5,
    timeToBeat: 15.0,
    showLastNumber: true,
    categories: [
        "Wertzuweisungen",
        "Verwirrung",
        "GanzzahlDivision",
        "BedingteAnweisung",
        "IfGanzzahlDivision",
        "IfLogischeOperatoren",
        "ForSchleife",
        "WhileSchleife",
        "SchleifenMitIf",
        "Listen",
        "ListenOperationen",
    ],
    categoryColors: {
        Anfangskarten: "#3498db",
        Wertzuweisungen: "#2ecc71",
        Verwirrung: "#f1c40f",
        GanzzahlDivision: "#e67e22",
        BedingteAnweisung: "#e74c3c",
        IfGanzzahlDivision: "#9b59b6",
        IfLogischeOperatoren: "#1abc9c",
        ForSchleife: "#34495e",
        WhileSchleife: "#d35400",
        SchleifenMitIf: "#c0392b",
        Listen: "#7f8c8d",
        ListenOperationen: "#27ae60",
    },
};

// State
let state = {
    drawPile: [],
    discardPile: [],
    currentValue: 0,
    startTime: null,
    timerInterval: null,
    isAnimating: false,
    isGameOver: false,
    awaitingInput: false,
    penaltyTime: 0,
    isFirstCorrect: true,
    isGameActive: false,
};

// DOM Elements
const timerDisplay = document.getElementById("timer-display");
const penaltyDisplay = document.getElementById("penalty-display");
const drawPileEl = document.getElementById("draw-pile");
const discardPileEl = document.getElementById("discard-pile");
const stackCount = document.querySelector(".stack-count");
const drawPileBack = drawPileEl.querySelector(".card-back");
const instruction = document.getElementById("instruction");
const resultModal = document.getElementById("result-modal");
const modalTitle = document.getElementById("modal-title");
const modalMessage = document.getElementById("modal-message");
const modalTime = document.getElementById("modal-time");
const modalCongrats = document.getElementById("modal-congrats");
const restartBtn = document.getElementById("restart-game");

// Numpad Elements
const currentInputDisplay = document.getElementById("current-input-display");
const lastNumberDisplay = document.getElementById("last-number-display");
const lastNumberValue = document.getElementById("last-number-value");
const numpadDigitBtns = document.querySelectorAll(".numpad-btn[data-digit]");
const backspaceBtn = document.getElementById("backspace-btn");
const minusBtn = document.getElementById("minus-btn");
const enterBtn = document.getElementById("enter-btn");

let numpadInputValue = "0";
let lastSubmittedNumber = null;
let cardSvgTemplate = "";
let cardStartSvgTemplate = "";

// SVG Templates laden
async function loadSvgTemplate() {
    try {
        const [frontRes, startRes] = await Promise.all([
            fetch(
                document.getElementById("default-link").href +
                    "images/code-mainia-python/" +
                    "card-front.svg",
            ),
            fetch(
                document.getElementById("default-link").href +
                    "images/code-mainia-python/" +
                    "card-start.svg",
            ),
        ]);
        cardSvgTemplate = await frontRes.text();
        cardStartSvgTemplate = await startRes.text();
    } catch (e) {
        console.error("SVG Templates konnten nicht geladen werden", e);
    }
}

function getCardBackground(category) {
    let svg =
        category === "Anfangskarten" ? cardStartSvgTemplate : cardSvgTemplate;
    if (!svg) return "";

    const color = config.categoryColors[category] || "#ffffff";

    // Inject style override into the SVG
    const styleOverride = `<style>
        .st1, .st2 { stroke: ${color} !important; }
        .st54 { fill: ${color} !important; }
    </style>`;

    // Find where the style block starts and inject after it or just after <defs>
    let finalSvg = svg;
    if (svg.includes("</style>")) {
        finalSvg = svg.replace("</style>", `${styleOverride}</style>`);
    } else if (svg.includes("<defs>")) {
        finalSvg = svg.replace("<defs>", `<defs>${styleOverride}`);
    } else {
        finalSvg = svg.replace("<svg", `<svg>${styleOverride}`);
    }

    // Strip XML declaration if present
    finalSvg = finalSvg.replace(/<\?xml.*?\?>/i, "");

    // Convert SVG string to Data URL to use as background-image
    return `url("data:image/svg+xml;charset=utf-8,${encodeURIComponent(finalSvg.trim())}")`;
}

function updateNumpadDisplay() {
    currentInputDisplay.textContent = numpadInputValue;
    if (config.showLastNumber) {
        lastNumberDisplay.classList.remove("hidden");
        lastNumberValue.textContent =
            lastSubmittedNumber !== null ? lastSubmittedNumber : "-";
    } else {
        lastNumberDisplay.classList.add("hidden");
    }
}

function appendDigit(digit) {
    if (!state.awaitingInput) return;
    if (numpadInputValue === "0") {
        numpadInputValue = digit;
    } else if (numpadInputValue === "-0" || numpadInputValue === "-") {
        numpadInputValue = "-" + digit;
    } else {
        numpadInputValue += digit;
    }
    updateNumpadDisplay();
}

function toggleMinus() {
    if (!state.awaitingInput) return;
    if (numpadInputValue.startsWith("-")) {
        numpadInputValue = numpadInputValue.slice(1) || "0";
    } else {
        numpadInputValue =
            "-" + (numpadInputValue === "0" ? "" : numpadInputValue);
    }
    updateNumpadDisplay();
}

function deleteLastChar() {
    if (!state.awaitingInput) return;
    if (
        numpadInputValue.length <= 1 ||
        (numpadInputValue.startsWith("-") && numpadInputValue.length <= 2)
    ) {
        numpadInputValue = "0";
    } else {
        numpadInputValue = numpadInputValue.slice(0, -1);
    }
    updateNumpadDisplay();
}

function showPenalty() {
    state.penaltyTime += 5;
    penaltyDisplay.classList.remove("hidden");
    penaltyDisplay.style.animation = "none";
    penaltyDisplay.offsetHeight;
    penaltyDisplay.style.animation = "penalty-fade 1.5s ease-out forwards";

    setTimeout(() => {
        penaltyDisplay.classList.add("hidden");
    }, 1500);
}

function startGame() {
    // Wenn das Spiel bereits läuft, nichts tun
    if (state.isGameActive && !state.isGameOver) return;

    // Falls das Spiel vorbei ist oder gar nicht läuft, neu initialisieren
    initGame().then(() => {
        state.isGameActive = true;
        state.isGameOver = false;
        state.currentValue = 0;
        state.playedCards = [];
        state.startTime = null;
        state.penaltyTime = 0;
        state.awaitingInput = false;
        numpadInputValue = "0";
        lastSubmittedNumber = null;
        updateNumpadDisplay();

        resultModal.classList.add("hidden");
        discardPileEl.innerHTML = "";
        if (drawPileBack) drawPileBack.classList.remove("hidden");

        // Kleine Verzögerung um Sicherzustellen dass UI bereit ist
        setTimeout(() => drawCard(), 50);
    });
}

function submitNumpadInput() {
    if (!state.isGameActive || state.isGameOver) {
        startGame();
        return;
    }
    if (!state.awaitingInput) return;

    const userVal = parseInt(numpadInputValue) || 0;

    if (userVal === state.currentValue) {
        lastSubmittedNumber = userVal;
        state.awaitingInput = false;
        numpadInputValue = "0";
        updateNumpadDisplay();

        if (state.drawPile.length === 0) {
            endRound();
        } else {
            setTimeout(() => drawCard(), 300);
        }
    } else {
        showPenalty();
        currentInputDisplay.classList.add("wrong");
        setTimeout(() => {
            currentInputDisplay.classList.remove("wrong");
        }, 300);
        numpadInputValue = "0";
        updateNumpadDisplay();
    }
}

function resetNumpad() {
    numpadInputValue = "0";
    updateNumpadDisplay();
}

// Numpad Event Listeners
numpadDigitBtns.forEach((btn) => {
    btn.addEventListener("click", () => appendDigit(btn.dataset.digit));
});
backspaceBtn.addEventListener("click", deleteLastChar);
minusBtn.addEventListener("click", toggleMinus);
enterBtn.addEventListener("click", submitNumpadInput);

// Initialisierung
async function initGame() {
    state.isGameActive = false;
    if (cardSvgTemplate === "") {
        await loadSvgTemplate();
    }
    state = {
        drawPile: [],
        discardPile: [],
        currentValue: 0,
        startTime: null,
        timerInterval: null,
        isAnimating: false,
        isGameOver: false,
        awaitingInput: false,
        penaltyTime: 0,
        isFirstCorrect:
            state.isFirstCorrect !== undefined ? state.isFirstCorrect : true,
    };

    // Bestzeit Initialisierung
    const bestTimeValueEl = document.getElementById("best-time-value");
    if (state.isFirstCorrect) {
        bestTimeValueEl.textContent = config.timeToBeat.toFixed(2);
    }

    // 1. Startkarte auswählen
    const startCards = cards.filter((c) => c.category === "Anfangskarten");
    const startCard = startCards[Math.floor(Math.random() * startCards.length)];

    // 2. Restliche Karten basierend auf Kategorien auswählen
    const otherCards = cards.filter((c) =>
        config.categories.includes(c.category),
    );

    if (otherCards.length < config.totalCards - 1) {
        instruction.classList.remove("hidden");
        instruction.innerHTML = `<span style="color: red; font-weight: bold;">Fehler: Nicht genügend Karten in den gewählten Kategorien vorhanden!</span><br>Benötigt: ${config.totalCards - 1}, Vorhanden: ${otherCards.length}`;
        return;
    }

    // Mischen und die benötigte Anzahl an eindeutigen Karten nehmen
    let pool = [...otherCards].sort(() => Math.random() - 0.5);
    pool = pool.slice(0, config.totalCards - 1);

    // Stapel zusammensetzen (Startkarte oben)
    state.drawPile = [startCard, ...pool];

    // UI Update
    discardPileEl.innerHTML = "";
    if (drawPileBack) drawPileBack.classList.remove("hidden");
    updateStackUI();
    timerDisplay.textContent = "0.00";
    instruction.classList.add("hidden");
    resultModal.classList.add("hidden");
    resetNumpad();
}

function updateStackUI() {
    stackCount.textContent = state.drawPile.length;
}

function startTimer() {
    state.startTime = Date.now();
    state.timerInterval = setInterval(() => {
        const diff = (Date.now() - state.startTime) / 1000 + state.penaltyTime;
        timerDisplay.textContent = diff.toFixed(2);
    }, 50);
}

function getTotalTime() {
    return (Date.now() - state.startTime) / 1000 + state.penaltyTime;
}

function stopTimer() {
    clearInterval(state.timerInterval);
}

function formatCardText(text, category) {
    const isStart = category === "Anfangskarten";
    const color = config.categoryColors[category] || "#000000";
    const textColor = isStart ? "#ffffff" : "#000000";
    const fontSize = isStart ? "11rem" : "1.2rem";

    const formatted = text
        // 1) Nur *...* ersetzen, wenn die * NICHT escaped sind
        .replace(
            /(?<!\\)\*(.*?)(?<!\\)\*/g,
            `<span style="color: ${color}; font-weight: bold;">$1</span>`,
        )
        // 2) Escaped Sterne \* wieder zu normalen * machen
        .replace(/\\\*/g, "*");

    return `<div class="card-text-box" style="color: ${textColor}; font-size: ${fontSize}; text-align: ${isStart ? "center" : "left"}"><div>${formatted}</div></div>`;
}

function adjustFontSize(element) {
    let fontSize = parseFloat(window.getComputedStyle(element).fontSize);
    const maxHeight = element.clientHeight;
    const maxWidth = element.clientWidth;
    const minFontSize = 4;

    // Reduziere Schriftgröße, wenn entweder die Höhe oder die Breite (wegen white-space: pre) überschritten wird
    while (
        (element.scrollHeight > maxHeight || element.scrollWidth > maxWidth) &&
        fontSize > minFontSize
    ) {
        fontSize -= 0.5;
        element.style.fontSize = fontSize + "px";
    }
}

function drawCard() {
    if (state.isAnimating || state.isGameOver || state.awaitingInput) return;

    // Wenn das Spiel noch nicht aktiv ist, starten
    if (!state.isGameActive) {
        startGame();
        return;
    }

    // Wenn der Stapel leer ist, nichts tun
    if (state.drawPile.length === 0) {
        return;
    }

    state.isAnimating = true;

    // Falls erste Karte -> Timer starten
    if (state.startTime === null) {
        startTimer();
    }

    // Wenn nur noch eine Karte übrig ist (die gerade gezogen wird), Rückseite verstecken
    if (state.drawPile.length === 1 && drawPileBack) {
        drawPileBack.classList.add("hidden");
    }

    const cardData = state.drawPile.shift();

    // Bei der ersten Karte (Startkarte) wird der Wert direkt gesetzt
    if (state.discardPile.length === 0) {
        state.currentValue = cardData.calculate(0);
    } else {
        state.currentValue = cardData.calculate(state.currentValue);
    }

    // Animation erstellen
    const cardEl = document.createElement("div");
    cardEl.className = "card moving";

    const front = document.createElement("div");
    front.className = "card card-front";
    front.innerHTML = formatCardText(cardData.text, cardData.category);
    front.style.display = "flex";
    front.style.backgroundImage = getCardBackground(cardData.category);

    const back = document.createElement("div");
    back.className = "card card-back";

    cardEl.appendChild(front);
    cardEl.appendChild(back);

    drawPileEl.appendChild(cardEl);
    const textBox = front.querySelector(".card-text-box");
    if (textBox) adjustFontSize(textBox);

    cardEl.offsetHeight;

    const rect1 = drawPileEl.getBoundingClientRect();
    const rect2 = document
        .getElementById("discard-pile")
        .getBoundingClientRect();
    const scale =
        parseFloat(
            getComputedStyle(document.documentElement).getPropertyValue(
                "--game-scale",
            ),
        ) || 1;
    const offset = state.discardPile.length * 2;
    const x = (rect2.left - rect1.left) / scale;
    const y = (rect2.top - rect1.top + offset * scale) / scale;

    cardEl.style.setProperty("--move-x", `${x}px`);
    cardEl.style.setProperty("--move-y", `${y}px`);

    updateStackUI();

    setTimeout(() => {
        cardEl.classList.add("moving");
    }, 10);

    setTimeout(() => {
        const finalCard = document.createElement("div");
        finalCard.className = "card flipped-final";
        const f_front = document.createElement("div");
        f_front.className = "card-front";
        f_front.innerHTML = formatCardText(cardData.text, cardData.category);
        f_front.style.backgroundImage = getCardBackground(cardData.category);

        const f_back = document.createElement("div");
        f_back.className = "card-back";
        finalCard.appendChild(f_front);
        finalCard.appendChild(f_back);

        state.discardPile.push(cardData);
        cardEl.remove();
        discardPileEl.appendChild(finalCard);

        const textBox = finalCard.querySelector(".card-text-box");
        if (textBox) adjustFontSize(textBox);

        state.isAnimating = false;
        state.awaitingInput = true;
    }, 450);
}

function endRound() {
    state.isGameOver = true;
    state.awaitingInput = false;
    stopTimer();

    const totalTime = getTotalTime();
    const bestTimeValueEl = document.getElementById("best-time-value");
    const bestTimeDisplayEl = document.getElementById("best-time-display");
    let currentBest = parseFloat(bestTimeValueEl.textContent);

    modalTitle.textContent = "Runde beendet!";
    modalMessage.textContent = "Alle Karten richtig berechnet!";
    modalTime.textContent = totalTime.toFixed(2) + "s";
    modalCongrats.textContent = "";

    const encouragingMessages = [
        "Fast geschafft! Bleib dran!",
        "Ein bisschen Übung noch, dann knackst du die Zeit!",
        "Guter Versuch! Nächstes Mal bist du schneller!",
        "Nicht aufgeben, du wirst immer besser!",
        "Das war knapp! Noch eine Runde?",
        "Kopf hoch! Jeder Meister hat mal klein angefangen.",
    ];

    if (totalTime <= config.timeToBeat) {
        if (state.isFirstCorrect || totalTime < currentBest) {
            if (state.isFirstCorrect) {
                modalCongrats.textContent = "Zeit geschlagen! Erste Bestzeit!";
                state.isFirstCorrect = false;
            } else {
                modalCongrats.textContent = "Neue Bestzeit!";
            }
            bestTimeValueEl.textContent = totalTime.toFixed(2);
            bestTimeDisplayEl.innerHTML = `Beste Zeit: <span id="best-time-value">${totalTime.toFixed(2)}</span>s`;
        } else {
            modalCongrats.textContent = "Gute Zeit!";
        }
    } else {
        const randomMsg =
            encouragingMessages[
                Math.floor(Math.random() * encouragingMessages.length)
            ];
        modalCongrats.textContent = randomMsg;
        modalCongrats.style.color = "#e67e22"; // Orange für Aufmunterung
    }

    resultModal.classList.remove("hidden");
}

restartBtn.addEventListener("click", initGame);

drawPileEl.addEventListener("click", drawCard);

// Keyboard support
document.addEventListener("keydown", (e) => {
    if (e.key === "ArrowRight") {
        drawCard();
    }

    // Numpad keyboard support
    if (e.key >= "0" && e.key <= "9") {
        appendDigit(e.key);
        e.preventDefault();
    } else if (e.key === "-") {
        toggleMinus();
        e.preventDefault();
    } else if (e.key === "Backspace") {
        deleteLastChar();
        e.preventDefault();
    } else if (e.key === "Enter") {
        if (state.awaitingInput) {
            submitNumpadInput();
        } else {
            startGame();
        }
        e.preventDefault();
    }
});

// Start
window.addEventListener("resize", updateScale);
function updateScale() {
    const container = document.getElementById("game-container");
    const padding = 20;
    const originalWidth = 800;
    const originalHeight = 600; // Schätzwert für die ursprüngliche Höhe

    const scaleX = (window.innerWidth - padding) / originalWidth;
    const scaleY = (window.innerHeight - padding) / originalHeight;
    const scale = Math.min(scaleX, scaleY * 0.8) * 1.5; // 150% Platznutzung

    document.documentElement.style.setProperty("--game-scale", scale);
}
updateScale();

initGame();
