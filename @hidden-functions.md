## Debug Content Viewer

### Zweck
Ermöglicht das Anzeigen des gespeicherten Inhalts von Submissions für den aktuell eingeloggten Benutzer zu Debug-Zwecken.

### URL-Format
```
/debug/content/{taskId}?version={versionNumber}
```

### Parameter
- `taskId` (Pfad): ID der Aufgabe  
- `version` (Query, optional): Spezifische Versionsnummer. Wenn nicht angegeben, wird die neueste Version angezeigt

### Beispiele
```
# Neueste Version für Task 10 (aktueller Benutzer)
/debug/content/10

# Spezifische Version 3 für Task 10 (aktueller Benutzer)
/debug/content/10?version=3
```

### Funktionalität
- Zeigt den rohen Inhalt einer Submission des aktuell eingeloggten Benutzers an
- Verwendet automatisch den aktuell authentifizierten Benutzer
- Validiert Task-Existenz und Benutzer-Anmeldung
- Unterstützt sowohl neueste als auch spezifische Versionen
- Zeigt Metadaten wie Submission-Zeit und Version an

### Sicherheit
- Nur eingeloggte Benutzer können ihre eigenen Submissions einsehen
- ⚠️ **Nur für Entwicklung** - Diese Funktion ist nirgends verlinkt und sollte nur in der Entwicklungsumgebung verwendet werden.