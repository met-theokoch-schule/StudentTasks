
# TaskView Entwicklungsanleitung

## Übersicht

TaskViews sind spezielle Thymeleaf-Templates, die verschiedene Aufgabentypen in unserem Student-Task-System darstellen. Jeder TaskView definiert, wie eine Aufgabe dem Schüler präsentiert wird und wie Inhalte gespeichert und abgegeben werden.

## Anforderungen an einen TaskView

### 1. Template-Struktur

Ein TaskView muss folgende grundlegende Struktur haben:

```html
<!DOCTYPE html>
<html lang="de" xmlns:th="http://www.thymeleaf.org">
<head>
    <!-- Standard Meta-Tags und CSS -->
</head>
<body>
    <!-- TaskView-spezifische Implementierung -->
</body>
</html>
```

### 2. Pflicht-Funktionalitäten

Jeder TaskView **MUSS** folgende Funktionen implementieren:

#### a) Content-Speicherung
- JavaScript-Funktion `saveContent(isSubmission = false)`
- Senden des Inhalts via POST an `/api/tasks/${taskId}/content`
- Explizite Speicherung durch Benutzeraktion

#### b) Content-Abgabe
- JavaScript-Funktion `submitTask()`
- Senden des Inhalts via POST an `/api/tasks/${taskId}/submit`
- Bestätigungsdialog vor Abgabe

#### c) Status-Anzeige
- Visuelles Feedback für Speicher-/Abgabe-Status
- Auto-Save-Indikator

#### d) Responsive Design
- Bootstrap 5 kompatibel
- Mobile-first Ansatz

## Verfügbare Thymeleaf-Attribute

### Aufgaben-Informationen

```html
<!-- Aufgaben-Grunddaten -->
${task.id}                    <!-- Long: Eindeutige Aufgaben-ID -->
${task.title}                 <!-- String: Aufgabentitel -->
${task.description}           <!-- String: Aufgabenbeschreibung (HTML) -->
${task.dueDate}              <!-- LocalDateTime: Fälligkeitsdatum -->
${task.isActive}             <!-- Boolean: Ist Aufgabe aktiv? -->
${task.createdAt}            <!-- LocalDateTime: Erstellungsdatum -->
${task.lastModified}         <!-- LocalDateTime: Letzte Änderung -->
${task.defaultSubmission}    <!-- String: Standard-Abgabetext -->

<!-- Aufgaben-Beziehungen -->
${task.taskView.id}          <!-- Long: TaskView-ID -->
${task.taskView.name}        <!-- String: TaskView-Name -->
${task.creator.name}         <!-- String: Ersteller-Name -->
${task.unit.name}            <!-- String: Unterrichtseinheit -->
${task.unit.description}     <!-- String: Einheitsbeschreibung -->
```

### Benutzer-Aufgaben-Status

```html
<!-- UserTask-Informationen -->
${userTask.id}               <!-- Long: UserTask-ID -->
${userTask.status.name}      <!-- String: Status (NICHT_BEGONNEN, IN_BEARBEITUNG, ABGEGEBEN, etc.) -->
${userTask.createdAt}        <!-- LocalDateTime: Erstellungsdatum -->
${userTask.lastModified}     <!-- LocalDateTime: Letzte Änderung -->

<!-- Benutzer-Informationen -->
${userTask.user.id}          <!-- Long: Benutzer-ID -->
${userTask.user.name}        <!-- String: Benutzername -->
${userTask.user.email}       <!-- String: E-Mail-Adresse -->
```

### Content-Daten

```html
<!-- Aktueller Inhalt -->
${currentContent}            <!-- String: Aktueller Aufgabeninhalt -->
${content}                   <!-- String: Alias für currentContent -->

<!-- Versions-Informationen -->
${viewingVersion}            <!-- Integer: Angezeigte Version -->
${isHistoryView}             <!-- Boolean: Ist Historie-Ansicht? -->
```

### Spezielle Flags

```html
<!-- Lehrer-/Schüler-Kontext -->
${isTeacherView}             <!-- Boolean: Ist Lehrer-Ansicht? -->
${isIframe}                  <!-- Boolean: Wird in iFrame angezeigt? -->

<!-- Berechtigungen -->
${canEdit}                   <!-- Boolean: Darf bearbeitet werden? -->
${isSubmitted}               <!-- Boolean: Bereits abgegeben? -->
```

## JavaScript-API Spezifikation

### Pflicht-Funktionen

```javascript
// Content speichern
function saveContent(isSubmission = false) {
    const content = getContentFromView(); // TaskView-spezifisch
    
    const url = isSubmission ? 
        `/api/tasks/${taskId}/submit` : 
        `/api/tasks/${taskId}/content`;
    
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: content })
    })
    .then(response => {
        if (response.ok) {
            updateSaveStatus(isSubmission ? 'submitted' : 'saved');
        } else {
            updateSaveStatus('error');
        }
    })
    .catch(error => updateSaveStatus('error'));
}

// Aufgabe abgeben
function submitTask() {
    if (confirm('Möchten Sie diese Aufgabe wirklich abgeben?')) {
        saveContent(true);
    }
}

// Status-Anzeige aktualisieren
function updateSaveStatus(status) {
    const statusElement = document.getElementById('save-status');
    switch (status) {
        case 'saved': /* Gespeichert */ break;
        case 'saving': /* Speichert... */ break;
        case 'error': /* Fehler */ break;
        case 'submitted': /* Abgegeben */ break;
        case 'ready': /* Bereit zum Speichern */ break;
    }
}
```

### TaskView-spezifische Funktionen

```javascript
// MUSS implementiert werden - Content aus dem View extrahieren
function getContentFromView() {
    // Beispiel für simple-text:
    return document.getElementById('submission-content').value;
    
    // Beispiel für complex content:
    return JSON.stringify({
        text: document.getElementById('text-field').value,
        selections: getSelections(),
        metadata: getMetadata()
    });
}

// MUSS implementiert werden - Content in den View laden
function loadContentToView(content) {
    // Beispiel für simple-text:
    document.getElementById('submission-content').value = content;
    
    // Beispiel für complex content:
    const data = JSON.parse(content);
    document.getElementById('text-field').value = data.text;
    loadSelections(data.selections);
    loadMetadata(data.metadata);
}
```

## Content-Serialisierung

### Einfacher Content (Text)
```javascript
// Direkte String-Speicherung
const content = document.getElementById('textarea').value;
```

### Komplexer Content (JSON)
```javascript
// Strukturierte Daten als JSON
const content = JSON.stringify({
    version: "1.0",
    type: "math-exercise",
    answers: {
        question1: "42",
        question2: ["a", "c"]
    },
    workingSteps: [
        { step: 1, formula: "x = a + b" },
        { step: 2, formula: "x = 3 + 4" }
    ],
    metadata: {
        timeSpent: 1800, // Sekunden
        attempts: 3
    }
});
```

## CSS-Klassen und Styling

### Standard Bootstrap-Klassen
```css
/* Verwendete Bootstrap-Komponenten */
.container, .row, .col-*     /* Layout */
.card, .card-header, .card-body /* Karten */
.btn, .btn-primary, .btn-success /* Buttons */
.form-control, .form-label   /* Formulare */
.alert, .badge              /* Feedback */
```

### TaskView-spezifische Klassen
```css
/* Standard-IDs für JavaScript */
#submission-content         /* Haupt-Content-Element */
#save-status               /* Status-Anzeige */
#saveButton, #submitButton /* Action-Buttons */

/* Standard-Klassen */
.task-info-sidebar        /* Aufgaben-Info */
.submission-area          /* Abgabe-Bereich */
.save-status             /* Speicher-Status */
```

## Lehrer-Integration

### iFrame-Unterstützung
```javascript
// Benachrichtigung an Parent-Window
if (window.parent && window.parent !== window) {
    window.parent.postMessage('content-saved', '*');
}

// UserTask-spezifische Speicherung im Lehrer-Modus
const isTeacherView = [[${isTeacherView ?: false}]];
const userTaskId = [[${userTask.id}]];

if (isTeacherView && userTaskId) {
    saveUrl = `/api/usertasks/${userTaskId}/content`;
}
```

### Read-Only Modus
```javascript
// Bearbeitung deaktivieren bei abgegebenen Aufgaben
const userTaskStatus = [[${userTask.status?.name}]];
if (userTaskStatus === 'VOLLSTÄNDIG') {
    disableEditing();
}
```

## Accessibility

### Pflicht-Attribute
```html
<!-- Aria-Labels für Screen Reader -->
<label for="submission-content">Deine Antwort:</label>
<textarea id="submission-content" 
          aria-describedby="content-help"
          aria-required="true">
</textarea>

<!-- Keyboard-Navigation -->
<button type="button" accesskey="s" onclick="saveContent()">
    Speichern (Alt+S)
</button>
```

## Performance-Richtlinien

### Memory Management
```javascript
// Event Listener cleanup bei Bedarf
window.addEventListener('beforeunload', function() {
    // Cleanup-Aktionen falls erforderlich
});
```

## Testing

### Pflicht-Tests für jeden TaskView
1. **Content-Speicherung**: Kann Inhalt manuell gespeichert werden?
2. **Content-Ladung**: Wird gespeicherter Inhalt korrekt geladen?
3. **Explizite Speicherung**: Funktioniert manuelle Speicherung?
4. **Abgabe**: Kann Aufgabe abgegeben werden?
5. **Status-Updates**: Werden Status-Änderungen angezeigt?
6. **Mobile-Ansicht**: Funktioniert auf kleinen Bildschirmen?
7. **Lehrer-Integration**: Funktioniert iFrame-Modus?

## Deployment

### Template-Registrierung
```properties
# In taskviews.properties
taskview.my-custom-view.name=Mein Custom View
taskview.my-custom-view.description=Beschreibung des Views
taskview.my-custom-view.template=taskviews/my-custom-view.html
taskview.my-custom-view.active=true
```

### Datenbankeinträge
```sql
-- TaskView in Datenbank registrieren
INSERT INTO task_views (name, description, template_path, is_active) 
VALUES ('Mein Custom View', 'Beschreibung', 'taskviews/my-custom-view.html', true);
```

## Best Practices

1. **Versionierung**: Immer Version-Feld in JSON-Content einbauen
2. **Fallback**: Graceful Degradation bei fehlenden Features
3. **Validation**: Client- und Server-seitige Validierung
4. **Feedback**: Klares visuelles Feedback für alle Aktionen
5. **Dokumentation**: Inline-Kommentare für komplexe Logik

## Fehlerbehebung

### Häufige Probleme
- **TaskId undefined**: Thymeleaf-Variablen prüfen
- **Speicherung funktioniert nicht**: API-Endpoints überprüfen
- **iFrame-Kommunikation**: PostMessage-Events kontrollieren
- **Status nicht aktualisiert**: DOM-Elemente validieren

### Debug-Ausgaben
```javascript
console.log('🔍 TaskId from Thymeleaf:', taskId);
console.log('📝 Content to save:', content);
console.log('✅ Save successful');
```
