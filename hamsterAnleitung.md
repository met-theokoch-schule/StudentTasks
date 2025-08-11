
# DefaultSubmission Anleitung für Hamstersimulator

Eine gültige `defaultSubmission` für den Hamstersimulator sollte sowohl die Weltkonfigurationen als auch einen Standard-Python-Code enthalten.

## Vollständiges Beispiel

```json
{
  "configurations": [
    {
      "name": "Einfach",
      "hamsterX": 1,
      "hamsterY": 1,
      "hamsterDirection": "east",
      "hamsterGrains": 0,
      "territory": [
        "w|0|0|w",
        "0|2|1|0"
      ]
    },
    {
      "name": "Komplex",
      "hamsterX": 0,
      "hamsterY": 0,
      "hamsterDirection": "south",
      "hamsterGrains": 3,
      "territory": [
        "w|3|0|w|0",
        "0|w|1|0|2",
        "1|0|0|w|0"
      ]
    }
  ],
  "defaultContent": "# Hamster-Programm - Beispiel\n# Der Hamster sammelt alle Körner ein\n\nwhile not maulLeer():\n    gib()\n\nwhile kornDa():\n    nimm()\n\n# Bewege dich vorwärts wenn möglich\nif vornFrei():\n    vor()\n    if kornDa():\n        nimm()\n\n# Drehe dich um und gehe zurück\nlinksUm()\nlinksUm()\n\nif vornFrei():\n    vor()\n    \n# Lege alle gesammelten Körner ab\nwhile not maulLeer():\n    gib()"
}
```

## Aufbau der Konfiguration

### 1. `configurations` Array
Array mit Weltkonfigurationen, zwischen denen der Schüler wechseln kann.

Jede Konfiguration enthält:
- **`name`**: Bezeichnung der Welt (z.B. "Einfach", "Komplex")
- **`hamsterX/Y`**: Startposition des Hamsters (0-basiert)
- **`hamsterDirection`**: Startrichtung (`"north"`, `"east"`, `"south"`, `"west"`)
- **`hamsterGrains`**: Anzahl Körner im Maul zu Beginn
- **`territory`**: Array mit Strings, die das Spielfeld beschreiben

### 2. Territory Format
Das `territory` ist ein Array von Strings, wobei jeder String eine Zeile repräsentiert:

```
"territory": [
  "w|3|0|w|0",    // Zeile 0: Wand, 3 Körner, leer, Wand, leer
  "0|w|1|0|2",    // Zeile 1: leer, Wand, 1 Korn, leer, 2 Körner
  "1|0|0|w|0"     // Zeile 2: 1 Korn, leer, leer, Wand, leer
]
```

**Feldtypen:**
- `w` = Wand
- `0` = leeres Feld  
- `1,2,3,4...` = Anzahl Körner auf dem Feld
- Getrennt durch `|`
- Leerzeichen werden ignoriert

### 3. `defaultContent`
Python-Code der geladen wird, wenn `currentContent` leer ist.

**Verfügbare Hamster-Befehle:**
- `vor()` - Hamster bewegt sich vorwärts
- `linksUm()` - Hamster dreht sich nach links
- `nimm()` - Hamster nimmt ein Korn auf
- `gib()` - Hamster legt ein Korn ab
- `vornFrei()` - Prüft ob der Weg frei ist (boolean)
- `kornDa()` - Prüft ob ein Korn da ist (boolean)
- `maulLeer()` - Prüft ob das Maul leer ist (boolean)

## Beispiel für einfache Welt

```json
{
  "configurations": [
    {
      "name": "Erste Schritte",
      "hamsterX": 0,
      "hamsterY": 0,
      "hamsterDirection": "east",
      "hamsterGrains": 0,
      "territory": [
        "0|1|0",
        "0|0|2",
        "3|0|0"
      ]
    }
  ],
  "defaultContent": "# Mein erstes Hamster-Programm\n\n# Gehe vorwärts\nvor()\n\n# Nimm das Korn\nif kornDa():\n    nimm()\n\n# Drehe dich nach links\nlinksUm()"
}
```

## Wichtige Hinweise

1. Das JSON muss valide sein (keine Kommentare, richtige Anführungszeichen)
2. Koordinaten sind 0-basiert (beginnen bei 0)
3. Territory-Größe wird automatisch aus dem Array ermittelt
4. Außen um das Territory sind automatisch Wände
5. Der `defaultContent` ersetzt `currentContent` nur wenn dieser leer ist
6. Hamster-Funktionen sind automatisch verfügbar und zeigen keine Fehler im Editor
