# Schüler-Aufgaben-Verwaltungssystem

## 1. Projektübersicht

### Projekttitel
**Schüler-Aufgaben-Verwaltungssystem mit OpenID Connect**

### Projektbeschreibung
Ein webbasiertes System, das es Lehrern ermöglicht, Aufgaben für Schülergruppen zu erstellen und zu verwalten. Schüler können Aufgaben bearbeiten, einreichen und den Status verfolgen. Lehrer können Abgaben bewerten und Feedback geben.

### Projektziele
- [x] OpenID Connect Authentifizierung für Schüler und Lehrer
- [x] Gruppenbasierte Aufgabenzuweisung
- [x] Aufgaben-Editor Integration (HTML-basiert)
- [x] Historische Speicherung aller Abgaben
- [x] Flexibles Status-System für Aufgaben
- [x] Lehrer-Feedback und Bewertungssystem

### Zielgruppe
- **Primär:** Schüler und Lehrer in Bildungseinrichtungen
- **Sekundär:** Administratoren für Systemverwaltung

## 2. Technische Anforderungen

### Technologie-Stack
- **Backend:** Spring Boot 3.x mit Spring Security (OAuth2/OpenID Connect)
- **Frontend:** Thymeleaf Templates mit Bootstrap CSS
- **Datenbank:** SQLite mit Hibernate (Community Dialect erforderlich)
- **Authentifizierung:** OpenID Connect Provider
- **Markdown:** CommonMark Java Library für Markdown-zu-HTML Konvertierung
- **Build-Tool:** Maven (bereits konfiguriert)

### Systemanforderungen
- [x] **Technische Voraussetzungen:**
  - SQLite JDBC Driver (org.xerial:sqlite-jdbc)
  - Hibernate Community Dialects (org.hibernate.orm:hibernate-community-dialects)
  - Datei-basierte SQLite Database mit Persistierung
  - Automatische Schema-Generierung bei erstem Start

- [x] **Funktionale Anforderungen:**
  - OpenID Connect Integration mit Gruppen- und Rollenerkennung
  - Aufgaben-CRUD für Lehrer
  - Gruppenbasierte Aufgabensichtbarkeit
  - HTTP POST Integration für Aufgabenabgaben
  - Historische Speicherung aller Submissions
  - Flexibles Status-System (erweiterbar)
  - Kommentar-System für Lehrer-Feedback

- [x] **Nicht-funktionale Anforderungen:**
  - Sichere Authentifizierung und Autorisierung
  - Responsive Design für verschiedene Geräte
  - Performance für gleichzeitige Nutzer
  - Einfache Erweiterbarkeit des Status-Systems

## 3. Datenmodell

### Entitäten
```
User
├── id (Long, Primary Key)
├── openIdSubject (String, Unique) // aus "sub" Claim
├── name (String) // aus "name" Claim
├── email (String) // aus "email" Claim
├── preferredUsername (String) // aus "preferred_username" Claim
├── givenName (String) // aus "given_name" Claim (Vorname)
├── familyName (String) // aus "family_name" Claim (Nachname)
├── roles (Set<Role>) // abgeleitet aus "roles" Array mit "id" Feld
└── groups (Set<Group>) // abgeleitet aus "groups" Map mit "act" Feld als Gruppennamen

Group
├── id (Long, Primary Key)
├── name (String)
└── description (String)

Role
├── id (Long, Primary Key)
├── name (String) // STUDENT, TEACHER, ADMIN
└── description (String)

Task
├── id (Long, Primary Key)
├── title (String)
├── description (Text) // Markdown-formatted task description
├── createdBy (User)
├── createdAt (LocalDateTime)
├── dueDate (LocalDateTime)
├── assignedGroups (Set<Group>)
├── isActive (Boolean)
├── viewType (String) // References to available task views (foreign key to TaskView.id)
└── defaultSubmission (Text) // Default content for submissions in the format expected by the task view

TaskView
├── id (String, Primary Key) // e.g., "html-editor", "math-exercise", "code-editor"
├── name (String) // Display name for dropdown selection
├── description (String)
├── templatePath (String) // Path to Thymeleaf template file
└── isActive (Boolean)

TaskStatus
├── id (Long, Primary Key)
├── name (String) // NICHT_BEGONNEN, IN_BEARBEITUNG, ABGEGEBEN, ÜBERARBEITUNG_NÖTIG, VOLLSTÄNDIG
├── description (String)
├── order (Integer)
└── isActive (Boolean)

UserTask
├── id (Long, Primary Key)
├── user (User)
├── task (Task)
├── status (TaskStatus)
├── startedAt (LocalDateTime)
└── lastModified (LocalDateTime)

TaskContent
├── id (Long, Primary Key)
├── userTask (UserTask)
├── content (Text) // Serialized content (JSON/XML)
├── version (Integer) // Auto-incrementing version number
├── savedAt (LocalDateTime)
└── isSubmitted (Boolean) // false = draft, true = submitted

Submission
├── id (Long, Primary Key)
├── userTask (UserTask)
├── taskContent (TaskContent) // Reference to submitted content version
├── submittedAt (LocalDateTime)
└── version (Integer) // Reference to TaskContent.version

TaskReview
├── id (Long, Primary Key)
├── userTask (UserTask)
├── reviewer (User)
├── status (TaskStatus)
├── comment (Text)
├── reviewedAt (LocalDateTime)
└── submission (Submission)
```

## 4. Projektstruktur

### Verzeichnisstruktur
```
src/
├── main/
│   ├── java/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── OAuth2Config.java
│   │   │   ├── DatabaseConfig.java
│   │   │   └── SQLiteConfig.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── LoginTestController.java
│   │   │   ├── TaskController.java
│   │   │   ├── SubmissionController.java
│   │   │   ├── TeacherController.java
│   │   │   ├── TeacherGroupController.java
│   │   │   └── TeacherTaskController.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Task.java
│   │   │   ├── TaskView.java
│   │   │   ├── TaskContent.java
│   │   │   ├── UserTask.java
│   │   │   ├── Submission.java
│   │   │   ├── TaskStatus.java
│   │   │   ├── TaskReview.java
│   │   │   ├── Group.java
│   │   │   └── Role.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   ├── TaskViewRepository.java
│   │   │   ├── TaskContentRepository.java
│   │   │   ├── SubmissionRepository.java
│   │   │   ├── TaskStatusRepository.java
│   │   │   └── UserTaskRepository.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── TaskService.java
│   │   │   ├── TaskViewService.java
│   │   │   ├── TaskContentService.java
│   │   │   ├── SubmissionService.java
│   │   │   └── AuthenticationService.java
│   │   ├── dto/
│   │   │   ├── SubmissionRequest.java
│   │   │   └── TaskReviewRequest.java
│   │   └── util/
│   │       └── DatabaseUtils.java
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   ├── js/
│       │   └── images/
│       ├── templates/
│       │   ├── fragments/
│       │   ├── student/
│       │   │   ├── dashboard.html
│       │   │   ├── tasks-list.html
│       │   │   └── task-detail.html
│       │   ├── teacher/
│       │   │   ├── dashboard.html
│       │   │   ├── groups-list.html
│       │   │   ├── group-detail.html
│       │   │   ├── tasks-list.html
│       │   │   ├── task-create.html
│       │   │   ├── task-submissions.html
│       │   │   └── submission-history.html
│       │   ├── taskviews/
│       │   │   ├── html-editor.html
│       │   │   ├── math-exercise.html
│       │   │   ├── code-editor.html
│       │   │   └── text-editor.html
│       │   └── layout.html
│       ├── application.properties
│       ├── data.sql
│       └── taskviews.properties
└── test/
    └── java/
        ├── controller/
        ├── service/
        └── repository/
```

## 5. Entwicklungsphasen

### Phase 1: Setup und Authentifizierung (Woche 1-2)
- [x] **Sprint 1.1:** Spring Boot Setup mit Dependencies
  - Spring Security OAuth2 Client
  - Spring Data JPA mit SQLite
  - Thymeleaf
  - SQLite JDBC Driver
  - Hibernate Community SQLite Dialect (GitHub Dependency)
- [x] **Sprint 1.2:** OpenID Connect Konfiguration
  - OAuth2 Client Setup mit Scopes: 'groups' und 'roles'
  - User Entity mit Gruppen/Rollen aus Claims
  - Login/Logout Flow
  - **Login-Testseite:** Eine Testseite wird erstellt, die nach erfolgreichem Login alle übermittelten Benutzerdaten anzeigt, um die korrekte Konfiguration der Berechtigungen im IDM zu überprüfen.
  - **Login/Logout-Tests:** Es wird eine Möglichkeit geschaffen, den Login/Logout-Prozess generell zu testen, inklusive detaillierter Fehlerausgabe bei gescheitertem Login.
- [x] **Sprint 1.3:** Grundlegende Templates
  - Layout Template
  - Login/Dashboard Views

### Phase 2: Datenmodell und Core Services (Woche 3-4)
- [ ] **Sprint 2.1:** Datenbankentitäten implementieren
  - JPA Entities für alle Modelle (User, Task, TaskView, TaskContent, etc.)
  - Task Entity mit Markdown-Description und Default-Submission Feldern
  - Repository Layer
  - Database Migrations
  - Markdown-Processor Service (CommonMark Library)
- [x] **Sprint 2.2:** Core Services
  - UserService (Gruppen/Rollen-Management)
  - TaskService (CRUD für Aufgaben mit View-Auswahl)
  - TaskViewService (Verfügbare Views verwalten)
  - TaskContentService (Serialisierte Content-Speicherung)
- [x] **Sprint 2.3:** Task Status System
  - Flexibles Status-System
  - Status-Übergänge definieren
  - TaskContent Versionierung

### Phase 3: Lehrer-Interface (Woche 5)
- [ ] **Sprint 3.1:** Lehrer Dashboard
  - Aufgaben erstellen/bearbeiten
  - Gruppenauswahl
  - Aufgaben-Übersicht
- [ ] **Sprint 3.2:** Gruppen-basierte Übersicht
  - Liste aller Gruppen mit aktiven Aufgaben anzeigen
  - Gruppen-Detail-View: Klickbare Gruppen zeigen alle SuS mit ihren zugeordneten Aufgaben
  - Multi-Gruppen-Unterstützung: Eine Aufgabe kann mehreren Gruppen zugänglich sein
- [ ] **Sprint 3.3:** Aufgaben-basierte Übersicht
  - Liste aller Aufgaben für Lehrer anzeigen
  - Aufgaben-Detail-View: Klick auf Aufgabe zeigt alle SuS, die diese bearbeitet haben
  - Aufklappbare Submission-Historie pro Schüler mit allen Speicherständen/Versionen
- [ ] **Sprint 3.4:** Bewertungssystem
  - Submission Review Interface mit Historie
  - Status-Änderungen mit Kommentaren
  - Feedback-System pro Submission-Version

### Phase 4: Schüler-Interface (Woche 6)
- [ ] **Sprint 4.1:** Schüler Dashboard
  - Aufgaben nach Gruppe anzeigen
  - Aufgaben-Details View
  - Status-Anzeige
- [ ] **Sprint 4.2:** Aufgaben-Editor Integration
  - HTML Editor einbinden
  - Submission API (HTTP POST)
  - Versions-Historie

### Phase 5: Testing und Optimierung (Woche 7)
- [x] **Sprint 5.1:** Unit Tests
  - Service Layer Tests
  - Repository Tests
- [x] **Sprint 5.2:** Integration Tests
  - Controller Tests
  - Security Tests
- [x] **Sprint 5.3:** Performance und UI
  - Frontend Optimierungen
  - Responsive Design

### Phase 6: Deployment und Go-Live (Woche 8)
- [x] **Sprint 6.1:** Deployment Vorbereitung
  - Produktions-Konfiguration
  - Environment Variables
- [x] **Sprint 6.2:** Go-Live und Monitoring
  - Replit Deployment
  - Monitoring Setup

## 6. Sicherheitskonzept

### Authentifizierung
- OpenID Connect mit externem Provider
- JWT Token Handling mit Scopes: 'groups' und 'roles'
- Session Management mit Gruppen- und Rollenmapping

### Autorisierung
- Rollenbasierte Zugriffskontrolle (RBAC)
- Gruppenbasierte Datensichtbarkeit
- Method-Level Security

### Datenschutz
- Verschlüsselung sensibler Daten
- DSGVO-konforme Datenspeicherung
- Audit-Logs für Änderungen

## 7. Detaillierte UI-Flows

### Lehrer-Interface Flows

#### Gruppen-basierte Sicht
1. **Gruppenübersicht:** `/teacher/groups`
   - Zeigt Liste aller Gruppen, die momentan aktive Aufgaben haben
   - Jede Gruppe zeigt Anzahl der zugeordneten SuS und aktiven Aufgaben

2. **Gruppen-Detail:** `/teacher/groups/{groupId}`
   - Klick auf Gruppe öffnet Detail-View
   - Zeigt alle SuS der Gruppe mit ihren jeweils zugeordneten Aufgaben
   - Status-Übersicht pro Schüler-Aufgaben-Kombination
   - Direktlinks zu einzelnen Submissions

#### Aufgaben-basierte Sicht
1. **Aufgabenübersicht:** `/teacher/tasks`
   - Liste aller vom Lehrer erstellten Aufgaben
   - Filter: Aktive/Inaktive Aufgaben, nach Gruppen
   - "Neue Aufgabe erstellen" Button

2. **Aufgaben-Erstellung:** `/teacher/tasks/create`
   - **Titel-Feld:** Eingabe des Aufgabentitels
   - **Markdown-Editor:** Aufgabentext mit folgenden Features:
     - Raw Markdown-Eingabe mit Syntax-Highlighting
     - Live-Vorschau-Modus (Split-View oder Tab-Umschaltung)
     - WYSIWYG-Modus mit Markdown-Kompatibilität
     - Umschaltung zwischen Raw/Preview/WYSIWYG Modi
   - **View-Auswahl:** Dropdown mit verfügbaren Task Views
   - **Default-Submission:** Textfeld für Basis-Befüllung im erwarteten Format des gewählten Views
   - **Gruppen-Auswahl:** Multi-Select für Zielgruppen
   - **Fälligkeitsdatum:** Datepicker

3. **Aufgaben-Detail:** `/teacher/tasks/{taskId}/submissions`
   - Klick auf Aufgabe zeigt alle SuS, die diese Aufgabe bearbeitet haben
   - Gruppiert nach Gruppen (falls Aufgabe mehreren Gruppen zugeordnet)
   - Pro Schüler: Aufklappbare Submission-Historie mit allen Versionen
   - Schnelle Status-Änderung und Kommentar-Funktion

#### Multi-Gruppen-Aufgaben
- Eine Aufgabe kann mehreren Gruppen gleichzeitig zugeordnet werden
- Lehrer sehen in der Aufgaben-Detail-View alle betroffenen Gruppen
- Gruppierung der Submissions nach Gruppen für bessere Übersicht

## 8. API Endpoints

### Schüler Endpoints
- `GET /student/dashboard` - Schüler Dashboard
- `GET /student/tasks` - Verfügbare Aufgaben (aktive + in Bearbeitung)
- `GET /student/tasks/{id}` - Aufgaben-Details mit spezifischem View und gerenderten Markdown-Aufgabentext
- `GET /api/tasks/{taskId}/content` - Aktueller Bearbeitungsstand (letzter Speicherstand oder Default vom Lehrer)
- `GET /api/tasks/{taskId}/content/{version}` - Spezifischer Speicherstand
- `POST /api/tasks/{taskId}/content` - Content speichern (Format abhängig vom Task View)
- `POST /api/tasks/{taskId}/submit` - Aufgabe als abgegeben markieren
- `GET /student/submissions/{taskId}` - Submission-Historie

### Lehrer Endpoints
- `GET /teacher/dashboard` - Lehrer Dashboard
- `GET /teacher/tasks` - Aufgaben-Verwaltung
- `GET /teacher/tasks/create` - Aufgaben-Erstellungsformular mit Markdown-Editor und View-Auswahl
- `POST /teacher/tasks` - Aufgabe erstellen (mit Markdown-Description und Default-Submission)
- `GET /teacher/tasks/{taskId}/edit` - Aufgabe bearbeiten
- `PUT /teacher/tasks/{taskId}` - Aufgabe aktualisieren
- `POST /api/markdown/preview` - Markdown zu HTML konvertieren (für Live-Vorschau)
- `GET /teacher/groups` - Liste aller Gruppen mit aktiven Aufgaben
- `GET /teacher/groups/{groupId}` - Alle SuS einer Gruppe mit ihren zugeordneten Aufgaben
- `GET /teacher/tasks/{taskId}/submissions` - Alle SuS mit Speicherständen für eine spezifische Aufgabe
- `GET /teacher/submissions/{userTaskId}/history` - Aufklappbare Submission-Historie eines Schülers
- `GET /teacher/reviews` - Bewertungen verwalten
- `POST /api/reviews` - Bewertung abgeben

### Admin Endpoints
- `GET /admin/status` - Status-Verwaltung
- `POST /admin/status` - Neuen Status erstellen

## 8. Task View System

### Verfügbare Task Views
Die verfügbaren Task Views werden in der Datei `taskviews.properties` definiert:

```properties
# Verfügbare Task Views für Dropdown-Auswahl
taskview.simple-text.name=Einfacher Texteditor
taskview.simple-text.description=Einfaches Textfeld für Text-Abgaben
taskview.simple-text.template=taskviews/simple-text.html
taskview.simple-text.active=true

taskview.html-editor.name=HTML Editor
taskview.html-editor.description=Rich-Text HTML Editor für Textaufgaben
taskview.html-editor.template=taskviews/html-editor.html
taskview.html-editor.active=true

taskview.math-exercise.name=Mathematik Übung
taskview.math-exercise.description=Interaktive Mathematik-Aufgaben mit LaTeX
taskview.math-exercise.template=taskviews/math-exercise.html
taskview.math-exercise.active=true

taskview.code-editor.name=Code Editor
taskview.code-editor.description=Syntax-highlightender Code-Editor
taskview.code-editor.template=taskviews/code-editor.html
taskview.code-editor.active=true

taskview.text-editor.name=Text Editor
taskview.text-editor.description=Einfacher Texteditor für Aufsätze
taskview.text-editor.template=taskviews/text-editor.html
taskview.text-editor.active=true
```

### Content Serialisierung
- **Frontend:** Jeder Task View muss seinen Zustand als JSON/XML serialisieren
- **Backend:** Speichert serialisierten Content als Text in TaskContent Entity
- **Versionierung:** Jeder Speichervorgang erstellt eine neue Version
- **API:** GET/POST Endpoints für Content-Abruf und -Speicherung

### Task View Templates
Jeder Task View hat eine eigene Thymeleaf-Template-Datei:
- `taskviews/simple-text.html` - **Beispiel-Implementation:** Einfaches Textfeld mit Auto-Save
- `taskviews/html-editor.html` - Rich-Text Editor
- `taskviews/math-exercise.html` - Mathematik-Aufgaben
- `taskviews/code-editor.html` - Code-Editor
- `taskviews/text-editor.html` - Einfacher Texteditor

### Beispiel Task View: Simple Text Editor

**Template:** `taskviews/simple-text.html`
```html
<!-- Zeigt Aufgabentitel und gerenderten Markdown-Aufgabentext an -->
<div class="task-header">
    <h2 th:text="${task.title}">Aufgabentitel</h2>
    <div class="task-description" th:utext="${renderedDescription}">
        <!-- Hier wird der Markdown-Text als HTML gerendert -->
    </div>
</div>

<!-- Einfaches Textfeld für die Abgabe -->
<div class="submission-area">
    <textarea id="submission-content" rows="10" cols="80">
        <!-- Wird mit Default-Submission oder letztem Speicherstand befüllt -->
    </textarea>
    <button onclick="saveContent()">Speichern</button>
    <button onclick="submitTask()">Abgeben</button>
</div>

<script>
// GET /api/tasks/${taskId}/content - lädt aktuellen Stand
// POST /api/tasks/${taskId}/content - speichert Content als Plain Text
// POST /api/tasks/${taskId}/submit - markiert als abgegeben
</script>
```

**Content-Format:** Plain Text (wird direkt im Textfeld angezeigt)

## 9. Deployment-Konfiguration

### Replit Spezifisch
- Port 5000 für Webserver
- SQLite Datei-basierte Database (./data/student_tasks.db)
- Persistente Datenspeicherung (überlebt Neustarts)
- Environment Variables für OAuth2
- Automatische data/ Verzeichnis-Erstellung

### Produktionsumgebung
- OpenID Connect Provider Konfiguration
- Database Migration Scripts
- Logging und Monitoring

## 10. Meilensteine

| Meilenstein | Beschreibung | Zieldatum | Status |
|-------------|--------------|-----------|--------|
| M1 | OAuth2 + Spring Security Setup | Woche 2 | ⏳ |
| M2 | Datenmodell komplett | Woche 4 | ⏳ |
| M3 | Lehrer-Interface MVP | Woche 5 | ⏳ |
| M4 | Schüler-Interface MVP | Woche 6 | ⏳ |
| M5 | Testing abgeschlossen | Woche 7 | ⏳ |
| M6 | Deployment erfolgreich | Woche 8 | ⏳ |

## 11. Erweiterte Features (Optional)

### Zukünftige Erweiterungen
- [ ] Datei-Upload für Aufgaben
- [ ] Plagiatsprüfung
- [ ] Benachrichtigungssystem
- [ ] Mobile App
- [ ] Statistiken und Reports
- [ ] Export-Funktionen

---

## Implementierungsnotizen

### SQLite Database
```xml
<!-- SQLite-spezifische Dependencies in pom.xml -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
</dependency>

<!-- Hibernate Community SQLite Dialect (von GitHub) -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>
```

```properties
# SQLite Konfiguration in application.properties
spring.datasource.url=jdbc:sqlite:./data/student_tasks.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=create-drop
```

### OAuth2 Claims Mapping
```java
// OAuth2 Claims Struktur basierend auf IServ Provider:
// - "sub": "9cd9395e-e45d-458d-9d25-938246496133" (eindeutige User-ID)
// - "email": "user@domain.com"
// - "name": "Vor- und Nachname"
// - "preferred_username": "username"
// - "given_name": "Vorname"
// - "family_name": "Nachname"
// - "roles": [{"id": "ROLE_TEACHER", "displayName": "Lehrer"}, {"id": "ROLE_STUDENT", "displayName": "Schüler"}]
// - "groups": {"1013": {"act": "kollegium", "name": "Kollegium"}, "16220": {"act": "j11-inf-1", "name": "J11-Inf-1"}}
//
// Rollen-Extraktion: roles[].id (z.B. "ROLE_TEACHER" -> Authority "ROLE_TEACHER")
// Gruppen-Extraktion: groups[].act (z.B. "kollegium" -> Authority "GROUP_kollegium")
```

### SQLite Konfiguration
```java
// SQLite-spezifische Konfiguration
// Hibernate Community Dialect erforderlich da Spring Boot keinen SQLite-Support mitbringt
// Dependency: org.hibernate.orm:hibernate-community-dialects

@Configuration
public class SQLiteConfig {
    // SQLite-spezifische JPA Konfiguration
    // Foreign Key Constraints aktivieren
    // WAL-Mode für bessere Concurrency
}
```

### Status-System Design
```java
// TaskStatus als Entity für Erweiterbarkeit
// Workflow-Engine für Status-Übergänge
```

## Changelog
| Datum | Änderung | Author |
|-------|----------|--------|
| Heute | Projektspezifische Anforderungen definiert | Assistant |