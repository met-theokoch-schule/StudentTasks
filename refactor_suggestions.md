# Refactor Suggestions

Stand: 2026-04-27

## Ziel dieser Analyse

Diese Analyse betrachtet die aktuelle Codebasis so, als waere noch kein frueherer Refactor-Kontext bekannt. Der Fokus liegt deshalb nicht auf bereits erledigten Slices, sondern auf der Frage:

- Welche Refactorings sind fuer dieses Projekt jetzt noch wirklich wichtig?
- Welche Themen sind eher nice-to-have?
- Welche Dinge sollte man bei maximal etwa 30 gleichzeitigen Nutzern bewusst nicht ueberoptimieren?

Rahmenannahme:

- Das System soll vor allem gut weiterentwickelbar und fachlich sicher bleiben.
- Maximale Performance ist aktuell kein Hauptziel.
- Wartbarkeit, klare Verantwortlichkeiten und verlaessliche Zugriffsregeln sind wichtiger als technische Perfektion.

## Kurzfazit

Die Anwendung ist bereits in einer deutlich besseren Form als ein typisches "Controller-Repository-direkt" Spring-Boot-Projekt. Es gibt inzwischen klar erkennbare Query-/Command-Schnitte, fachliche Exceptions, DTOs fuer grosse View-Pfade und eine gehaertete SQLite-/Flyway-Schiene.

Die wichtigsten verbleibenden Themen sind aber nicht mehr "mehr Architektur" im abstrakten Sinn, sondern vor allem diese vier Punkte:

1. Zugriffsregeln und Autorisierung konsistent und lueckenlos machen.
2. Entity- und Service-Vertraege bereinigen, damit die Weiterentwicklung nicht an technischen Randfaellen haengt.
3. Referenz- und Initialdaten strategisch ordnen, damit Runtime-Bootstrap und Migrationen nicht parallel gegeneinander arbeiten.
4. Einige grosse, ueberlappende Klassen und Web-Pfade weiter entflechten, aber nur dort, wo es echten Wartungsnutzen bringt.

Alles darueber hinaus sollte aktuell bewusst niedrig priorisiert bleiben.

## Positiver Ist-Zustand

Diese Punkte sind aus heutiger Sicht bereits tragfaehig und muessen nicht kurzfristig noch einmal gross umgebaut werden:

- Die fachlichen Kernobjekte `Task`, `UserTask`, `TaskContent`, `TaskReview`, `TaskView` und `TaskStatus` sind klar genug modelliert.
- Die Query-Schicht fuer Schueler- und Lehreransichten ist erkennbar aus den groessten Controllern herausgezogen.
- Die Fehlerpfade sind fuer MVC und API weitgehend typisiert statt rein generisch.
- Die SQLite-Datenhaltung ist mit Flyway, Baseline und gezielten Migrations-/Bootstrapping-Tests inzwischen deutlich solider als frueher.
- Die Testbasis ist fuer die wichtigsten Service-, MVC- und Migrationspfade bereits brauchbar.

Das bedeutet: Es braucht jetzt keinen kompletten Neuansatz mehr. Es geht um gezielte Aufraeum- und Absicherungsschritte.

## Prioritaet A: Jetzt wirklich wichtig

### 1. Autorisierung und Zugriffsregeln zentralisieren und schliessen

Das ist aus heutiger Sicht das wichtigste verbleibende Thema.

#### Warum das wichtig ist

Einige Zugriffsregeln sind sauber umgesetzt, andere aber noch inkonsistent oder zu locker. Das ist gefaehrlicher als technische Doppelungen, weil hier fachlich falsches Verhalten moeglich wird.

#### Konkrete Beobachtungen

- `StudentTaskApiAccessService` prueft fuer Student-API-Zugriffe aktuell nur, ob User und Task existieren, aber nicht, ob der User auf diese Aufgabe zugreifen darf.
  Dateien:
  - `src/main/java/com/example/studenttask/service/StudentTaskApiAccessService.java:29`
  - `src/main/java/com/example/studenttask/service/StudentTaskApiAccessService.java:35`

- Darauf aufbauend koennen `saveTaskContent(...)` und `submitTask(...)` fuer beliebige Task-IDs arbeiten, solange Task und Benutzer existieren.
  Dateien:
  - `src/main/java/com/example/studenttask/service/StudentTaskApiCommandService.java:40`
  - `src/main/java/com/example/studenttask/service/StudentTaskApiCommandService.java:62`

- Auch die Teacher-Schreib-API ist nur rollenbasiert gesichert. `saveTeacherTaskContent(...)` validiert nicht, ob der Lehrer zu diesem `UserTask` fachlich ueberhaupt Zugriff haben darf.
  Dateien:
  - `src/main/java/com/example/studenttask/controller/StudentTaskApiController.java:35`
  - `src/main/java/com/example/studenttask/service/StudentTaskApiCommandService.java:23`

- Im Lehrerbereich ist die Ownership-/Access-Policy nicht konsistent:
  - `deleteTask(...)` prueft Ersteller-Eigentum.
  - `updateTask(...)` tut das nicht.
  Dateien:
  - `src/main/java/com/example/studenttask/service/TeacherTaskCommandService.java:52`
  - `src/main/java/com/example/studenttask/service/TeacherTaskCommandService.java:60`

- Auch `getSubmissionReviewData(...)` und `getSubmissionContentViewData(...)` arbeiten nur ueber `userTaskId`, ohne Lehrerzugriff an derselben Stelle zu validieren.
  Datei:
  - `src/main/java/com/example/studenttask/service/TeacherTaskQueryService.java:71`

- Die Controller-Policy driftet:
  - `TeacherTaskController` und `TeacherController` erlauben `ROLE_ADMIN`.
  - `TeacherGroupController` erlaubt nur Lehrerrollen.
  Dateien:
  - `src/main/java/com/example/studenttask/controller/TeacherTaskController.java:37`
  - `src/main/java/com/example/studenttask/controller/TeacherController.java:25`
  - `src/main/java/com/example/studenttask/controller/TeacherGroupController.java:26`

- Die Student-Webpfade selbst sind nur ueber allgemeine Authentifizierung geschuetzt, aber nicht explizit ueber eine Student-Policy. Ob Lehrer/Admins diese Pfade direkt aufrufen duerfen sollen, ist damit nicht klar im Code ausgedrueckt.
  Datei:
  - `src/main/java/com/example/studenttask/controller/StudentController.java:18`

#### Empfehlung

Als naechster echter Refactoring-Block sollte eine zentrale Access-Policy-Schicht eingefuehrt oder geschaerft werden, zum Beispiel:

- `TaskAccessService`
- `TeacherTaskAccessService`
- oder eine konsolidierte Policy-Schicht fuer Student/Teacher/API

Diese Schicht sollte zentral entscheiden:

- darf Student Aufgabe sehen?
- darf Student `UserTask` anlegen?
- darf Student speichern/submitten?
- darf Lehrer Aufgabe bearbeiten?
- darf Lehrer bestimmte Abgaben sehen oder veraendern?

#### Was ich konkret empfehlen wuerde

1. Student-API zuerst haerten.
2. Teacher-Ownership fuer Edit/Review/Content-Zugriffe nachziehen.
3. Negative Tests fuer "existiert, aber nicht berechtigt" ergaenzen.

Das ist sowohl fachlich als auch fuer spaetere Weiterentwicklung der wichtigste Block.

### 2. Entity-Gleichheit und Identitaet bereinigen

Das ist kein kosmetisches Thema, sondern eine echte Stabilitaetsfrage.

#### Warum das wichtig ist

Mehrere Entities behandeln zwei unpersistierte Objekte aktuell als gleich. Das fuehrt leicht zu Fehlern in Sets, Vergleichen, Mocking und spaeteren Refactors.

#### Konkrete Beobachtungen

- `Task`, `User`, `Group`, `Role`, `UserTask`, `TaskContent`, `TaskReview`, `TaskView` verwenden das Muster:
  - wenn `id == null`, dann gilt Objekt A als gleich Objekt B, falls auch dort `id == null` ist
  Dateien:
  - `src/main/java/com/example/studenttask/model/Task.java:181`
  - `src/main/java/com/example/studenttask/model/User.java:206`
  - `src/main/java/com/example/studenttask/model/UserTask.java:140`
  - `src/main/java/com/example/studenttask/model/TaskContent.java:110`
  - `src/main/java/com/example/studenttask/model/TaskReview.java:105`
  - `src/main/java/com/example/studenttask/model/TaskView.java:118`
  - `src/main/java/com/example/studenttask/model/Group.java:90`
  - `src/main/java/com/example/studenttask/model/Role.java:64`

Das ist fuer JPA-Entities unguenstig, weil zwei neue Objekte fachlich nicht automatisch dasselbe Objekt sind.

#### Empfehlung

Equals/HashCode fuer Entities vereinheitlichen:

- Entweder nur persistierte IDs fuer Equality verwenden.
- Oder fuer unpersistierte Entities explizit `false` liefern, wenn nicht dieselbe Instanz.

Das sollte bewusst als eigener kleiner Slice gemacht werden, mit gezielten Modell- und Service-Tests. Fuer die Wartbarkeit ist das wichtiger als fast jede Query-Optimierung.

### 3. Lookup-Vertraege vereinheitlichen: `Optional` statt `null`-Mischform

#### Warum das wichtig ist

Die Services verwenden derzeit gemischte Lookup-Vertraege. Manche geben `Optional<T>` zurueck, manche `null`, manche werfen fachliche Exceptions. Das macht Aufrufer komplizierter und foerdert kleine Schutzlogik an vielen Stellen.

#### Konkrete Beobachtungen

- `TaskViewService.findById(...)` liefert `Optional<TaskView>`.
  Datei:
  - `src/main/java/com/example/studenttask/service/TaskViewService.java:36`

- `GroupService.findById(...)` liefert `Group` oder `null`.
  Datei:
  - `src/main/java/com/example/studenttask/service/GroupService.java:31`

- `UnitTitleService.findById(...)` liefert `UnitTitle` oder `null`.
  Datei:
  - `src/main/java/com/example/studenttask/service/UnitTitleService.java:20`

- Entsprechend stehen im Code Mischformen wie:
  - `orElseThrow(...)`
  - `if (x == null)`
  - `findById(...).isPresent()`

#### Empfehlung

Nicht alles blind auf einmal umbauen, aber kuenftig einen Standard festlegen:

- Lookup nach ID oder technischem Schluesel: `Optional<T>`
- Fachlich zwingend benoetigte Objekte: `require...()`-Methoden, die typed Exceptions werfen

Beispiel:

- `findGroup(...) -> Optional<Group>`
- `requireGroup(...) -> Group`

Das reduziert Streuung im Code und vereinfacht Controller- und Service-Refactors spaeter deutlich.

### 4. Service-Oberflaechen entduplizieren und auf echte Verantwortungen zuschneiden

#### Warum das wichtig ist

Ein Teil der Services ist funktional okay, aber API-seitig ueber Jahre gewachsen. Dadurch gibt es Alias-Methoden, Mischverantwortungen und Namen, die sich nur minimal unterscheiden.

#### Konkrete Beobachtungen

- `TaskService` ist mit 286 Zeilen die groesste klassische Allround-Serviceklasse und enthaelt:
  - CRUD
  - Aktivierung/Deaktivierung
  - Zugriffspruefungen
  - Statistik
  - mehrere Alias-Methoden fuer dieselben Repository-Abfragen
  Datei:
  - `src/main/java/com/example/studenttask/service/TaskService.java:18`

- Beispiele fuer Redundanz in `TaskService`:
  - `findTasksByCreator(...)`
  - `findByCreatedBy(...)`
  - `findByCreatedByOrderByCreatedAtDesc(...)`
  - `findActiveTasksByCreator(...)`
  - `findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(...)`
  - `deleteTask(...)`
  - `delete(...)`
  - `deleteById(...)`
  Dateien:
  - `src/main/java/com/example/studenttask/service/TaskService.java:69`
  - `src/main/java/com/example/studenttask/service/TaskService.java:215`
  - `src/main/java/com/example/studenttask/service/TaskService.java:272`

- Auch `TaskViewService` hat Alias-/CRUD-Ueberlappungen.
  Datei:
  - `src/main/java/com/example/studenttask/service/TaskViewService.java:22`

#### Empfehlung

Hier lohnt kein grosser Big-Bang. Sinnvoll ist ein pragmatischer Rueckbau:

- pro Service doppelte Alias-Methoden entfernen
- Query-Methoden und Command-Methoden klarer trennen
- Zugriffspruefungen aus generischen CRUD-Services herausziehen

Fuer `TaskService` waere mittelfristig dieses Zielbild sinnvoll:

- `TaskQueryService`
- `TaskCommandService`
- `TaskAccessService`

Aber nur schrittweise, wenn ohnehin an den betroffenen Pfaden gearbeitet wird.

## Prioritaet B: Wichtig, aber nach den Sicherheits- und Identitaetsthemen

### 5. Referenzdaten-Strategie ordnen: Flyway und Runtime-Initializer sauber trennen

#### Warum das wichtig ist

Die Strukturmigrationen sind inzwischen versioniert. Referenzdaten werden aber weiterhin zur Laufzeit durch `DataInitializer` gepflegt. Das funktioniert, ist aber strategisch unsauber, weil Schema-Haertung und fachliche Grunddaten auf zwei verschiedenen Mechaniken beruhen.

#### Konkrete Beobachtungen

- `DataInitializer` schreibt Rollen, Task-Status, TaskViews und UnitTitles beim Start in die Datenbank.
  Datei:
  - `src/main/java/com/example/studenttask/config/DataInitializer.java:20`

- Gleichzeitig ist die Datenbankstruktur jetzt ueber Flyway-Migrationen versioniert.
  Dateien:
  - `src/main/resources/db/migration/V2__create_sqlite_baseline_schema.sql:1`
  - `src/main/java/db/migration/V1__add_runtime_unique_constraints.java:1`
  - `src/main/java/db/migration/V5__add_sqlite_foreign_keys.java:1`

#### Bewertung

Das ist kein akuter Fehler. Fuer ein kleines System ist ein Runtime-Seeder grundsaetzlich okay. Aber:

- TaskViews und Statuscodes sind fachlich recht zentral.
- Aenderungen daran sind Teil der Produktlogik.
- Je mehr davon es gibt, desto weniger angenehm ist eine grosse imperative Startup-Klasse.

#### Empfehlung

Hier wuerde ich nicht sofort alles in SQL-Migrationen kippen. Der pragmatische Mittelweg waere:

1. Referenzkataloge aus `DataInitializer` in eigene kleine Katalogklassen extrahieren.
2. `DataInitializer` nur noch als technischer Orchestrator.
3. Spaeter entscheiden:
   - stabile Referenzdaten per Flyway
   - oder bewusst als idempotenter Katalog-Sync zur Laufzeit

Damit verbessert sich die Lesbarkeit deutlich, ohne dass ihr sofort die ganze Saatlogik neu aufbauen muesst.

### 6. Rollen- und Benutzerlogik an einer Stelle konsolidieren

#### Warum das wichtig ist

Es gibt aktuell mehrere Stellen, die Rollenwissen tragen, teilweise mit leicht unterschiedlicher Logik.

#### Konkrete Beobachtungen

- `UserService` hat die robuste Rollenerkennung ueber mehrere Legacy-/Namensvarianten.
  Datei:
  - `src/main/java/com/example/studenttask/service/UserService.java:84`

- `User` enthaelt aber weiterhin `isTeacher()`/`isStudent()`, die nur `"TEACHER"` bzw. `"STUDENT"` pruefen und damit nicht zur aktuellen Rollenrealitaet passen.
  Datei:
  - `src/main/java/com/example/studenttask/model/User.java:194`

- `AuthenticationService.isAdmin()` prueft auf `"ADMIN"` statt auf die in den Default-Daten angelegte Rolle `"ROLE_ADMIN"`.
  Datei:
  - `src/main/java/com/example/studenttask/service/AuthenticationService.java:85`

- Gleichzeitig wird der aktuelle Benutzer im Web-Layer mehrfach direkt ueber `userService.findByOpenIdSubject(...)` geladen, obwohl mit `AuthenticationService` bereits ein eigener Einstiegspunkt existiert.
  Dateien:
  - `src/main/java/com/example/studenttask/service/AuthenticationService.java:25`
  - `src/main/java/com/example/studenttask/controller/StudentController.java:113`
  - `src/main/java/com/example/studenttask/controller/TeacherController.java:60`
  - `src/main/java/com/example/studenttask/controller/TeacherGroupController.java:81`
  - `src/main/java/com/example/studenttask/controller/TeacherTaskController.java:263`

#### Empfehlung

Das ist ein guter kleiner Konsistenz-Slice:

- irrefuehrende Rollenhelfer in Entities entfernen oder korrigieren
- Rollennormalisierung nur in einer zentralen Stelle halten
- Security-Expressions langfristig gegen sprechende Methoden oder ein Policy-Bean ziehen

Das reduziert spaetere "warum ist das hier anders als dort?"-Fehler.

### 7. Constructor Injection als Standard nachziehen

#### Warum das wichtig ist

Die Codebasis nutzt noch sehr breit Feldinjektion mit `@Autowired`. Das ist kein Laufzeitproblem, aber es erschwert Unveraenderlichkeit, macht Abhaengigkeiten weniger sichtbar und fuehrt zu inkonsistentem Stil.

#### Konkrete Beobachtungen

- Sehr viele Controller und Services arbeiten noch mit Feldinjektion.
  Beispiele:
  - `src/main/java/com/example/studenttask/controller/TeacherTaskController.java:40`
  - `src/main/java/com/example/studenttask/service/TeacherTaskQueryService.java:30`
  - `src/main/java/com/example/studenttask/service/StudentTaskViewSupportService.java:16`
  - `src/main/java/com/example/studenttask/config/SecurityConfig.java:38`

#### Empfehlung

Das ist ein klarer Wartbarkeitsgewinn, aber kein Projektblocker. Deshalb:

- nicht als Massenumbau
- sondern opportunistisch bei jeder angeruehrten Klasse

Empfohlener Standard fuer neue oder bearbeitete Klassen:

- `final` Felder
- Konstruktorinjektion
- kein Feld-`@Autowired`

## Prioritaet C: Selektiv sinnvoll, aber bewusst nicht als Grossprojekt

### 8. Query-Komplexitaet nur dort reduzieren, wo sie die Lesbarkeit oder Korrektheit stoert

#### Warum das wichtig ist

Es gibt einige Query-Pfade mit geschachtelten Schleifen und vielen Einzelnachfragen. Das ist bei 30 gleichzeitigen Nutzern kein dringendes Skalierungsthema. Teilweise leidet aber die Lesbarkeit darunter.

#### Konkrete Beobachtungen

- `GroupQueryService` berechnet Matrix, Pending-Zahlen und Last-Activity ueber mehrere verschachtelte Loops und viele `findByUserAndTask(...)`-Abfragen.
  Datei:
  - `src/main/java/com/example/studenttask/service/GroupQueryService.java:69`

- `StudentTaskOverviewService` ist funktional okay, aber die Mischung aus Gruppen-Tasks, Legacy-Aufgaben ausserhalb aktueller Gruppen und on-demand-`UserTask`-Erzeugung ist fachlich dicht.
  Datei:
  - `src/main/java/com/example/studenttask/service/StudentTaskOverviewService.java:28`

#### Bewertung

Das ist aktuell eher ein Wartungs- als ein Performance-Thema. Fuer eure Nutzerzahl braucht ihr kein aggressives `JOIN FETCH`, kein Caching und keine Query-Engine.

#### Empfehlung

Nur selektiv optimieren:

- wenn ein Pfad fachlich schwer lesbar ist
- oder wenn er im Betrieb spaeter wirklich auffaellig wird

Ein vernuenftiger Mittelweg waere:

- einzelne zugeschnittene Repository-Queries fuer Matrix-/Statistikfaelle
- keine breite "wir optimieren jetzt alles mit EntityGraph"-Kampagne

### 9. API-Erfolgskontrakte konsistenter machen

#### Warum das wichtig ist

Die Fehlerantworten der Student-API sind bereits klar strukturiert. Die Erfolgspfade liefern dagegen einfache Strings oder leere Bodies. Das ist fuer das aktuelle Frontend machbar, aber fuer spaetere Erweiterungen unpraktisch.

#### Konkrete Beobachtungen

- Fehler laufen ueber `ApiErrorResponseDto`.
  Datei:
  - `src/main/java/com/example/studenttask/controller/StudentTaskApiExceptionHandler.java:21`

- Erfolg liefert Strings wie:
  - `"Content saved successfully"`
  - `"Content saved successfully (ID: ..., Version: ...)"`
  Dateien:
  - `src/main/java/com/example/studenttask/controller/StudentTaskApiController.java:45`
  - `src/main/java/com/example/studenttask/controller/StudentTaskApiController.java:58`

#### Empfehlung

Keine komplette API-Neuerfindung. Aber fuer neue oder geaenderte Endpunkte lieber:

- kleine Response-DTOS
- einheitliche Felder wie `status`, `contentId`, `version`

Das lohnt sich vor allem fuer spaetere Frontend-Arbeit.

### 10. Teacher-Webformulare und Layout-Fragmente zusammenziehen

#### Warum das wichtig ist

Im Web-Layer gibt es inzwischen gute DTO-/Service-Schnitte, aber die Templates sind teilweise noch stark dupliziert.

#### Konkrete Beobachtungen

- `teacher/task-create.html` und `teacher/task-edit.html` sind beide gross und in weiten Teilen strukturell sehr aehnlich.
  Dateien:
  - `src/main/resources/templates/teacher/task-create.html:1`
  - `src/main/resources/templates/teacher/task-edit.html:1`

- Es existiert ein `layout.html`, das aktuell kaum oder gar nicht sichtbar als gemeinsamer Frame genutzt wird.
  Datei:
  - `src/main/resources/templates/layout.html:1`

- `TeacherTaskController` arbeitet fuer Rueckspruenge mit frei uebergebenem `returnUrl` und mit dem `Referer`-Header. Das ist technisch praktisch, aber als dauerhafter Navigationsvertrag fragil und potenziell missbrauchsfaehig.
  Datei:
  - `src/main/java/com/example/studenttask/controller/TeacherTaskController.java:85`
  - `src/main/java/com/example/studenttask/controller/TeacherTaskController.java:190`

#### Empfehlung

Das ist ein guter Aufraeum-Slice fuer spaeter:

- gemeinsame Teacher-Form-Fragmente extrahieren
- gemeinsame Nav-/Breadcrumb-/Error-Fragmente prufen
- nur dort abstrahieren, wo echte Duplikation besteht

Wichtig: Die grossen spezialisierten TaskView-Templates sind nicht derselbe Fall. Dort sollte man nicht aus Prinzip stark abstrahieren, wenn die Dateien fachlich sehr unterschiedlich sind.

## Themen, die ich aktuell bewusst nicht priorisieren wuerde

### 1. Breite Performance-Optimierung

Nicht prioritieren:

- Caching
- aggressive Query-Projektionen ueberall
- EntityGraph-/Join-Fetch-Kampagnen
- Pool-/Thread-Feintuning

Begruendung:

- max. 30 gleichzeitige Nutzer
- SQLite
- aktuelle Haupthebel liegen eher in Korrektheit und Wartbarkeit

Ausnahme:

- wenn Gruppenmatrix oder bestimmte Lehreransichten im echten Betrieb spaeter spuerenbar langsam werden

### 2. Komplettumbau aller DTOs auf Records

Die DTOs sind aktuell boilerplate-lastig, aber funktional harmlos.

Beispiel:

- `src/main/java/com/example/studenttask/dto/StudentTaskViewDataDto.java:1`

Das ist ein moeglicher spaeterer Stilgewinn, aber kein akuter Refactoring-Block.

### 3. Grosser Umbau der spezialisierten TaskView-Frontends

Einige Templates und JS-Dateien sind sehr gross:

- `src/main/resources/templates/taskviews/html-css-editor.html`
- `src/main/resources/static/js/python-sorting-editor.js`
- `src/main/resources/static/js/struktogramm.js`

Das ist aber fachlich spezieller Editor-Code. Solange dort nicht aktiv neue Features gebaut werden, ist ein grosser Umbau wahrscheinlich mehr Risiko als Nutzen.

Hier gilt:

- nur anfassen, wenn konkret an genau diesem Editor gearbeitet wird
- ansonsten lieber stabile Spezialmodule in Ruhe lassen

### 4. Volle Vereinheitlichung jeder kleinen Stilfrage

Beispiele:

- jede DTO-Klasse in Record umwandeln
- jede Exception-Hierarchie noch feiner schneiden
- jede Methode auf exakt dieselbe Namenskonvention bringen

Diese Dinge kann man nebenbei mitnehmen, aber sie sollten keine eigenen Sprints bekommen.

## Empfohlene Reihenfolge

Wenn ich die naechsten Refactoring-Schritte fuer dieses Projekt priorisieren muesste, waere meine Reihenfolge:

1. Autorisierungs- und Access-Policy-Luecken schliessen.
2. Entity-Equality und Lookup-Vertraege bereinigen.
3. Doppelte/unklare Service-APIs verschlanken, beginnend mit `TaskService`.
4. Referenzdaten aus `DataInitializer` strukturierter machen.
5. Teacher-Form-/Layout-Duplikate bereinigen.
6. Query-Komplexitaet nur selektiv dort abbauen, wo sie Korrektheit oder Lesbarkeit behindert.

## Konkreter naechster pragmatischer Slice

Wenn nur ein einziger sinnvoller naechster Slice gemacht werden soll, wuerde ich diesen empfehlen:

### Access-Policy-Haertung fuer API und Lehrerpfade

Enthalten sollte das:

1. `StudentTaskApiAccessService` so umbauen, dass Student-API nur auf fachlich erlaubte Aufgaben zugreifen kann.
2. Teacher-API-/Review-/Edit-Pfade auf dieselbe Zugriffslogik ziehen.
3. Negative Tests fuer unerlaubte Task-IDs und fremde `UserTask`-IDs ergaenzen.
4. Dabei gleich die uneinheitliche Rollen-/Admin-Policy sichtbar entscheiden.

Warum genau dieser Slice zuerst:

- hoher fachlicher Nutzen
- relativ klar abgegrenzter Umfang
- reduziert echte Fehl- und Missbrauchsmoeglichkeiten
- schafft eine bessere Basis fuer alle weiteren Refactors

## Schlussbewertung

Das Projekt braucht jetzt keinen weiteren breitflaechigen Architekturumbau. Die groben Schichten sind da. Was noch zaehlt, sind gezielte Korrektheit, konsistente Vertraege und die Entfernung einiger gewachsener Inkonsistenzen.

Die wichtigste Leitlinie fuer die naechsten Schritte sollte deshalb sein:

- nicht maximal elegant refactoren
- sondern die Codebasis an den Stellen haerten, an denen spaetere Weiterentwicklung sonst unnoetig riskant oder verwirrend wird

Fuer dieses Projekt heisst das vor allem:

- Zugriff zuerst
- Vertraege danach
- Optimierung nur selektiv
