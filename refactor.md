# Refactoring-Roadmap

Stand: 2026-04-20

## Zielbild

Das Projekt soll auf einen Zustand gebracht werden, in dem:

- Controller nur noch HTTP-, Routing- und View-Verantwortung tragen
- Fachlogik in klar getrennten Query-, Command- und Support-Services liegt
- Status-, Abgabe- und Review-Regeln an einer Stelle definiert sind
- Persistenzregeln nicht nur in Java, sondern auch im Datenmodell belastbar sind
- Betrieb und Deployment ohne lokale Sonderfaelle und eingecheckte Secrets funktionieren

Diese Roadmap ist bewusst komprimiert. Erledigte Detail-Slices stehen nicht mehr einzeln hier, sondern nur noch als zusammengefasster Stand.

## Bereits abgeschlossen

### 1. Testbasis und technischer Cleanup

- tragfaehige Unit- und MVC-Testbasis fuer die kritischen Schueler-, Lehrer- und Statuspfade aufgebaut
- direkte Konsolenausgaben im Produktivcode auf strukturiertes Logging umgestellt

### 2. Statusmodell zentralisiert

- `TaskStatusCode` als typisierte Grundlage eingefuehrt
- Statusuebergaenge im Kernpfad zentralisiert
- Default-Status und Resubmit-Faelle stabilisiert
- Statusdarstellung in Dashboard-, Gruppen- und Review-Pfaden vereinheitlicht

### 3. Web- und Service-Schicht weitgehend entkoppelt

- Student-Dashboard, Aufgabenliste, Historie, Versionen und API-Pfade aus den Controllern geloest
- Teacher-Dashboard-, Teacher-Task-Query- und Teacher-Task-Command-Pfade in Services verschoben
- Gruppenansichten von Controller-internen Strukturen entkoppelt und typisiert

### 4. OAuth2- und Benutzersynchronisation konsolidiert

- Identity-Sync in einen zentralen Pfad gezogen
- doppelte Synchronisationslogik reduziert
- relevante Controller auf den zentralen Identity-Sync umgestellt

### 5. Task- und TaskView-Modell konsolidiert

- `taskView` als kanonischer Fachzugriff etabliert
- Legacy-Fallbacks und Uebergangsaliasse entfernt
- TaskView-Render- und Schreibpfade vereinheitlicht

### 6. Submission-Runtime entfernt und Legacy-Rollout abgeschlossen

- neue und bestehende Abgaben laufen zur Laufzeit nur noch ueber `TaskContent`, `TaskReview` und `UserTask.status`
- `Submission` ist aus der Runtime entfernt
- temporaere Backfill-, Dashboard- und Drop-Helfer wurden nach abgeschlossenem Rollout wieder entfernt

## Offene Aufgaben

### 1. Konfigurations- und Betriebsmodell haerten

Status: teilweise erledigt
Prioritaet: hoch

Bereits erledigt:

- gemeinsame Runtime-Properties von lokalen, staging- und produktiven Profilen getrennt
- OAuth-Client-Secret aus den versionierten Properties entfernt und auf Umgebungsvariablen/JVM-Properties umgestellt
- `ddl-auto=update` auf das lokale Profil begrenzt; staging und Produktion mutieren das Schema nicht mehr implizit
- `.gitignore` um `target/` und lokale SQLite-Artefakte erweitert
- eingecheckte Build- und lokale DB-Artefakte aus dem Git-Index entfernt

Noch offen:

- versionierte DB-Migrationen einfuehren und den Ersatz fuer `ddl-auto=update` vollenden
- konfigurationsnahe Tests fuer staging-/prod-Profile ergaenzen
- verbleibende Betriebswerte und Deploy-Dokumentation weiter auf externe Konfiguration ziehen

Ziel dieses Blocks:

- Secrets auf Umgebungsvariablen oder saubere Profile umstellen
- lokales, staging- und produktives Profil klar trennen
- mittelfristig auf versionierte DB-Migrationen umstellen
- lokale Artefakte aus dem Repository fernhalten

### 2. Fehlerbehandlung und API-Vertraege global standardisieren

Status: teilweise erledigt
Prioritaet: hoch

Bereits erledigt:

- die Student-Task-API hat mit `StudentTaskApiExceptionHandler` einen zentralen REST-Fehlerpfad
- Persistenz- und Statuskonflikte aus den Student-Task-Schreibpfaden laufen jetzt ebenfalls ueber einen fachlichen Konfliktvertrag statt ueber rohe `IllegalStateException`
- die verbliebenen generischen `RuntimeException`-Pfade fuer fehlende Aufgaben und fehlende Status sind durch fachliche Not-Found-Exceptions ersetzt
- die Student-Task-API mappt fehlende Tasks und fehlende Status aus ihren Schreibpfaden jetzt ebenfalls explizit auf `404 not_found` statt auf den generischen `internal_error`-Pfad
- die Student-Task-API bildet nun auch `403 forbidden` und `400 bad_request` ueber denselben JSON-Fehlervertrag ab
- unauthentifizierte und Security-seitig verbotene `/api/**`-Zugriffe werden jetzt ebenfalls direkt als JSON (`401`/`403`) beantwortet statt ueber Browser-Redirects oder HTML-Fehlerseiten
- kaputte OAuth2-Identitaeten laufen in Login-, Dashboard- und Debug-Einstiegen nicht mehr ueber generische `IllegalArgumentException`- bzw. 500er-Pfade, sondern ueber den bestehenden Auth-Fehlervertrag
- die Lehrer-MVC-Pfade nutzen jetzt fachliche Exceptions statt verteilter Redirect-, Null- und Fallback-Logik
- `TeacherMvcExceptionHandler` zentralisiert Login-Redirect sowie 404-/403-Antworten fuer `TeacherController`, `TeacherGroupController` und `TeacherTaskController`
- die Schueler- und Dashboard-MVC-Pfade nutzen jetzt ebenfalls fachliche Exceptions statt generischer `RuntimeException`s oder stiller Dashboard-Redirects
- `StudentMvcExceptionHandler` zentralisiert Login-Redirect sowie 404-/403-Antworten fuer `DashboardController`, `StudentController` und `TaskController`
- die gemeinsamen HTML-Fehlerseiten `error/404` und `access-denied` sind jetzt kontextsauber fuer Schueler- und Lehrerpfade und zeigen passende Ruecklinks statt fest verdrahteter Lehrertexte
- der Iframe-Pfad in `TaskIframeQueryService` laeuft nicht mehr ueber Redirect-Ergebnisobjekte, sondern ueber direkte DTOs und geprueften Task-Zugriff
- der `/debug`-Pfad im `HomeController` nutzt jetzt ebenfalls eine fachliche Auth-Exception statt Inline-Redirect, und die Debug-Seite bekommt geladene Gruppen konsistent ins Model
- `DebugController` nutzt jetzt fachliche Exceptions statt Inline-Fehlerstrings und prueft den Task-Zugriff ueber `findAssignedTask(...)`
- `DebugMvcExceptionHandler` nutzt fuer fehlende Debug-Inhalte jetzt ebenfalls die generische 404-Seite mit passendem Ruecklink statt einer eigenen Fehlerdarstellung im Content-Viewer
- die Login-Seite unterscheidet jetzt zwischen `auth required`, Session-Ablauf, generischem OAuth-Fehler und dem speziellen `invalid_user_info`-Fehler aus dem OAuth2-Loginpfad

Noch offen:

- vergleichbare Vereinheitlichung fuer einzelne verbleibende Sonder- und Fehlerseiten ausserhalb der Kernpfade
- klare Trennung zwischen HTML-Fehlerpfaden, Redirect-Fehlern und API-Fehlern
- fachliche Exceptions fuer gemeinsame Fehlerfaelle wie Zugriff, Statuswechsel, Not Found

### 3. Versionierungs-, Abgabe- und Review-Modell auf DB-Ebene haerten

Status: teilweise erledigt
Prioritaet: mittel bis hoch

Die alte doppelte Submission-Semantik ist entfernt, aber die Invarianten leben noch zu stark nur im Java-Code.

Bereits erledigt:

- `TaskReviewService` validiert Review-Versionen jetzt explizit gegen vorhandene `TaskContent`-Versionen und lehnt ungueltige Bindungen fachlich ab
- `TeacherMvcExceptionHandler` bildet fehlende Review-Versionen im Lehrerpfad konsistent auf 404 ab
- `TaskContentService` und `UserTaskService` bewachen die zentralen Schreibpfade jetzt zusaetzlich gegen doppelte Versionen bzw. doppelte User-Task-Zuordnungen
- diese Invarianten laufen jetzt ueber `TaskInvariantViolationException` statt ueber generische `IllegalStateException`
- fehlende Aufgaben und fehlende Status laufen jetzt ueber `TaskNotFoundException` bzw. `TaskStatusNotFoundException`
- gezielte Persistenztests fuer `UserTaskRepository` und `TaskContentRepository` sowie neue Service-Regressionstests fuer Review- und Versionsinvarianten sind ergaenzt
- die zusammengesetzten Eindeutigkeiten sind als Entity-Metadaten markiert; die aktuelle SQLite-DDL materialisiert sie ohne Migrationspfad jedoch noch nicht verlaesslich

Noch offen:

- versionierte DB-Migrationen oder ein gleichwertiger Schemamigrationspfad fuer echte Mehrspalten-Unique-Constraints
- verbleibende Persistenztests fuer Historie und bestehende Altdaten-/Duplikatfaelle
- bei Bedarf weitere Konsolidierung angrenzender Invarianten in dedizierte Fach-Exceptions statt generischer `RuntimeException`

### 4. Query- und Performance-Pfade optimieren

Status: offen
Prioritaet: mittel

Die Lesepfade sind heute besser getrennt, aber noch nicht systematisch auf Query-Ebene optimiert.

Noch offen:

- gezielte Projektionen oder Query-Modelle fuer grosse Uebersichten
- Reduktion moeglicher N+1-Pfade
- pragmatischer Einsatz von `JOIN FETCH`, `@EntityGraph` oder zugeschnittenen Repository-Queries

### 5. Letzte Web-Schicht-Reste bereinigen

Status: teilweise erledigt
Prioritaet: mittel

Die groessten Controller sind deutlich entschlackt, aber nicht komplett fertig.

Bereits erledigt in diesem Block:

- Create/Edit im `TeacherTaskController` laufen jetzt ueber ein eigenes `TeacherTaskFormDto` statt ueber direkte Entity-Bindung
- der Lehrer-Formpfad validiert jetzt Aufgabentitel sowie referenzierte TaskViews, Themen und Gruppen serverseitig statt manipulierte Werte still zu ignorieren
- Lehrer-Redirects und Fehlerpfade sind fuer Dashboard-, Gruppen- und Task-Ansichten konsistenter gezogen
- Schueler-History- und Versionspfade laufen jetzt direkter ueber Query-DTOs statt ueber Redirect-Ergebnisobjekte
- der Iframe-Task-Pfad nutzt jetzt direkte `TaskIframeViewDataDto` statt Redirect-Wrappern
- der Home-Debug-Pfad ist auf denselben Auth-Fehlervertrag gezogen und liefert Gruppen konsistent an die View
- der Debug-Content-Viewer arbeitet jetzt mit zentralem Exception-Handling statt verteilter Inline-Fehlerlogik

Noch offen:

- Success-Feedback-Pfade koennen bei Bedarf noch expliziter werden; die groben Form- und Referenzvalidierungen sind jetzt aber serverseitig abgedeckt

### 6. Testbasis gezielt verbreitern

Status: teilweise erledigt
Prioritaet: mittel

Die aktuelle Testbasis ist stark genug fuer weitere Refactorings, aber noch nicht tief genug auf Persistenz- und Betriebsseite.

Noch offen:

- weitere `@DataJpaTest`- oder vergleichbare Persistenztests fuer Historie und migrationsnahe Persistenzfaelle
- Testprofil fuer migrations- und konfigurationsnahe Szenarien
- gezielte Regressionstests fuer produktionsrelevante Konfiguration

## Empfohlene Reihenfolge

1. Konfigurations- und Betriebsmodell haerten
2. Fehlerbehandlung und API-Vertraege global standardisieren
3. Versionierungs- und Review-Invarianten auf DB-Ebene haerten
4. Letzte Web-Schicht-Reste in `TeacherTaskController` bereinigen
5. Query- und Performance-Pfade optimieren
6. Persistenz- und Betriebs-Tests verbreitern

## Naechster sinnvoller Slice

Wenn wir klein und risikoarm weitermachen wollen:

- den noch offenen Migrationspfad fuer echte DB-Constraints vorbereiten oder die Persistenztests um Altdaten-/Historienfaelle erweitern

Wenn wir den begonnenen Konfigurationsblock direkt weiter schliessen wollen:

- Konfiguration weiter haerten: Migrationspfad vorbereiten und konfigurationsnahe Tests fuer staging/prod nachziehen
