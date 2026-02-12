SQL Aufgaben mit mehreren Teilaufgaben und eigener Datenbank (clientseitig via SQL.js).

#### Bsp. Aufgabenbeschreibung (task.description)
Die Aufgabenbeschreibung muss ein JSON-String sein:
```
{
  "version": "1.0",
  "database": {
    "type": "sql",
    "content": "CREATE TABLE foo (id INT); INSERT INTO foo VALUES (1);"
  },
  "erd_image": "https://example.org/erd.png",
  "relationshipModel": "TABLE foo(_id)",
  "tasks": [
    {
      "id": "task-1",
      "title": "Aufgabe 1",
      "description": "## Ziel\n\nSchreibe ein SELECT.",
      "defaultCode": "SELECT * FROM foo;",
      "solutionCode": "SELECT * FROM foo;",
      "validation": {
        "orderMatters": false,
        "extraColumnsAllowed": false,
        "columnNamesMustMatch": true
      }
    }
  ]
}
```

**database.type**:
- `sql`: SQL-Init-Script
- `base64`: Base64-kodierte SQLite-Datei
- `url`: URL zu einer SQLite-Datei (CORS muss erlaubt sein)

**tasks**: Array mit Aufgaben<br>
- `id`, `title`: ID und angezeigter Titel der Aufgabe<br>
- `description`: Enthält die Aufgabenbeschreibung in marked.js Notation. Mit `>!{Lösung anzeigen}` erzeugt man ein Spoiler Tag (versteckter aufklappbarer Inhalt), auf dem der Text in geschweiften Klammern steht (`>!` führt zu "Tipp"). Der Text bis zum Ende bzw. bis zum nächsten `>!` wird versteckt. Es ist möglich mehrere zu haben.
- `defaultCode`: Ein vorgegebnener Code im Editor, z.B. um Fehler suchen zu lassen.<br>
- `solutionCode`: Ein Code, der zur Lösung führt, wenn `extraColumnsAllowed` gesetzt ist, muss dieser die minimalste Lösung liefern. Es werden die berechneten Tabellen verglichen, nicht der String.<br>

**validation**:
- `orderMatters`: Reihenfolge muss stimmen
- `extraColumnsAllowed`: zusaetzliche Spalten erlaubt
- `columnNamesMustMatch`: Spaltennamen muessen passen

#### Bsp. Tutorial (task.tutorial)
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