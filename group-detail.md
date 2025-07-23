
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

## JavaScript-Bibliotheken Bewertung

### Option 1: Vanilla CSS + Minimal JavaScript (EMPFOHLEN)
**Vorteile:**
- Keine zusätzlichen Abhängigkeiten
- Volle Kontrolle über Design und Performance
- Bootstrap-kompatibel
- Sticky Headers mit CSS sind gut unterstützt
- Rotierter Text mit CSS `writing-mode` ist Standard

**Nachteile:**
- Mehr manuelle Implementierung
- Potenzielle Browser-Kompatibilitätsprobleme bei älteren Versionen

**Umsetzung:**
```css
.task-matrix-container {
    position: relative;
    max-height: 70vh;
    overflow: auto;
    border: 1px solid #dee2e6;
}

.matrix-header th {
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

.rotated-text {
    writing-mode: vertical-rl;
    text-orientation: mixed;
    transform: rotate(180deg);
    max-height: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
```

### Option 2: DataTables.js
**Vorteile:**
- Integrierte Sticky Headers
- Responsive Design
- Scrolling-Features
- Such- und Filterfunktionen

**Nachteile:**
- jQuery-Abhängigkeit (Bootstrap 5 ist jQuery-frei)
- Komplexe Konfiguration für Matrix-Layout
- Schwierig, rotierten Text zu implementieren
- Überdimensioniert für unseren Anwendungsfall

### Option 3: AG-Grid (Community)
**Vorteile:**
- Sehr mächtige Grid-Funktionen
- Sticky Columns/Headers out-of-the-box
- Virtuelle Scrolling bei großen Datenmengen

**Nachteile:**
- Sehr große Bibliothek (~500KB+)
- Komplexe API
- Overkill für einfache Matrix-Anzeige
- Lizenzprobleme bei erweiterten Features

### Option 4: CSS Grid + Intersection Observer
**Vorteile:**
- Modern CSS Grid Layout
- Performant
- Flexibler als Tables

**Nachteile:**
- Komplexere Implementierung
- Sticky Headers schwieriger umsetzbar
- Mehr JavaScript für Scrolling-Logik

## Entscheidung: Vanilla CSS + Bootstrap

**Begründung:**
1. **Einfachheit:** Unsere Anforderungen (sticky headers/columns, rotierter Text) sind mit modernem CSS gut umsetzbar
2. **Performance:** Keine zusätzlichen großen Bibliotheken
3. **Wartbarkeit:** Code bleibt überschaubar und Bootstrap-konsistent
4. **Browser-Support:** CSS Sticky und Writing-Mode sind in allen modernen Browsern verfügbar

## Erweiterte Implementierung

### CSS mit verbesserter Browser-Kompatibilität
```css
.task-matrix-container {
    position: relative;
    max-height: 70vh;
    overflow: auto;
    border: 1px solid #dee2e6;
    border-radius: 8px;
}

.task-matrix {
    min-width: 100%;
    border-collapse: separate;
    border-spacing: 0;
    font-size: 0.875rem;
}

.matrix-header th {
    position: sticky;
    top: 0;
    background: #f8f9fa;
    z-index: 10;
    border-bottom: 2px solid #dee2e6;
    padding: 12px 8px;
    vertical-align: bottom;
}

.matrix-student-col {
    position: sticky;
    left: 0;
    background: #f8f9fa;
    z-index: 5;
    border-right: 2px solid #dee2e6;
    min-width: 150px;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.matrix-corner {
    position: sticky;
    top: 0;
    left: 0;
    z-index: 15;
    background: #e9ecef;
    border-right: 2px solid #dee2e6;
    border-bottom: 2px solid #dee2e6;
}

.rotated-text {
    writing-mode: vertical-rl;
    text-orientation: mixed;
    transform: rotate(180deg);
    max-height: 120px;
    min-height: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding: 8px 4px;
    line-height: 1.2;
}

.status-cell {
    text-align: center;
    padding: 8px;
    border: 1px solid #dee2e6;
    width: 45px;
    height: 45px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.status-cell:hover {
    background-color: #f8f9fa;
}

.status-icon {
    font-size: 1.2em;
    transition: transform 0.2s;
}

.status-cell:hover .status-icon {
    transform: scale(1.1);
}

/* Responsive Anpassungen */
@media (max-width: 768px) {
    .task-matrix-container {
        max-height: 50vh;
    }
    
    .matrix-student-col {
        min-width: 120px;
        font-size: 0.8rem;
    }
    
    .status-cell {
        width: 35px;
        height: 35px;
        padding: 4px;
    }
    
    .rotated-text {
        max-height: 100px;
        font-size: 0.75rem;
    }
}

/* Fallback für ältere Browser */
@supports not (position: sticky) {
    .matrix-header th,
    .matrix-student-col,
    .matrix-corner {
        position: fixed;
        /* Fallback-Positionierung */
    }
}
```

### Minimales JavaScript für UX-Verbesserungen
```javascript
// Tooltips für Aufgabennamen und Status
document.addEventListener('DOMContentLoaded', function() {
    // Tooltip für gekürzte Aufgabennamen
    document.querySelectorAll('.rotated-text').forEach(el => {
        if (el.scrollHeight > el.clientHeight) {
            el.title = el.textContent.trim();
        }
    });
    
    // Click-Handler für Status-Zellen
    document.querySelectorAll('.status-cell').forEach(cell => {
        cell.addEventListener('click', function() {
            const userTaskId = this.dataset.userTaskId;
            if (userTaskId) {
                window.location.href = `/teacher/submissions/${userTaskId}`;
            }
        });
    });
    
    // Smooth Scrolling für große Tabellen
    const container = document.querySelector('.task-matrix-container');
    if (container) {
        container.style.scrollBehavior = 'smooth';
    }
});
```

## Fazit
Die Vanilla CSS + Bootstrap Lösung ist für unseren Anwendungsfall optimal:
- Keine zusätzlichen Abhängigkeiten
- Vollständige Kontrolle über Design und Performance  
- Modern CSS Features sind ausreichend für unsere Anforderungen
- Einfache Wartbarkeit und Erweiterbarkeit
- Bootstrap-konsistentes Design
