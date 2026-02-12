Turingmaschinen simulieren und testen.

#### Bsp. Aufgabenbeschreibung:
Aufgabenbeschreibung als Markdown-Text.

#### Bsp. Tutorial
wird nicht unterstützt

#### Bsp. Standard-Vorlage
```
{
    "version": 1,
    "modelName": "Busy Beaver 3",
    "rulesText": "A _ B 1 R\nA 1 C 1 L\nB _ A 1 L\nB 1 B 1
  R\nC _ B 1 L\nC 1 H 1 R",
    "tape": [
      "1100",
      "111110011""
    ],
    "speed": 300,
    "config": {
      "LEFT": "L",
      "RIGHT": "R",
      "BLANK": "_",
      "INIT_STATE": "INIT",
      "HALT_STATE": "HALT",
      "COMMENT_PREFIX": "//",
      "MAX_STATES": 1024,
      "MAX_TAPE_LEN": 1048576,
      "MAX_STATE_SIZE": 32,
      "TRANSITION_SIZE": 710000
    }
  }
```
- `modelName`: Name der Turingmaschine
- `rulesText`: Regeln, die bereits geladen sind, zum Beispiel wenn Fehler gesucht werden sollen. Ansonsten leerer String.
- `tape`: String oder Array von Strings mit Startbändern.
- `config`: Ein paar Konfigurationsmöglichkeiten für die Turing Maschine, am Besten unverändert lassen.