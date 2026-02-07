Mit diesem Task-View können Relationenalgebra Aufgaben gestellt werden. Dafür werden die Tabellen wir in der Aufgabenbeschreibung angelegt. Aus diesen wird automatisch die Sturktur extrahiert und in einem Tab angezeigt (anklickbar, dann wird der angeklickte Name in den momentanen Editor übernommen).

Die Aufgaben werden automatisch geprüft, der Taskview aber nicht automatisch als Vollständig markiert.

#### Bsp. Aufgabenbeschreibung:
Die Aufgaben wie folgt codiert:
```
{
    "version": "1.0",
    "tables": {
        "L": {
            "columns": [
                { "name": "LNr", "type": "string" },
                { "name": "LName", "type": "string" },
                { "name": "Stadt", "type": "string" },
                { "name": "km", "type": "number" }
            ],
            "rows": [
                ["S1", "Smith", "London", 20],
                ["S2", "Jones", "Paris", 10],
                ["S3", "Blake", "Paris", 30],
                ["S4", "Clark", "London", 20],
                ["S5", "Adams", "Athens", 30]
            ]
        },
        "T": {
            "columns": [
                { "name": "TNr", "type": "string" },
                { "name": "TName", "type": "string" },
                { "name": "Farbe", "type": "string" },
                { "name": "Gewicht", "type": "number" },
                { "name": "Lagerort", "type": "string" }
            ],
            "rows": [
                ["P1", "Nut", "Red", 12, "London"],
                ["P2", "Bolt", "Green", 17, "Paris"],
                ["P3", "Screw", "Blue", 17, "Rome"],
                ["P4", "Screw", "Red", 14, "London"],
                ["P5", "Cam", "Bllue", 12, "Paris"],
                ["P6", "Cog", "Red", 19, "London"]
            ]
        },
        "Liefert": {
            "columns": [
                { "name": "LNr", "type": "string" },
                { "name": "Tor", "type": "string" },
                { "name": "Menge", "type": "number" }
            ],
            "rows": [
                ["S1", "P1", 200],
                ["S1", "P2", 200],
                ["S2", "P3", 400],
                ["S3", "P3", 200],
                ["S3", "P4", 500],
                ["S4", "P6", 300],
                ["S5", "P2", 200]
            ]
        }
    },
    "tasks": [
        {
            "id": "task-1",
            "title": "Aufgabe 1",
            "description": "Bestimme die identifizierende Nummer und die Entfernung (in km) jedes Lieferanten aus Paris. \\**Hinweis:** Beim Vergleichen eines Attributs mit einem String muss der String in einfache Anführungszeichen.\nBeispiel: LName='Smith'",
            "defaultCode": "",
            "solutionCode": "pi LNr,km ( sigma Stadt='Paris' (L))",
            "showLKOperations": false,
            "validation": {
                "orderMatters": false,
                "extraColumnsAllowed": false,
                "columnNamesMustMatch": true
            }
        },
        {
            "id": "task-2",
            "title": "Aufgabe 2",
            "description": "Zeige alle Teile!",
            "defaultCode": "",
            "solutionCode": "pi TNr (T)",
            "showLKOperations": false,
            "validation": {
                "orderMatters": false,
                "extraColumnsAllowed": true,
                "columnNamesMustMatch": false
            }
        }
    ]
}
```
**tables**: Die Tabellen, auf denen die Aufgaben basieren.<br>
**tasks**: Array mit Aufgaben<br>
**id**, **title**: ID und angezeigter Titel der Aufgabe<br>
**description**: Enthält die Aufgabenbeschreibung in marked.js Notation. Mit `>!{Lösung anzeigen}` erzeugt man ein Spoiler Tag (versteckter aufklappbarer Inhalt), auf dem der Text in geschweiften Klammern steht (`>!` führt zu "Tipp"). Der Text bis zum Ende bzw. bis zum nächsten `>!` wird versteckt. Es ist möglich mehrere zu haben.<br>
**defaultCode**: Ein vorgegebnener Code im Editor, z.B. um Fehler suchen zu lassen.<br>
**solutionCode**: Ein Code, der zur Lösung führt, wenn **extraColumnsAllowed** gesetzt ist, muss dieser die minimalste Lösung liefern. Es werden die berechneten Tabellen verglichen, nicht der String.<br>
**showLXOperations**: Zeigt die nur im Leistungskurs zu behandelnden Operationen als anklickbare Felder über dem Editor an. Benutzbar sind diese immer.<br>
**orderMatters**: Ob die Reihenfolge, in der die Tabelle der oben hinterlegten Lösung entsprechen muss geprüft wird.<br>
**extraColumnsAllowed**: Ob die Lösung des/der SchülerIn mehr Spalten haben darf, als die Lösung des **solutionCode**<br>
**columnNamesMustMatch**: Ob auch die Spaltennamen exakt mit der Lösung übereinstimmen müssen. (Nur LK hat Namensänderungen.)

#### Bsp. Tutorial
```
[
    {
        "title": "HTML Grundlagen",
        "content": "# Tutorial 1:/nHTML Grundlagen\n\nText"
    },
    {
        "title": "CSS Grundlagen",
        "content": "# Tutorial 2"
    },
    {
        "title": "Layout mit CSS",
        "content": "# Tutorial 3"
    }
]
```
**title**: Wird momentan nicht benutzt.<br>
**content**: Inhalt einer Tutorialseite als marked.js Text.

#### Bsp. Standard-Vorlage
wird nicht unterstützt