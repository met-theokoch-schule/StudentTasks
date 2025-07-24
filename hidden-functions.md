
# Hidden Debug Functions

## Content Viewer

Ein Debug-Tool zum Anzeigen gespeicherter TaskContent-Daten.

### Verwendung

#### Web-Interface
- **URL:** `/debug/content/{userId}/{taskId}`
- **URL mit Version:** `/debug/content/{userId}/{taskId}?version={versionNumber}`

**Beispiele:**
- `/debug/content/1/3` - Zeigt den neuesten Content von User 1 für Task 3
- `/debug/content/1/3?version=2` - Zeigt Version 2 des Contents von User 1 für Task 3

#### API-Endpoints (Raw Content)
- **URL:** `/debug/api/content/{userId}/{taskId}`
- **URL mit Version:** `/debug/api/content/{userId}/{taskId}?version={versionNumber}`
- **Response:** Plain Text (Content-String)

**Beispiele:**
- `/debug/api/content/1/3` - Gibt den rohen Content-String zurück
- `/debug/api/content/1/3?version=2` - Gibt Version 2 des rohen Content-Strings zurück

### Features

1. **Versions-Übersicht:** Zeigt alle verfügbaren Versionen mit Zeitstempel und Abgabe-Status
2. **Content-Anzeige:** Formatierte Darstellung des gespeicherten Contents
3. **Raw-Download:** Direkter Zugriff auf den rohen Content-String
4. **Copy-to-Clipboard:** Schnelles Kopieren des Contents
5. **Metadaten:** Anzeige von User-, Task- und UserTask-Informationen

### Zweck

Dieses Tool ist besonders nützlich für:
- **Erstellen von Default Submissions:** Einfaches Kopieren gespeicherter Schüler-Lösungen
- **Debugging:** Überprüfung was tatsächlich gespeichert wurde
- **Content-Analyse:** Untersuchung der Content-Struktur verschiedener Task Views
- **Versionsverfolgung:** Nachvollziehen von Änderungen an Submissions

### Sicherheitshinweis

⚠️ **WICHTIG:** Dieser Controller ist nur für Debug-Zwecke gedacht und sollte in Produktionsumgebungen nicht zugänglich sein. Er bypassed alle Sicherheitsprüfungen und gibt sensible Daten preis.
