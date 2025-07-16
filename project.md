
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
- **Datenbank:** SQLite mit Hibernate (custom SQLite dialect)
- **Authentifizierung:** OpenID Connect Provider
- **Build-Tool:** Maven (bereits konfiguriert)

### Systemanforderungen
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
├── openIdSubject (String, Unique)
├── name (String)
├── email (String)
├── roles (Set<Role>)
└── groups (Set<Group>)

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
├── description (Text)
├── createdBy (User)
├── createdAt (LocalDateTime)
├── dueDate (LocalDateTime)
├── assignedGroups (Set<Group>)
├── isActive (Boolean)
└── editorHtml (Text)

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

Submission
├── id (Long, Primary Key)
├── userTask (UserTask)
├── content (Text)
├── submittedAt (LocalDateTime)
└── version (Integer)

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
│   │   │   └── DatabaseConfig.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── TaskController.java
│   │   │   ├── SubmissionController.java
│   │   │   ├── TeacherController.java
│   │   │   ├── TeacherGroupController.java
│   │   │   └── TeacherTaskController.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Task.java
│   │   │   ├── UserTask.java
│   │   │   ├── Submission.java
│   │   │   ├── TaskStatus.java
│   │   │   ├── TaskReview.java
│   │   │   ├── Group.java
│   │   │   └── Role.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   ├── SubmissionRepository.java
│   │   │   ├── TaskStatusRepository.java
│   │   │   └── UserTaskRepository.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── TaskService.java
│   │   │   ├── SubmissionService.java
│   │   │   └── AuthenticationService.java
│   │   ├── dto/
│   │   │   ├── SubmissionRequest.java
│   │   │   └── TaskReviewRequest.java
│   │   └── util/
│   │       └── SQLiteDialect.java
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   ├── js/
│       │   └── images/
│       ├── templates/
│       │   ├── fragments/
│       │   ├── student/
│       │   ├── teacher/
│       │   │   ├── dashboard.html
│       │   │   ├── groups-list.html
│       │   │   ├── group-detail.html
│       │   │   ├── tasks-list.html
│       │   │   ├── task-submissions.html
│       │   │   └── submission-history.html
│       │   └── layout.html
│       ├── application.properties
│       └── data.sql
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
  - SQLite Dialect Integration
- [x] **Sprint 1.2:** OpenID Connect Konfiguration
  - OAuth2 Client Setup
  - User Entity mit Gruppen/Rollen
  - Login/Logout Flow
- [x] **Sprint 1.3:** Grundlegende Templates
  - Layout Template
  - Login/Dashboard Views

### Phase 2: Datenmodell und Core Services (Woche 3-4)
- [x] **Sprint 2.1:** Datenbankentitäten implementieren
  - JPA Entities für alle Modelle
  - Repository Layer
  - Database Migrations
- [x] **Sprint 2.2:** Core Services
  - UserService (Gruppen/Rollen-Management)
  - TaskService (CRUD für Aufgaben)
  - SubmissionService (Abgaben-Management)
- [x] **Sprint 2.3:** Task Status System
  - Flexibles Status-System
  - Status-Übergänge definieren

### Phase 3: Schüler-Interface (Woche 5)
- [x] **Sprint 3.1:** Schüler Dashboard
  - Aufgaben nach Gruppe anzeigen
  - Aufgaben-Details View
  - Status-Anzeige
- [x] **Sprint 3.2:** Aufgaben-Editor Integration
  - HTML Editor einbinden
  - Submission API (HTTP POST)
  - Versions-Historie

### Phase 4: Lehrer-Interface (Woche 6)
- [x] **Sprint 4.1:** Lehrer Dashboard
  - Aufgaben erstellen/bearbeiten
  - Gruppenauswahl
  - Aufgaben-Übersicht
- [x] **Sprint 4.2:** Gruppen-basierte Übersicht
  - Liste aller Gruppen mit aktiven Aufgaben anzeigen
  - Gruppen-Detail-View: Klickbare Gruppen zeigen alle SuS mit ihren zugeordneten Aufgaben
  - Multi-Gruppen-Unterstützung: Eine Aufgabe kann mehreren Gruppen zugänglich sein
- [x] **Sprint 4.3:** Aufgaben-basierte Übersicht
  - Liste aller Aufgaben für Lehrer anzeigen
  - Aufgaben-Detail-View: Klick auf Aufgabe zeigt alle SuS, die diese bearbeitet haben
  - Aufklappbare Submission-Historie pro Schüler mit allen Speicherständen/Versionen
- [x] **Sprint 4.4:** Bewertungssystem
  - Submission Review Interface mit Historie
  - Status-Änderungen mit Kommentaren
  - Feedback-System pro Submission-Version

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
- JWT Token Handling
- Session Management

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
   
2. **Aufgaben-Detail:** `/teacher/tasks/{taskId}/submissions`
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
- `GET /student/tasks` - Verfügbare Aufgaben
- `GET /student/tasks/{id}` - Aufgaben-Details
- `POST /api/submissions` - Aufgabe einreichen
- `GET /student/submissions/{taskId}` - Submission-Historie

### Lehrer Endpoints
- `GET /teacher/dashboard` - Lehrer Dashboard
- `GET /teacher/tasks` - Aufgaben-Verwaltung
- `POST /teacher/tasks` - Aufgabe erstellen
- `GET /teacher/groups` - Liste aller Gruppen mit aktiven Aufgaben
- `GET /teacher/groups/{groupId}` - Alle SuS einer Gruppe mit ihren zugeordneten Aufgaben
- `GET /teacher/tasks/{taskId}/submissions` - Alle SuS mit Speicherständen für eine spezifische Aufgabe
- `GET /teacher/submissions/{userTaskId}/history` - Aufklappbare Submission-Historie eines Schülers
- `GET /teacher/reviews` - Bewertungen verwalten
- `POST /api/reviews` - Bewertung abgeben

### Admin Endpoints
- `GET /admin/status` - Status-Verwaltung
- `POST /admin/status` - Neuen Status erstellen

## 8. Deployment-Konfiguration

### Replit Spezifisch
- Port 5000 für Webserver
- SQLite Database im Repl Storage
- Environment Variables für OAuth2

### Produktionsumgebung
- OpenID Connect Provider Konfiguration
- Database Migration Scripts
- Logging und Monitoring

## 9. Meilensteine

| Meilenstein | Beschreibung | Zieldatum | Status |
|-------------|--------------|-----------|--------|
| M1 | OAuth2 + Spring Security Setup | Woche 2 | ⏳ |
| M2 | Datenmodell komplett | Woche 4 | ⏳ |
| M3 | Schüler-Interface MVP | Woche 5 | ⏳ |
| M4 | Lehrer-Interface MVP | Woche 6 | ⏳ |
| M5 | Testing abgeschlossen | Woche 7 | ⏳ |
| M6 | Deployment erfolgreich | Woche 8 | ⏳ |

## 10. Erweiterte Features (Optional)

### Zukünftige Erweiterungen
- [ ] Datei-Upload für Aufgaben
- [ ] Plagiatsprüfung
- [ ] Benachrichtigungssystem
- [ ] Mobile App
- [ ] Statistiken und Reports
- [ ] Export-Funktionen

---

## Implementierungsnotizen

### SQLite Dialect
```java
// Custom SQLite Dialect für Hibernate
// Quelle: https://github.com/gwenn/sqlite-dialect
```

### OAuth2 Claims Mapping
```java
// Gruppen aus OpenID Connect Claims extrahieren
// Rollen-Mapping konfigurieren
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
