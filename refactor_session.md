# Refactor Session

Stand: 2026-04-20

## Zweck dieses Dokuments

Dieses Dokument haelt nur noch den verdichteten Projektstand fest. Die fruehere Slice-fuer-Slice-Historie wurde bewusst entfernt, weil viele Uebergangsschritte fachlich abgeschlossen sind und fuer die weitere Arbeit kaum noch Mehrwert haben.

Fuer Detailhistorie gilt:

- der aktuelle Arbeitsstand steht in diesem Dokument
- die Langform bleibt bei Bedarf ueber die Git-Historie rekonstruierbar

## Aktueller Gesamtstand

- die Refactoring-Serie hat die kritischen Kernpfade bereits deutlich stabilisiert
- die Runtime ist von `Submission` entkoppelt; Abgabe, Historie und Review laufen ueber `TaskContent`, `TaskReview` und `UserTask.status`
- temporaere Legacy-Migrations- und Rollout-Helfer sind nach erfolgreichem Produktiveinsatz wieder entfernt
- Statusworkflow, TaskView-Zuordnung, OAuth2-Synchronisation und grosse Teile der Web-/Service-Trennung sind umgesetzt
- die verbleibenden Hauptthemen liegen jetzt eher in Betriebsreife, Fehlervertraegen, Persistenzinvarianten und den letzten Controller-Resten

## Wesentliche abgeschlossene Bloecke

### Testbasis und technischer Cleanup

- tragfaehige Unit- und MVC-Testbasis fuer die wichtigen Schueler-, Lehrer- und Statuspfade aufgebaut
- direkte Konsolenausgaben durch strukturiertes Logging ersetzt

### Statusworkflow und Statusdarstellung

- `TaskStatusCode` eingefuehrt und der zentrale Statuspfad stabilisiert
- Default-Status, Submit- und Resubmit-Verhalten vereinheitlicht
- Same-Status-Resubmit proxy-sicher gemacht
- Dashboard-, Gruppen- und Lehrerpfade auf zentrale Statushilfen gezogen

### Web- und Service-Schicht

- Student-Dashboard und Aufgabenliste in `StudentTaskOverviewService` verschoben
- Student-Task-Ansicht, Historie und Versionspfade in `StudentTaskQueryService` verschoben
- Student-Task-API in Query-, Command-, Access- und Exception-Pfade getrennt
- Teacher-Dashboard in `TeacherDashboardQueryService` verschoben
- Teacher-Task-Lese- und Schreibpfade in `TeacherTaskQueryService` und `TeacherTaskCommandService` verschoben
- Gruppenansichten von Controller-internen Datentypen entkoppelt und typisiert

### OAuth2- und Identitaets-Synchronisation

- die wesentliche Synchronisationslogik in einen zentralen Identity-Sync gezogen
- relevante Controller und Login-nahe Pfade auf den zentralen Sync umgestellt

### Task- und TaskView-Konsolidierung

- `taskView` als kanonischer Fachzugriff etabliert
- alte Fallbacks, Aliasse und Uebergangslogik entfernt
- Render- und Schreibpfade auf die vereinheitlichte Sicht gezogen

### Versionierungs- und Submission-Rueckbau

- neue Abgaben schreiben nur noch `TaskContent`
- ungenutzte Submission-Read- und Write-Helfer entfernt
- `Submission` komplett aus Runtime, Repository und Service-Schicht entfernt
- Legacy-Bestandsdaten wurden ueber temporaere Rollout-Helfer beruecksichtigt; diese Hilfen sind inzwischen wieder entfernt

## Noch offene Refactoring-Aufgaben

### 1. Konfigurations- und Betriebsmodell haerten

- erledigt in diesem Slice:
  - gemeinsame Basis-Properties sowie getrennte `local`-, `staging`- und `prod`-Profile eingefuehrt
  - OAuth-Client-Secret aus versionierten Properties entfernt und Replit-Deploy-Credentials aus `.replit` externalisiert
  - `ddl-auto=update` auf das lokale Profil begrenzt; staging/prod laufen mit bestehendem Schema
  - `.gitignore` fuer `target/` und lokale SQLite-Dateien ergaenzt
  - bereits eingecheckte Build- und DB-Artefakte aus dem Git-Index entfernt
- noch offen:
  - versionierte Migrationen als Ersatz fuer `ddl-auto=update` einfuehren
  - staging-/prod-nahe Konfigurations- und Migrations-Tests ergaenzen
  - verbleibende Betriebsdokumentation fuer externe Konfiguration nachziehen

### 2. Fehlerbehandlung global vereinheitlichen

- erledigt in diesem Slice:
  - Lehrer-Dashboard-, Gruppen- und Task-Controller auf fachliche Exceptions fuer Authentifizierung, Zugriff und Not Found umgestellt
  - `TeacherMvcExceptionHandler` fuer konsistente Login-Redirects sowie 404-/403-HTML-Antworten eingefuehrt
  - neue MVC-Regressionstests fuer die standardisierten Lehrer-Fehlerpfade ergaenzt
- erledigt im aktuellen Folge-Slice:
  - `DashboardController` und `StudentController` auf denselben MVC-Fehlervertrag umgestellt
  - Schueler-History- und Versionspfade liefern jetzt direkte DTOs statt Redirect-Ergebnisobjekte
  - `StudentMvcExceptionHandler` sowie neue Student-/Dashboard-Regressionstests ergaenzt
- erledigt im naechsten Folge-Slice:
  - `TaskController` und `TaskIframeQueryService` aus dem Redirect-Ergebnisvertrag geloest
  - Iframe-Zugriff auf `findAssignedTask(...)` umgestellt, damit keine `UserTask` fuer unberechtigte Aufgaben erzeugt werden
  - `StudentMvcExceptionHandler` auf den Iframe-Pfad erweitert und gezielte Iframe-Regressionstests ergaenzt
- erledigt im aktuellen Randrouten-Slice:
  - `/debug` im `HomeController` auf fachliche Auth-Exception statt Inline-Redirect umgestellt
  - `HomeMvcExceptionHandler` fuer den Home-Debug-Pfad eingefuehrt
  - Debug-View-Modell um die tatsaechlich geladenen Gruppen ergaenzt
- erledigt im abschliessenden Debug-Slice:
  - `DebugController` von verteilter Inline-Fehlerbehandlung auf fachliche Exceptions umgestellt
  - `DebugMvcExceptionHandler` fuer Login-Redirect und 404-Fehler im Debug-Content-Viewer eingefuehrt
  - Debug-Task-Zugriff auf `findAssignedTask(...)` gehaertet
- erledigt im aktuellen API-Konflikt-Slice:
  - Persistenz- und Statuskonflikte in `TaskContentService` und `UserTaskService` laufen jetzt ueber `TaskInvariantViolationException`
  - `StudentTaskApiExceptionHandler` mappt diese Konflikte fuer die Student-Task-API konsistent auf `409 invalid_state`
  - gezielte API- und Service-Regressionstests fuer den neuen Konfliktvertrag sind ergaenzt
- erledigt im aktuellen Not-Found-Slice:
  - die letzten generischen `RuntimeException`-Pfade in `TaskService`, `TaskStatusService` und `TaskReviewService` sind durch fachliche Exceptions ersetzt
  - `TeacherMvcExceptionHandler` mappt fehlende Aufgaben und fehlende Status jetzt ebenfalls konsistent auf 404
  - gezielte Service- und Handler-Tests fuer die neuen Not-Found-Vertraege sind ergaenzt
- erledigt im aktuellen OAuth2-Auth-Fehlerpfad-Slice:
  - `IdentitySyncService` nutzt fuer kaputte OAuth2-Identitaeten jetzt `OAuth2IdentityResolutionException` statt `IllegalArgumentException`
  - `SecurityConfig`, `DashboardController` und `HomeController` ziehen diesen Fall auf den bestehenden Auth-Fehlervertrag statt auf implizite 500er
  - gezielte Config-, Controller- und Service-Tests fuer fehlende OAuth2-Subjects sind ergaenzt
- erledigt im aktuellen HTML-Fehlerseiten-Slice:
  - `StudentMvcExceptionHandler` und `TeacherMvcExceptionHandler` liefern fuer 404- und 403-Seiten jetzt kontextspezifische Ruecklinks und Labels
  - `error/404` und `access-denied` sind von lehrerfixem Copy auf generische, pfadsaubere HTML-Fehlerseiten umgestellt
  - gezielte Handler-Regressionstests fuer die neuen Ruecklink-Vertraege sind ergaenzt
- erledigt im aktuellen Debug-404-Slice:
  - `DebugMvcExceptionHandler` nutzt fuer fehlende Debug-Inhalte jetzt denselben 404-HTML-Vertrag wie die uebrigen MVC-Pfade
  - der Debug-Viewer ist damit auf Erfolgsdarstellung reduziert; Fehler laufen nicht mehr ueber ein separates Inline-Error-Template
  - gezielte Debug-Handler-Regressionen fuer Ruecklink und Meldung sind ergaenzt
- erledigt im aktuellen Login-Fehlerdarstellungs-Slice:
  - `HomeMvcExceptionHandler` leitet bei Auth-Pflicht jetzt explizit nach `/login?required=true` um
  - `SecurityConfig` unterscheidet im OAuth2-Failure-Redirect zwischen generischem Loginfehler und `invalid_user_info`
  - die Login-Seite zeigt fuer `required`, `expired`, `oauthIdentityError` und generische Fehler jetzt getrennte Hinweise
- erledigt im aktuellen API-Not-Found-Slice:
  - `StudentTaskApiExceptionHandler` mappt fehlende Tasks und fehlende Status jetzt ebenfalls auf den bestehenden `not_found`-Vertrag
  - API-Schreibpfad-Tests decken nun explizit ab, dass fehlende Status fachlich typisiert bis zum Handler propagieren
  - der generische `internal_error`-Pfad bleibt damit staerker auf echte Ueberraschungsfehler begrenzt
- erledigt im aktuellen API-Forbidden-/Bad-Request-Slice:
  - `StudentTaskApiExceptionHandler` mappt Spring-Sicherheitsfehler jetzt auf `403 forbidden`
  - ungueltige API-Anfragen wie unlesbare Request-Bodies oder Typfehler laufen jetzt auf `400 bad_request`
  - gezielte Handler-Regressionen fuer `forbidden` und `bad_request` sind ergaenzt
- erledigt im aktuellen API-Security-Entry-Point-Slice:
  - `/api/**` nutzt jetzt in `SecurityConfig` einen JSON-Authentication-Entry-Point statt Browser-Redirects
  - Security-seitige API-403-Faelle laufen ueber denselben JSON-Vertrag wie die restlichen API-Fehler
  - gezielte Config-Tests pruefen die serialisierten `401 unauthorized`- und `403 forbidden`-Antworten
- noch offen:
  - gemeinsame fachliche Exception-Muster appweit weiterziehen

### 3. Persistenzinvarianten fuer Versionierung und Review haerten

- erledigt in diesem Slice:
  - `TaskReviewService` validiert Review-Versionen jetzt explizit gegen vorhandene `TaskContent`-Versionen
  - `TeacherMvcExceptionHandler` mappt fehlende Review-Versionen im Lehrerpfad konsistent auf 404
  - `TaskContentService` und `UserTaskService` bewachen die zentralen Schreibpfade gegen doppelte Versionen bzw. doppelte User-Task-Zuordnungen
  - diese Invarianten laufen jetzt ueber `TaskInvariantViolationException` statt ueber generische `IllegalStateException`
  - fehlende Aufgaben und fehlende Status laufen jetzt ueber `TaskNotFoundException` bzw. `TaskStatusNotFoundException`
  - neue `@DataJpaTest`-Slices pruefen die Persistenzvertraege von `UserTaskRepository` und `TaskContentRepository`
- noch offen:
  - echte Mehrspalten-Unique-Constraints ueber einen Migrationspfad materialisieren; die aktuelle SQLite-DDL erzeugt sie trotz Entity-Metadaten noch nicht verlaesslich
  - verbleibende Historien-/Altdatenfaelle mit weiteren Persistenztests absichern

### 4. Letzte Controller-Reste bereinigen

- Create/Edit im `TeacherTaskController` laufen jetzt ueber ein eigenes `TeacherTaskFormDto`
- Lehrer-Create/Edit validieren jetzt Aufgabentitel sowie referenzierte TaskViews, Themen und Gruppen serverseitig und rendern bei Fehlern wieder das passende Formular
- Lehrer-Redirects und Fehlerpfade sind fuer die zentralen MVC-Ansichten konsistenter gezogen
- Schueler-History- und Versionspfade sind auf direkte Query-DTOs ohne Redirect-Wrapper reduziert
- der Iframe-Task-Pfad ist auf direkte `TaskIframeViewDataDto` ohne Redirect-Wrapper reduziert
- der Home-Debug-Pfad liefert Rollen, Gruppen und Auth-Fehler konsistenter
- der Debug-Content-Viewer arbeitet mit zentralem Exception-Handling und geprueftem Task-Zugriff
- einzelne Success-Feedback-Pfade koennen noch klarer auf DTOs und Use-Case-Services geschnitten werden

### 5. Query- und Performance-Pfade pruefen

- entkoppelte Lesepfade bei Bedarf auf N+1- und Aggregationskosten untersuchen
- gegebenenfalls dedizierte Queries oder Projektionen ergaenzen

## Empfohlener naechster Schritt

Es gibt zwei sinnvolle Fortsetzungen:

- klein und sicher: den Migrationspfad fuer echte DB-Constraints vorbereiten oder die neuen Persistenztests auf Historien-/Altdatenfaelle erweitern
- im begonnenen Betriebsblock bleiben: Migrationspfad und konfigurationsnahe Tests vorbereiten

## Letzter verifizierter Teacher-Form-Refactor

- Befehl: `mvn -Dmaven.repo.local=/tmp/m2 -Dtest=TeacherTaskControllerTest,TeacherTaskCommandServiceTest,TeacherTaskQueryServiceTest test`
- Zeitpunkt: `2026-04-26T16:01:21Z`
- Ergebnis:
  - direkte `Task`-Bindung in `TeacherTaskController` durch `TeacherTaskFormDto` ersetzt
  - gezielte Lehrer-Controller-/Service-Tests erfolgreich

## Letzter verifizierter Konfigurationsstart

- Befehl: `timeout 45s mvn -Dmaven.repo.local=/tmp/m2 -DskipTests -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments='-DISERV_CLIENT_ID=test-id -DISERV_CLIENT_SECRET=test-secret -DISERV_REDIRECT_URI=http://localhost:8080/login/oauth2/code/iserv' spring-boot:run`
- Zeitpunkt: `2026-04-26T15:19:43Z`
- Ergebnis:
  - Profil `local` wurde aktiv geladen
  - Spring Boot, JPA, Hikari und Tomcat wurden erfolgreich initialisiert
  - Prozess wurde nach erfolgreichem Start kontrolliert per `timeout` beendet

## Letzter verifizierter Teststand

Gezielter Teacher-Formvalidierungs- und Cleanup-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=TeacherTaskControllerTest,TeacherTaskCommandServiceTest test`
- Zeitpunkt: `2026-04-26T23:00:15Z`
- Ergebnis:
  - Lehrer-Create/Edit validieren Aufgabentitel sowie referenzierte TaskViews, Themen und Gruppen serverseitig
  - manipulierte oder veraltete Referenzen werden nicht mehr still ignoriert
  - der tote Altpfad fuer unvollstaendige Task-Erstellung in `TaskService` ist entfernt
  - fokussierte Lehrer-Controller-/Service-Tests erfolgreich

Gezielter Not-Found-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=TaskServiceTest,TaskStatusServiceTest,TaskReviewServiceTest,TeacherMvcExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T21:24:30Z`
- Ergebnis:
  - die letzten generischen `RuntimeException`-Pfade sind durch fachliche Not-Found-Exceptions ersetzt
  - `TeacherMvcExceptionHandler` bildet fehlende Aufgaben und fehlende Status konsistent auf 404 ab
  - fokussierte Service- und Handler-Tests erfolgreich

Gezielter OAuth2-Auth-Fehlerpfad-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=SecurityConfigTest,IdentitySyncServiceTest,DashboardControllerTest,HomeControllerTest test`
- Zeitpunkt: `2026-04-26T21:47:12Z`
- Ergebnis:
  - kaputte OAuth2-Identitaeten laufen in Security-, Dashboard- und Debug-Einstiegen ueber den bestehenden Auth-Fehlervertrag
  - generische `IllegalArgumentException` aus dem Identity-Sync-Pfad ist ersetzt

Gezielter HTML-Fehlerseiten-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=StudentMvcExceptionHandlerTest,TeacherMvcExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T22:03:09Z`
- Ergebnis:
  - gemeinsame 404- und 403-Seiten erhalten je nach MVC-Pfad die passenden Dashboard-Ruecklinks
  - fest verdrahtete Lehrertexte in den generischen HTML-Fehlerseiten sind entfernt

Gezielter Debug-404-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=DebugMvcExceptionHandlerTest,DebugControllerTest test`
- Zeitpunkt: `2026-04-26T22:03:09Z`
- Ergebnis:
  - fehlende Debug-Inhalte laufen ueber die generische 404-Seite mit Ruecklink auf `/debug`
  - der Debug-Content-Viewer ist nur noch Erfolgsansicht

Gezielter Login-Fehlerdarstellungs-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=SecurityConfigTest,HomeMvcExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T22:30:35Z`
- Ergebnis:
  - Login-Redirects unterscheiden jetzt zwischen Auth-Pflicht und OAuth2-Fehlertypen
  - `invalid_user_info` aus dem OAuth2-Loginpfad hat einen eigenen Login-Fehlercode

Gezielter API-Not-Found-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=StudentTaskApiExceptionHandlerTest,StudentTaskApiCommandServiceTest test`
- Zeitpunkt: `2026-04-26T22:35:43Z`
- Ergebnis:
  - fehlende Tasks und fehlende Status laufen im API-Fehlervertrag nicht mehr ueber den generischen `internal_error`-Pfad
  - fokussierte API-Handler- und Command-Service-Regressionen erfolgreich

Gezielter API-Forbidden-/Bad-Request-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=StudentTaskApiExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T22:39:50Z`
- Ergebnis:
  - API-Zugriffsfehler laufen ueber `403 forbidden`
  - ungueltige API-Requests laufen ueber `400 bad_request`

Gezielter API-Security-Entry-Point-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=SecurityConfigTest test`
- Zeitpunkt: `2026-04-26T22:46:11Z`
- Ergebnis:
  - unauthentifizierte und Security-seitig verbotene `/api/**`-Zugriffe liefern jetzt JSON statt Redirect/HTML
  - `SecurityConfig` erzeugt die standardisierten `unauthorized`- und `forbidden`-Bodies fuer den Security-Randpfad

Gezielter API-Konflikt-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=StudentTaskApiExceptionHandlerTest,TaskContentServiceTest,UserTaskServiceTest,StudentTaskApiCommandServiceTest test`
- Zeitpunkt: `2026-04-26T21:01:15Z`
- Ergebnis:
  - Persistenz- und Statuskonflikte laufen in den betroffenen Servicepfaden ueber `TaskInvariantViolationException`
  - `StudentTaskApiExceptionHandler` bildet diese Konflikte auf `409 invalid_state` ab
  - fokussierte API- und Service-Tests erfolgreich

Gezielter Persistenzinvarianten-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=TaskReviewServiceTest,TeacherMvcExceptionHandlerTest,UserTaskServiceTest,TaskContentServiceTest,UserTaskRepositoryDataJpaTest,TaskContentRepositoryDataJpaTest test`
- Zeitpunkt: `2026-04-26T18:57:47Z`
- Ergebnis:
  - Review-Versionen werden gegen vorhandene Content-Versionen validiert
  - zentrale Schreibpfade fuer `UserTask` und `TaskContent` besitzen Guard-Regressionen
  - neue JPA-Slice-Tests fuer `UserTaskRepository` und `TaskContentRepository` laufen gruen
  - die aktuelle SQLite-DDL zeigt weiter, dass echte Mehrspalten-Unique-Constraints erst mit einem Migrationspfad belastbar werden

Gezielter Debug-Fehlerpfad-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=DebugControllerTest,DebugMvcExceptionHandlerTest,HomeControllerTest,HomeMvcExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T18:19:58Z`
- Ergebnis:
  - `DebugController` und `DebugMvcExceptionHandler` auf fachliche Exceptions und gehaerteten Task-Zugriff verifiziert
  - Home-Debug-Pfade bleiben dabei intakt

Gezielter Home-Debug-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=HomeControllerTest,HomeMvcExceptionHandlerTest,DebugControllerTest test`
- Zeitpunkt: `2026-04-26T18:12:35Z`
- Ergebnis:
  - `HomeController`-Debug-Pfad und `HomeMvcExceptionHandler` verifiziert
  - Debug-Modell liefert jetzt auch Gruppen konsistent an die View

Gezielter Iframe-Fehlerpfad-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=TaskControllerTest,TaskIframeQueryServiceTest,StudentMvcExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T18:07:57Z`
- Ergebnis:
  - `TaskController` und `TaskIframeQueryService` auf direkte DTOs und Student-MVC-Fehlerpfade verifiziert
  - unberechtigter Iframe-Zugriff wird nicht mehr ueber implizite Redirects behandelt

Gezielter Student-/Dashboard-Fehlerpfad-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=DashboardControllerTest,StudentControllerTest,StudentTaskQueryServiceTest,StudentMvcExceptionHandlerTest,TaskControllerTest test`
- Zeitpunkt: `2026-04-26T17:09:46Z`
- Ergebnis:
  - fachliche Student-/Dashboard-Exceptions und zentraler MVC-Handler verifiziert
  - direkte Student-History-/Versions-DTO-Pfade erfolgreich getestet

Gezielter Lehrer-Fehlerpfad-Testlauf:

- Befehl: `mvn -q -Dmaven.repo.local=/tmp/m2 -Dtest=TeacherControllerTest,TeacherGroupControllerTest,TeacherTaskControllerTest,TeacherTaskCommandServiceTest,TeacherMvcExceptionHandlerTest test`
- Zeitpunkt: `2026-04-26T16:10:44Z`
- Ergebnis:
  - fachliche Lehrer-Exceptions und zentraler MVC-Handler verifiziert
  - Tests erfolgreich

Gezielter Teacher-/Cleanup-Rueckbau-Testlauf:

- Befehl: `mvn -Dmaven.repo.local=/tmp/m2 -Dtest=TeacherDashboardQueryServiceTest,TeacherControllerTest,TeacherTaskQueryServiceTest test`
- Zeitpunkt: `2026-04-20T13:27:20Z`
- Ergebnis:
  - Tests: `13`
  - Failures: `0`
  - Errors: `0`
  - Skipped: `0`

Letzter erfolgreicher vollstaendiger Testlauf:

- Befehl: `mvn -Dmaven.repo.local=/tmp/m2 test`
- Zeitpunkt: `2026-04-20T13:28:36Z`
- Ergebnis:
  - Tests: `139`
  - Failures: `0`
  - Errors: `0`
  - Skipped: `0`
