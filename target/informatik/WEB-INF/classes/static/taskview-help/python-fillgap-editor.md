# Python Lückentext-Modus

Diese Seite zeigt einen vom Lehrer vorgegebenen Python-Code als **nicht editierbaren Codeblock** mit Zeilennummern und Highlighting.
Schüler können nur die markierten Lücken ausfüllen.

## Lücken-Syntax (für `defaultSubmission`)

Verwende im Lehrer-Code folgende Marker:

```text
[[__gap:<id>|<breiteInZeichen>__]]<vorgabeteil>[[/__gap__]]
```

- `<id>` ist optional, sollte aber zur Lesbarkeit gesetzt werden (z. B. `counter`, `bedingung`, `wert`).
- `<breiteInZeichen>` ist optional und steuert die Startbreite des Feldes.
- `<vorgabeteil>` ist nur Lehrkraft-Referenz und wird in der Schüleransicht durch ein Input-Feld ersetzt.
- Lücken sind **nur innerhalb einer einzelnen Zeile** erlaubt.
- Gültige Varianten:
  - `[[__gap__]]...[[/__gap__]]`
  - `[[__gap:steps__]]...[[/__gap__]]`
  - `[[__gap|8__]]...[[/__gap__]]`
  - `[[__gap:steps|8__]]...[[/__gap__]]`

## Beispiel

```python
#@displayable
class Auto:
    #@show(name="geschwindigkeit", label="km/h")
    def __init__(self):
        self.geschwindigkeit = [[__gap:start_speed|5__]]0[[/__gap__]]

auto = Auto()
for i in range([[__gap:steps|3__]]10[[/__gap__]]):
    auto.geschwindigkeit += [[__gap:delta|3__]]2[[/__gap__]]

print("Fertig")
```

## Verhalten in der Schüleransicht

- Der Code außerhalb der Marker ist read-only.
- Jede Lücke wird als eigenes Input-Feld dargestellt.
- Die Felder haben keinen sichtbaren Platzhalter (auch keine ID).
- Gleich benannte IDs werden **nicht** automatisch synchronisiert.
- Leere Inputs bleiben leer (es wird kein Fallback eingesetzt).

## Ausführung

Beim Klick auf `Ausführen` wird der vollständige Python-Code erzeugt aus:

1. festem Lehrercode,
2. Eingaben aus allen Lückenfeldern.

Dieser zusammengesetzte Code wird wie bisher im Browser (Pyodide/Worker) ausgeführt.
Objekt-Viewer-Direktiven (`#@displayable`, `#@show`) funktionieren weiterhin, da sie im finalen Python-Code enthalten sind.

## Speichern

Es werden nur die Schülerantworten pro Lücke gespeichert (nicht der komplette zusammengesetzte Code).

## Hinweise

- Wenn keine Marker im `defaultSubmission` enthalten sind, ist der komplette Code read-only.
- Für Literale mit `[[__gap...`-Text im Python-Code sollten die Zeichenfolgen so angepasst werden, dass sie nicht als Marker interpretiert werden.
