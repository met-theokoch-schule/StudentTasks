
# Plan: Überarbeitete Gruppenansicht - Schüler-Aufgaben-Matrix

## Ziel
Erstelle eine übersichtliche Matrix-Tabelle, die auf einen Blick den Status aller Aufgaben für alle Schüler einer Gruppe zeigt.

## Design-Anforderungen
1. **Matrix-Layout**: 
   - Zeile 1: Header mit Aufgabennamen (um 90° gedreht)
   - Spalte 1: Schülernamen
   - Zellen: Status-Icons für jede Schüler-Aufgabe-Kombination

2. **Responsive Design**:
   - Scrollbar horizontal und vertikal
   - Fixierte erste Zeile (sticky header)
   - Fixierte erste Spalte (sticky left column)
   - Maximale Höhe für Aufgabennamen mit Kürzung (...)

3. **Status-Icons** (FontAwesome, konsistent mit Student Dashboard):
   - `NICHT_BEGONNEN`: `fas fa-circle` (grau)
   - `IN_BEARBEITUNG`: `fas fa-edit` (blau)
   - `ABGEGEBEN`: `fas fa-hourglass-half` (orange/warning)
   - `ÜBERARBEITUNG_NÖTIG`: `fas fa-redo` (rot)
   - `VOLLSTÄNDIG`: `fas fa-check-circle` (grün)

## Datenstruktur - Was brauchen wir?

### Controller-Daten
```java
// Neue Datenstruktur für Matrix-View
public class StudentTaskMatrix {
    private List<User> students;           // Alle Schüler der Gruppe
    private List<Task> tasks;              // Alle aktiven Aufgaben der Gruppe
    private Map<String, UserTaskStatus> statusMap;  // Key: "studentId_taskId", Value: Status
}

public class UserTaskStatus {
    private TaskStatus status;
    private boolean hasSubmissions;
    private Long userTaskId;  // Für direkten Link zur Bearbeitung
}
```

### Service-Methode
```java
// In GroupService
public StudentTaskMatrix getStudentTaskMatrix(Group group, User teacher) {
    List<User> students = // Alle Schüler der Gruppe
    List<Task> tasks = // Alle aktiven Aufgaben des Lehrers für diese Gruppe
    Map<String, UserTaskStatus> statusMap = // Status-Matrix aufbauen
    
    return new StudentTaskMatrix(students, tasks, statusMap);
}
```

## Template-Implementation

### CSS-Anforderungen
```css
.task-matrix-container {
    position: relative;
    max-height: 70vh;
    overflow: auto;
    border: 1px solid #dee2e6;
}

.task-matrix {
    min-width: 100%;
    border-collapse: separate;
    border-spacing: 0;
}

.matrix-header {
    position: sticky;
    top: 0;
    background: #f8f9fa;
    z-index: 10;
}

.matrix-student-col {
    position: sticky;
    left: 0;
    background: #f8f9fa;
    z-index: 5;
}

.matrix-corner {
    position: sticky;
    top: 0;
    left: 0;
    z-index: 15;
    background: #e9ecef;
}

.rotated-text {
    writing-mode: vertical-rl;
    text-orientation: mixed;
    transform: rotate(180deg);
    max-height: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.status-cell {
    text-align: center;
    padding: 8px;
    border: 1px solid #dee2e6;
    width: 40px;
    height: 40px;
}
```

### Thymeleaf Template Structure
```html
<div class="task-matrix-container">
    <table class="task-matrix">
        <thead>
            <tr class="matrix-header">
                <th class="matrix-corner">Schüler / Aufgaben</th>
                <th th:each="task : ${matrix.tasks}" class="matrix-header">
                    <div class="rotated-text" th:text="${task.title}" th:title="${task.title}">
                        Aufgabe
                    </div>
                </th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="student : ${matrix.students}">
                <td class="matrix-student-col" th:text="${student.name}">Schüler</td>
                <td th:each="task : ${matrix.tasks}" class="status-cell">
                    <!-- Status Icon basierend auf statusMap -->
                    <i th:class="'fas ' + ${matrix.getIcon(student.id, task.id)}"
                       th:style="'color: ' + ${matrix.getColor(student.id, task.id)}"></i>
                </td>
            </tr>
        </tbody>
    </table>
</div>
```

## Implementierungsschritte

### 1. Controller erweitern
- `TeacherGroupController.groupDetail()` um Matrix-Daten erweitern
- Neue Helper-Klassen für `StudentTaskMatrix` hinzufügen

### 2. Service erweitern  
- `GroupService.getStudentTaskMatrix()` implementieren
- Effiziente Datenbankabfragen für Status-Matrix

### 3. Template anpassen
- Bestehenden "Schüler und zugeordnete Aufgaben" Block ersetzen
- CSS für sticky headers und responsive Design
- JavaScript für bessere UX (Tooltips, Click-Handler)

### 4. Interaktivität
- Click auf Status-Icon → direkter Link zur Aufgabe/Review
- Hover-Tooltips mit Details (Status, letzte Änderung, etc.)
- Optional: Farbkodierung für Fälligkeitsdaten

## Datenbank-Performance
- Minimale Anzahl Queries durch JOIN-optimierte Abfragen
- Caching der Matrix-Daten bei größeren Gruppen
- Lazy Loading bei sehr vielen Aufgaben

## Fallback
- Bei > 20 Aufgaben: Horizontales Scrollen mit Warnung
- Bei > 50 Schülern: Paginierung oder alternative Darstellung
- Mobile: Vereinfachte Ansicht oder Link zur Vollversion

## Status-Icon Mapping
```java
public String getStatusIcon(TaskStatus status) {
    if (status == null) return "fas fa-circle text-secondary";
    
    switch (status.getName()) {
        case "NICHT_BEGONNEN": return "fas fa-circle text-secondary";
        case "IN_BEARBEITUNG": return "fas fa-edit text-primary";
        case "ABGEGEBEN": return "fas fa-hourglass-half text-warning";
        case "ÜBERARBEITUNG_NÖTIG": return "fas fa-redo text-danger";
        case "VOLLSTÄNDIG": return "fas fa-check-circle text-success";
        default: return "fas fa-question text-muted";
    }
}
```
