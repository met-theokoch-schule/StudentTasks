# SQL-TaskView Dokumentation

Diese Dokumentation beschreibt den Aufbau einer Aufgabe fuer den SQL-TaskView und wie die Datenbank uebergeben wird.

## Einstiegspunkt

- Template: `src/main/resources/templates/taskviews/sql-task-view.html`
- Logik: `src/main/resources/static/js/sql-task-view.js`
- Renderer: `src/main/resources/static/js/sql-task-renderer.js`
- Validator: `src/main/resources/static/js/sql-validator.js`

## Aufgabe: Pflichtstruktur (task.description)

Der SQL-TaskView liest die Aufgaben aus `task.description` (JSON-String). Dieser JSON-String wird in `#description` abgelegt und muss gueltiges JSON sein.

### Minimaler Aufbau

```json
{
  "version": "1.0",
  "database": {
    "type": "sql",
    "content": "CREATE TABLE foo (id INT);"
  },
  "tasks": [
    {
      "id": "task-1",
      "title": "Aufgabe 1",
      "description": "SQL-Text in Markdown",
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

### Felder im Root-Objekt

- `version` (String, optional): Freies Versionslabel. Wenn nicht gesetzt, wird nur ein Warnhinweis geloggt.
- `database` (Object, optional): Datenbank-Konfiguration, siehe unten.
- `tasks` (Array, **pflicht**): Liste der Aufgaben.
- `erd_image` (String, optional): URL zu einem ERD-Bild. Wenn gesetzt, wird der ERD-Tab eingeblendet.
- `relationshipModel` (String, optional): Relationenmodell als Text oder Markup. Wenn gesetzt, wird es im ERD-Tab gerendert.

### Felder pro Task (tasks[])

- `id` (String, **pflicht**): Eindeutige ID pro Aufgabe. Wird fuer Editor und Status-Tracking genutzt.
- `title` (String, optional): Titel der Aufgabe.
- `description` (String, optional): Markdown-Text (Spoiler via `>!` wird als `<details>` gerendert).
- `defaultCode` (String, optional): Initialer Code. Wenn gesetzt, erscheint ein "Zuruecksetzen"-Button.
- `solutionCode` (String, optional): Loesungscode fuer die Validierung.
- `validation` (Object, optional): Validierungsregeln. Wenn `solutionCode` oder `validation` fehlt, findet keine Korrektur statt.

### Validierungsregeln (validation)

- `orderMatters` (Boolean): Wenn `true`, muss die Ergebnisreihenfolge exakt stimmen.
- `extraColumnsAllowed` (Boolean): Wenn `false`, sind zusaetzliche Spalten ein Fehler.
- `columnNamesMustMatch` (Boolean): Wenn `true`, muessen Spaltennamen passen.

## Datenbank uebergeben (task.description.database)

Der SQL-TaskView initialisiert die Datenbank clientseitig mit SQL.js. Die Konfiguration erfolgt in `task.description.database`.

### Variante A: SQL-Init-Script

```json
"database": {
  "type": "sql",
  "content": "CREATE TABLE foo (id INT); INSERT INTO foo VALUES (1);"
}
```

- `type: "sql"`
- `content`: SQL-Statements, die direkt mit `db.exec(...)` ausgefuehrt werden.
- Mehrere Statements sind erlaubt (z.B. CREATE + INSERT).

### Variante B: SQLite-DB als Base64

```json
"database": {
  "type": "base64",
  "content": "UEsDBBQAAAA..." 
}
```

- `type: "base64"`
- `content`: Base64-kodierte SQLite-Datei (`.db`).
- Die Datei wird in Bytes dekodiert und mit `new SQL.Database(bytes)` geladen.

### Variante C: SQLite-DB per URL

```json
"database": {
  "type": "url",
  "content": "https://example.org/data/my-db.sqlite"
}
```

- `type: "url"`
- `content`: Vollstaendige URL zur SQLite-Datei (`.db`).
- Die Datei wird im Browser via `fetch` geladen und als `ArrayBuffer` an SQL.js uebergeben.
- Voraussetzung: Der externe Server muss CORS erlauben.

### Verhalten ohne `database`

Wenn `database` fehlt, wird eine leere In-Memory-DB erzeugt.

## Tutorial (task.tutorial)

Das Tutorial wird **nicht** in `task.description` gelesen, sondern aus `task.tutorial` (Hidden-DIV `#tutorial`).

Erlaubte Formate:

- JSON-Array (per `new Function` geparst), z.B.:

```json
[
  { "content": "# Seite 1" },
  { "content": "# Seite 2", "image": "https://.../erd.png" }
]
```

- Reiner Markdown-String (wird als eine Seite behandelt).

Wenn das Tutorial (oder `erd_image`/`relationshipModel` aus `task.description`) Inhalte hat, wird der ERD-Tab angezeigt.

## Gespeicherter Content (Task-Content)

Beim Speichern wird folgender JSON-String erzeugt:

```json
{
  "version": "1.0",
  "tasks": [
    {
      "id": "task-1",
      "code": "SELECT * FROM foo;",
      "status": "correct",
      "lastExecuted": "2024-01-01T12:00:00.000Z",
      "attempts": 2
    }
  ],
  "currentTutorialIndex": 0,
  "metadata": {
    "totalTasks": 1,
    "completedTasks": 1,
    "lastSaved": "2024-01-01T12:00:00.000Z",
    "taskOrder": ["task-1"]
  }
}
```

Dieser String wird an den API-Endpunkt `/api/tasks/{taskId}/content` bzw. `/api/tasks/usertasks/{userTaskId}/content` gesendet.
