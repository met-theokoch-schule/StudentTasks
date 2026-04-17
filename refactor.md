# Refactoring-Vorschlaege fuer das Spring-Boot-Projekt

## Grundlage der Analyse

Die Analyse basiert auf den produktiven Quellen unter `src/main/java`, den Spring-Konfigurationen in `src/main/resources` sowie der aktuellen Projektstruktur. Es gibt aktuell keine Testquellen unter `src/test`. Dadurch ist das Risiko bei groesseren Umbauten hoeher als in einem vergleichbar grossen Projekt mit vorhandener Testabdeckung.

## Bewertungsmaßstab fuer das Risiko

- `Niedrig` = ca. 10-20 % Wahrscheinlichkeit, dass bestehende Funktionalitaet waehrend des Umbaus beeintraechtigt wird
- `Mittel` = ca. 20-45 %
- `Mittel-Hoch` = ca. 45-65 %
- `Hoch` = ca. 65-80 %

Die Prozentangaben sind keine mathematische Messung, sondern eine technische Einschaetzung auf Basis von Kopplung, fehlenden Tests, Datenbankbetroffenheit und Anzahl der beruehrten Endpunkte.

---

## 1. Web-Schicht entflechten und Controller auf klare Anwendungsfaelle zuschneiden

**Beobachtung**

Die Web-Schicht ist stark ueberladen und ueberlappt in ihren Verantwortlichkeiten. Besonders auffaellig sind `StudentController`, `TeacherTaskController`, `TeacherController` und `TaskController`. Dort sind Routing, Berechtigungslogik, Statuslogik, Repository-Zugriffe, DTO-Aufbereitung und View-spezifische Entscheidungen eng vermischt. Dazu kommen ueberschneidende oder inkonsistente Endpunkte, zum Beispiel mehrere Schreibpfade fuer Aufgaben im Lehrerbereich und ein offenkundig fehlerhafter Pfad in `TeacherController` (`/teacher/teacher/submissions/...`).

**Benötigte Änderungen**

- Controller nach fachlichen Anwendungsfaellen aufteilen, zum Beispiel:
  - Student-Dashboard und Aufgabenliste
  - Student-Aufgabenansicht und Verlauf
  - Lehrer-Aufgabenverwaltung
  - Lehrer-Bewertung und Abgabenansicht
- Gemeinsame Anwendungslogik aus den Controllern in Use-Case-Services oder Facades verschieben.
- Einheitliche Request-DTOs und Form-Objekte einfuehren, statt komplette Entities direkt mit `@ModelAttribute` zu binden.
- Doppelte Endpunkte zusammenfuehren, sodass es pro fachlichem Vorgang genau einen Schreibpfad gibt.
- Rueckgabeverhalten vereinheitlichen: entweder HTML-Controller oder REST-Controller, aber keine Mischformen mit ad-hoc String-Antworten.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/controller/StudentController.java`
- `src/main/java/com/example/studenttask/controller/TeacherTaskController.java`
- `src/main/java/com/example/studenttask/controller/TeacherController.java`
- `src/main/java/com/example/studenttask/controller/TaskController.java`

**Nutzen**

- Weniger Seiteneffekte bei Aenderungen
- Klarere Verantwortlichkeiten
- Deutlich bessere Testbarkeit
- Geringeres Risiko, dass zwei Endpunkte dieselbe Fachlogik unterschiedlich behandeln

**Risiko fuer Funktionsbeeintraechtigung**

`Hoch`, ca. `70 %`

**Warum das Risiko hoch ist**

Diese Aenderung beruehrt Routing, Form-Binding, Redirects, Templates und Sicherheitsregeln gleichzeitig. Ohne Tests ist sehr wahrscheinlich, dass zunaechst einzelne Lehrer- oder Schuelerablaeufe regressieren.

---

## 2. Service-Schicht von der Web-Schicht entkoppeln

**Beobachtung**

`GroupService` ist derzeit direkt an `TeacherGroupController` gekoppelt und verwendet dessen innere Klassen (`GroupInfo`, `TaskInfo`, `GroupStatistics`, `StudentTaskInfo`) als Rueckgabetypen. Zusaetzlich werden lose `Map<String, Object>`-Strukturen verwendet. Damit kennt die Service-Schicht Details der MVC-Darstellung und ist nicht mehr sauber wiederverwendbar.

**Benötigte Änderungen**

- Eigene DTOs oder Read-Model-Klassen in ein neutrales Paket verschieben, zum Beispiel `dto`, `viewmodel` oder `querymodel`.
- `GroupService` so umbauen, dass es keine Controller-Klassen mehr importiert.
- `Map<String, Object>` durch typisierte Rueckgabeobjekte ersetzen.
- Controller sollen nur noch View-spezifisches Mapping machen, aber keine Fachdatenstruktur definieren.
- Falls sinnvoll: den Leseweg fuer komplexe Gruppenansichten in einen dedizierten Query-Service auslagern.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/service/GroupService.java`
- `src/main/java/com/example/studenttask/controller/TeacherGroupController.java`

**Nutzen**

- Saubere Schichtentrennung
- Wiederverwendbarkeit der Fachlogik
- Weniger fragiler Code in Templates und Services
- Einfachere Umstellung auf API- oder SPA-basierte Frontends

**Risiko fuer Funktionsbeeintraechtigung**

`Mittel-Hoch`, ca. `50 %`

**Warum das Risiko relevant ist**

Die betroffenen Datenstrukturen werden direkt von den Lehrer-Ansichten verwendet. Schon kleine Aenderungen an Feldnamen oder Aggregationslogik koennen Templates brechen.

---

## 3. OAuth2-Benutzersynchronisation zentralisieren und doppelte Benutzerlogik entfernen

**Beobachtung**

Es existieren zwei parallele Synchronisationspfade fuer OAuth2-Benutzer:

- `UserService.findOrCreateUserFromOAuth2(...)`
- `UserService.createOrUpdateUserFromOAuth2(...)`

Gleichzeitig synchronisiert `SecurityConfig` den Benutzer schon beim Login, waehrend `DashboardController` ihn anschliessend erneut aktualisiert. Die Rollen- und Gruppenlogik ist teilweise doppelt, und grosse Mengen personenbezogener OAuth2-Daten werden per `System.out.println` ausgegeben.

**Benötigte Änderungen**

- Einen einzigen fachlichen Einstiegspunkt definieren, zum Beispiel `IdentitySyncService.syncFromOAuth2User(...)`.
- Die Synchronisation genau einmal pro Login oder pro bewusstem Sync-Ereignis ausfuehren.
- Rollen- und Gruppenmapping in klar abgegrenzte Mapper oder Strategien auslagern.
- `DashboardController` von technischer Synchronisationslogik befreien.
- Vollstaendige Token- und Attribut-Dumps aus dem Produktivpfad entfernen und durch strukturiertes Logging ersetzen.
- Lehrer-/Schueler-Rollenerkennung zentral ueber normierte Codes abbilden, statt an vielen Stellen String-Varianten wie `teacher`, `lehrer`, `ROLE_TEACHER` zu pruefen.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/service/UserService.java`
- `src/main/java/com/example/studenttask/config/SecurityConfig.java`
- `src/main/java/com/example/studenttask/controller/DashboardController.java`
- `src/main/java/com/example/studenttask/service/AuthenticationService.java`

**Nutzen**

- Vorhersehbare Login- und Berechtigungslogik
- Weniger doppelte Datenbankupdates
- Besserer Datenschutz und bessere Wartbarkeit
- Klarere Grundlage fuer Rollen- und Gruppensynchronisation

**Risiko fuer Funktionsbeeintraechtigung**

`Mittel-Hoch`, ca. `55 %`

**Warum das Risiko relevant ist**

Login, Rollenerkennung und Gruppenzuordnung sind zentrale Querschnittsthemen. Fehler in diesem Refactoring koennen direkt zu falschen Berechtigungen oder fehlenden Nutzerzuordnungen fuehren.

---

## 4. Task-/TaskView-Modell konsolidieren und die View-Zuordnung vereinheitlichen

**Beobachtung**

Im Datenmodell von `Task` existieren zwei verschiedene Beziehungen fuer praktisch dieselbe Fachbedeutung:

- `viewType`
- `taskView`

Der Code nutzt beide Felder parallel. Einige Stellen verwenden `task.getTaskView()`, andere fallen auf `task.getViewType()` zurueck. `TaskView` selbst mappt nur ueber `viewType`, nicht ueber `taskView`. Das ist ein klassisches Zeichen fuer ein halb abgeschlossenes Refactoring und ein hohes Daten- und Verhaltensrisiko.

**Benötigte Änderungen**

- Ein einziges kanonisches Feld fuer die View-Zuordnung festlegen.
- Datenmigration fuer bestehende Datensaetze planen, damit alte Werte aus dem auszumusternden Feld verlustfrei uebernommen werden.
- Alle Controller, Services, Templates und Repository-Zugriffe auf das neue Modell umstellen.
- Rueckgabepfade vereinheitlichen: Templates immer ueber `templatePath` referenzieren, nicht teilweise ueber numerische IDs.
- Nach erfolgreicher Migration das alte Feld inklusive Datenbankspalte entfernen.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/model/Task.java`
- `src/main/java/com/example/studenttask/model/TaskView.java`
- `src/main/java/com/example/studenttask/service/TaskService.java`
- `src/main/java/com/example/studenttask/service/TaskContentService.java`
- `src/main/java/com/example/studenttask/controller/StudentTaskApiController.java`
- `src/main/java/com/example/studenttask/controller/TeacherController.java`
- `src/main/java/com/example/studenttask/controller/TeacherTaskController.java`

**Nutzen**

- Konsistentes Datenmodell
- Weniger Null-Fallbacks und Sonderfaelle
- Robustere Template-Aufloesung
- Einfachere Erweiterung neuer Aufgabentypen

**Risiko fuer Funktionsbeeintraechtigung**

`Hoch`, ca. `75 %`

**Warum das Risiko hoch ist**

Diese Aenderung beruehrt Persistenz, bestehende Daten, Template-Aufloesung, Aufgabenerstellung und Anzeige. Wenn die Migration nicht sauber geplant ist, sind defekte Aufgabenansichten sehr wahrscheinlich.

---

## 5. Statusmodell typisieren und Statusuebergaenge zentral modellieren

**Beobachtung**

Statuscodes wie `NICHT_BEGONNEN`, `ABGEGEBEN`, `UEBERARBEITUNG_NOETIG` und `VOLLSTAENDIG` sind an sehr vielen Stellen als harte Strings eingebaut. Teilweise werden sogar feste Datenbank-IDs verwendet, zum Beispiel wenn `StudentController` initial `TaskStatus` per `findById(1L)` holt. Gleichzeitig erzeugt `UserTaskService.findOrCreateUserTask(...)` neue `UserTask`-Objekte ohne initialen Status, obwohl das Entity den Status als `nullable = false` deklariert.

**Benötigte Änderungen**

- Ein typsicheres Statusmodell einfuehren, zum Beispiel ueber ein Enum wie `TaskStatusCode`.
- Statusuebergaenge nur noch in einer zentralen Workflow-Komponente definieren.
- Standardstatus nicht mehr ueber Datenbank-ID, sondern ueber Code aufloesen.
- Erstellung und Statuswechsel von `UserTask` ueber einen einzigen, validierten Pfad laufen lassen.
- Doppelte Methoden wie `resolveSubmittedStatusName(...)` zusammenziehen.
- Optional: technische und fachliche Status trennen, falls zukuenftig weitere Review-Zustaende hinzukommen.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/service/TaskStatusService.java`
- `src/main/java/com/example/studenttask/service/UserTaskService.java`
- `src/main/java/com/example/studenttask/service/TaskContentService.java`
- `src/main/java/com/example/studenttask/service/TaskReviewService.java`
- `src/main/java/com/example/studenttask/controller/StudentController.java`
- `src/main/java/com/example/studenttask/controller/StudentTaskApiController.java`
- `src/main/java/com/example/studenttask/service/GroupService.java`
- `src/main/java/com/example/studenttask/controller/TeacherController.java`
- `src/main/java/com/example/studenttask/controller/TeacherGroupController.java`

**Nutzen**

- Weniger Statusfehler und magische Strings
- Klarere fachliche Regeln
- Einfachere Erweiterbarkeit bei neuen Review- oder Bearbeitungszustaenden
- Besser testbare Statusmaschine

**Risiko fuer Funktionsbeeintraechtigung**

`Mittel-Hoch`, ca. `60 %`

**Warum das Risiko relevant ist**

Statuswerte steuern fast alle Kernauswertungen: Dashboard, Abgabe, Review, Gruppenmatrix, Verlauf und Lehrersichten. Jede Aenderung im Workflow hat sofort sichtbare Auswirkungen.

---

## 6. Versionierungs-, Abgabe- und Review-Modell vereinheitlichen

**Beobachtung**

Der aktuelle Fachzustand ist ueber mehrere Objekte verteilt:

- `TaskContent.isSubmitted`
- `Submission` mit eigener `version`
- `TaskReview.version`

Damit wird derselbe Sachverhalt mehrfach modelliert: Welche Version wurde gespeichert, welche wurde abgegeben und welche wurde bewertet. Solche Redundanzen erzeugen frueher oder spaeter Inkonsistenzen.

**Benötigte Änderungen**

- Eine eindeutige fachliche Quelle fuer "Abgabe" festlegen.
- Entscheiden, ob Reviews sich auf `Submission` oder direkt auf eine unveraenderliche `TaskContent`-Version beziehen sollen.
- Eindeutige Invarianten einfuehren, zum Beispiel:
  - eine Abgabe gehoert genau zu einer Content-Version
  - eine Version darf nicht mehrfach als neue Abgabe fuer denselben Fachvorgang angelegt werden
- Datenbank-Constraints und eindeutige Repository-Methoden einfuehren.
- Service-Methoden fuer `save`, `submit`, `review` neu zuschneiden.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/model/TaskContent.java`
- `src/main/java/com/example/studenttask/model/Submission.java`
- `src/main/java/com/example/studenttask/model/TaskReview.java`
- `src/main/java/com/example/studenttask/service/TaskContentService.java`
- `src/main/java/com/example/studenttask/service/SubmissionService.java`
- `src/main/java/com/example/studenttask/service/TaskReviewService.java`
- zugehoerige Repositories

**Nutzen**

- Weniger Inkonsistenzen in Historie und Review-Zuordnung
- Klarere Abfragewege fuer "letzte Abgabe", "bewertete Version", "offene Abgabe"
- Bessere Grundlage fuer Auditierbarkeit

**Risiko fuer Funktionsbeeintraechtigung**

`Hoch`, ca. `70 %`

**Warum das Risiko hoch ist**

Das ist ein strukturelles Datenmodell-Refactoring mit potenzieller Datenmigration. Historische Inhalte, Reviews und Versionen muessen konsistent uebernommen werden, sonst gehen wichtige Zusammenhaenge verloren.

---

## 7. Datenzugriffe fuer Gruppen-, Dashboard- und Aufgabenansichten auf Query-Ebene optimieren

**Beobachtung**

Mehrere Lesewege arbeiten mit stark verschachtelten Schleifen und vielen Einzelabfragen. Beispiele dafuer sind `GroupService` und `StudentController`. Dort werden in Schleifen wiederholt UserTasks, TaskContents oder Gruppenbeziehungen geladen und gezaehlt. Das fuehrt bei wachsender Datenmenge sehr wahrscheinlich zu N+1-Problemen und schlecht vorhersagbarer Performance.

**Benötigte Änderungen**

- Fuer zentrale Uebersichten dedizierte Query-Methoden oder Projektionen einfuehren.
- Wo sinnvoll `JOIN FETCH`, `@EntityGraph` oder speziell zugeschnittene Repository-Abfragen nutzen.
- Statistische Auswertungen nicht mehr pro Datensatz in Java zusammenzaehlen, wenn sie effizienter in einer Datenbankabfrage ermittelt werden koennen.
- Read-Modelle fuer Gruppenmatrix, Pending Reviews und Student-Dashboard einfuehren.
- Das globale `EAGER`-Laden von Rollen und Gruppen im `User`-Entity ueberpruefen und nur dort laden, wo es fachlich benoetigt wird.

**Betroffene Bereiche**

- `src/main/java/com/example/studenttask/service/GroupService.java`
- `src/main/java/com/example/studenttask/controller/StudentController.java`
- `src/main/java/com/example/studenttask/controller/TeacherController.java`
- `src/main/java/com/example/studenttask/repository/TaskRepository.java`
- `src/main/java/com/example/studenttask/repository/UserTaskRepository.java`
- `src/main/java/com/example/studenttask/repository/TaskContentRepository.java`
- `src/main/java/com/example/studenttask/model/User.java`

**Nutzen**

- Bessere Skalierbarkeit
- Vorhersehbarere Antwortzeiten
- Weniger Last auf Datenbank und Hibernate Session
- Geringeres Risiko fuer Lazy-/Performance-Probleme in Templates

**Risiko fuer Funktionsbeeintraechtigung**

`Mittel`, ca. `40 %`

**Warum das Risiko moderat ist**

Die Fachlogik aendert sich idealerweise nicht, aber die Daten werden auf anderem Weg geladen und aggregiert. Fehler zeigen sich dann meist in fehlenden Eintraegen oder falschen Zaehlern in Uebersichten.

---

## 8. Fehlerbehandlung und API-Vertraege standardisieren

**Beobachtung**

Es gibt im Projekt sehr unterschiedliche Fehlerbehandlungsstrategien:

- `RuntimeException` direkt im Controller
- Redirects bei Fehlern
- leere Strings oder Freitext als API-Antwort
- unterschiedliche HTTP-Statuscodes fuer fachlich aehnliche Fehler

Dadurch ist unklar, wie Frontend und Templates auf Fehler reagieren sollen, und spaetere Aenderungen werden schwerer testbar.

**Benötigte Änderungen**

- Eine zentrale Fehlerbehandlung mit `@ControllerAdvice` einfuehren.
- Fuer REST-Endpunkte standardisierte Fehlerantworten definieren, zum Beispiel mit klaren Fehlercodes und Meldungen.
- Fachliche Fehler wie "Task nicht gefunden", "kein Zugriff", "ungueltiger Statuswechsel" als eigene Exception-Typen modellieren.
- HTML- und REST-Fehlerpfade bewusst trennen.
- Logging und User-Feedback voneinander entkoppeln.

**Betroffene Bereiche**

- nahezu alle Controller
- besonders `StudentTaskApiController`, `StudentController`, `TeacherTaskController`, `TeacherController`, `DebugController`

**Nutzen**

- Vorhersehbare API-Vertraege
- Einfachere Frontend-Integration
- Sauberere Fehlersuche
- Bessere Grundlage fuer Tests

**Risiko fuer Funktionsbeeintraechtigung**

`Mittel`, ca. `35 %`

**Warum das Risiko moderat ist**

Die eigentliche Fachlogik bleibt weitgehend erhalten, aber Client-Code und Templates muessen auf neue Antwortformen vorbereitet werden.

---

## 9. Konfigurations- und Betriebsmodell auf Produktionsreife bringen

**Beobachtung**

Die Konfiguration enthaelt aktuell mehrere betriebliche Risiken:

- OAuth-Client-Secret ist in `application.properties` und `application-replit.properties` hinterlegt
- `spring.jpa.hibernate.ddl-auto=update` ist aktiv
- lokale Datenbankdateien liegen im Repository
- Build-Artefakte unter `target/` sind vorhanden
- `.gitignore` ignoriert weder `target/` noch die lokalen Datenbankdateien

Das ist weniger ein Code-Smell im engeren Sinn, aber ein klares Wartungs- und Betriebsproblem.

**Benötigte Änderungen**

- Secrets konsequent ueber Umgebungsvariablen oder Secret-Management bereitstellen.
- Profile fuer lokal, staging und produktiv sauber trennen.
- `ddl-auto=update` mittelfristig durch versionierte Migrationen ersetzen, zum Beispiel mit Flyway oder Liquibase.
- `target/`, Datenbankdateien und andere lokale Artefakte aus dem Repository heraushalten.
- Start- und Deployment-Annahmen dokumentieren.

**Betroffene Bereiche**

- `src/main/resources/application.properties`
- `src/main/resources/application-replit.properties`
- `.gitignore`
- Repository-Struktur insgesamt

**Nutzen**

- Sichererer Betrieb
- Reproduzierbare Deployments
- Nachvollziehbare Datenbankschemata
- Weniger versehentliche Seiteneffekte durch lokale Dateien

**Risiko fuer Funktionsbeeintraechtigung**

`Mittel-Hoch`, ca. `50 %`

**Warum das Risiko relevant ist**

Konfigurations- und Migrationsaenderungen betreffen den Start des Systems und den Datenbankzugriff direkt. Fehler zeigen sich oft erst beim Deployment oder beim ersten Start mit realen Daten.

---

## 10. Testbasis als Voraussetzung fuer groessere Refactorings aufbauen

**Beobachtung**

Es existiert aktuell keine erkennbare Testbasis. Gleichzeitig gibt es mehrere grosse, stark gekoppelte Klassen und kritische Ablaufe in Login, Statuswechsel, Abgabe und Review.

**Benötigte Änderungen**

- Mindestens folgende automatisierte Tests einfuehren, bevor groessere Umbauten umgesetzt werden:
  - Web-/Routing-Tests fuer die wichtigsten Lehrer- und Schuelerpfade
  - Service-Tests fuer Statuswechsel und Benutzersynchronisation
  - Persistenztests fuer Versionierung, Submission und Review
  - Regressionstests fuer Task-Erstellung, Task-Update und Aufgabenansicht
- Ein eigenes Testprofil mit separater Datenbankkonfiguration einrichten.
- Kritische Altfehler zunaechst mit Reproduktionstests absichern, bevor der Code umgebaut wird.

**Betroffene Bereiche**

- gesamtes Projekt
- insbesondere alle groesseren Controller und Services

**Nutzen**

- Refactorings werden planbar
- Regressionsrisiko sinkt deutlich
- Fachregeln werden erstmals explizit dokumentiert

**Risiko fuer Funktionsbeeintraechtigung**

`Niedrig`, ca. `15 %`

**Warum das Risiko hier niedrig ist**

Tests veraendern das Laufzeitverhalten nicht direkt. Der eigentliche Wert liegt darin, das Risiko aller anderen grossen Refactorings erheblich zu senken.

---

## Empfohlene Reihenfolge

Wenn die Vorschlaege umgesetzt werden sollen, wuerde ich die Reihenfolge so waehlen:

1. Testbasis aufbauen
2. OAuth2-/Benutzersynchronisation bereinigen
3. Statusmodell zentralisieren
4. Web-Schicht und Service-Schicht entkoppeln
5. Query- und Read-Modelle optimieren
6. Task-/TaskView-Modell konsolidieren
7. Versionierungs-/Submission-Modell vereinheitlichen
8. Fehlerbehandlung standardisieren
9. Konfigurations- und Betriebsmodell haerten

## Kurzfazit

Das Projekt wirkt nicht "schlecht", aber es traegt deutlich die Spuren eines schnell gewachsenen Systems: viel Fachlogik in Controllern, unvollstaendige Zwischenrefactorings, sehr viel Debug-Code im Produktivpfad, fehlende Tests und mehrere doppelte Modelle fuer denselben fachlichen Sachverhalt. Die groessten Hebel liegen deshalb nicht in Einzeloptimierungen, sondern in einer klareren Schichtentrennung, einem konsistenten Datenmodell und einem Sicherheitsnetz aus automatisierten Tests.
