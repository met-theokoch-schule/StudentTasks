Mit diesem Aufgabentyp kann man Automaten erstellen und bearbeiten. Er basiert auf flaci.com.

#### Bsp. Aufgabenbeschreibung:
Aufgabenbeschreibung als Markdown-Text.

LaTeX-Formeln in der Aufgabenbeschreibung werden ueber KaTeX gerendert. Unterstuetzt werden `$...$`, `$$...$$`, `\(...\)` und `\[...\]`.

#### Bsp. Tutorial
wird nicht unterstützt

#### Bsp. Standard-Vorlage
```json
{
  "config": {
    "type": "DEA",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "abba", "inLanguage": true },
      { "word": "bbb", "inLanguage": false }
    ]
  },
  "content": {
    "name": "Beispielautomat",
    "type": "DEA",
    "automaton": {
      "Alphabet": ["a", "b"],
      "allowPartial": false,
      "States": []
    }
  }
}
```

#### Leere Default-Beispiele je Automatentyp

Die folgenden Beispiele setzen bewusst alle bekannten Konfigurationsfelder explizit auf ihre Default-Werte, auch wenn einzelne Angaben redundant sind.

1. `DEA`
```json
{
  "config": {
    "type": "DEA",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "", "inLanguage": false }
    ]
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "DEA",
    "automaton": {
      "Alphabet": [],
      "allowPartial": false,
      "States": []
    }
  }
}
```

2. `NEA`
```json
{
  "config": {
    "type": "NEA",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "", "inLanguage": false }
    ]
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "NEA",
    "automaton": {
      "Alphabet": [],
      "States": []
    }
  }
}
```

3. `MEALY`
```json
{
  "config": {
    "type": "MEALY",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "", "expectedOutput": "" }
    ]
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "MEALY",
    "automaton": {
      "Alphabet": [],
      "StackAlphabet": [],
      "allowPartial": false,
      "States": []
    }
  }
}
```

4. `MOORE`
```json
{
  "config": {
    "type": "MOORE",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "", "expectedOutput": "" }
    ]
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "MOORE",
    "automaton": {
      "Alphabet": [],
      "StackAlphabet": [],
      "allowPartial": false,
      "States": []
    }
  }
}
```

5. `DKA`
```json
{
  "config": {
    "type": "DKA",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "", "inLanguage": false }
    ]
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "DKA",
    "automaton": {
      "Alphabet": [],
      "StackAlphabet": [],
      "States": []
    }
  }
}
```

6. `NKA`
```json
{
  "config": {
    "type": "NKA",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": [
      { "word": "", "inLanguage": false }
    ]
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "NKA",
    "automaton": {
      "Alphabet": [],
      "StackAlphabet": [],
      "States": []
    }
  }
}
```

7. `TM`
```json
{
  "config": {
    "type": "TM",
    "showSimulation": true,
    "lockAlphabetEditing": false,
    "lockAllowPartialEditing": false,
    "testWords": []
  },
  "content": {
    "name": "Aufgabenautomat",
    "type": "TM",
    "automaton": {
      "Alphabet": [],
      "StackAlphabet": [],
      "States": []
    }
  }
}
```

- `type`: Gewuenschter Automatentyp. Unterstuetzte Werte sind `DEA`, `NEA`, `MEALY`, `MOORE`, `DKA`, `NKA`, `TM`.
- `showSimulation`: Blendet den Simulations-Button und die Simulationsansicht aus, wenn der Wert explizit `false` ist. Jeder andere Wert blendet die Simulation ein.
- `lockAlphabetEditing`: Wenn `true`, bleiben die Alphabet-Eingabefelder sichtbar, koennen aber nicht veraendert werden.
- `lockAllowPartialEditing`: Wenn `true`, bleibt die Option `allowPartial` sichtbar, kann aber nicht veraendert werden.
- `testWords`: Optionales Array fuer die Test-Ansicht. Die Ansicht wird fuer alle Typen ausser `TM` angezeigt.
- `content`: Der Inhalt, der dem Lernenden als Basis gezeigt wird.
- `content.name`: Anzeigename des Automaten. Wenn nicht gesetzt, wird `Aufgabenautomat` verwendet.
- `content.type`: Typ des konkreten Inhalts. Wird nur verwendet, wenn kein Typ in `config` vorgegeben ist.
- `content.automaton`: Der eigentliche FLACI-Automat.

#### Typ-Prioritaet

Der Typ des Editors wird in dieser Reihenfolge bestimmt:

1. `config.automatonType`
2. `config.type`
3. `config.machineType`
4. `config.defaultType`
5. `content.type`
6. automatische Erkennung aus dem Automateninhalt
7. Fallback `DEA`

#### Unterstuetzte Content-Formate

Zusatzlich zum gezeigten Standardformat akzeptiert der TaskView auch aeltere oder direktere Formate:

1. Direktes Envelope-Format:
```json
{
  "config": { "type": "NEA" },
  "content": {
    "name": "Beispiel",
    "type": "NEA",
    "automaton": {
      "Alphabet": ["a"],
      "States": []
    }
  }
}
```

2. Content mit `JSON` statt `automaton`:
```json
{
  "content": {
    "name": "Beispiel",
    "type": "DEA",
    "JSON": "{\"Alphabet\":[\"a\"],\"States\":[]}"
  }
}
```

3. Direkter Automat ohne Wrapper:
```json
{
  "Alphabet": ["a", "b"],
  "States": []
}
```

4. Objekte mit `defaultContent` oder verschachteltem `content` werden ebenfalls rekursiv ausgewertet.

#### Testfaelle in `config.testWords`

Die Test-Ansicht orientiert sich an der bestehenden `kfgedit`-Loesung und erscheint als rechte Seitenansicht.

Fuer Akzeptoren (`DEA`, `NEA`, `DKA`, `NKA`) erwartet `testWords` Eintraege mit Eingabewort und Soll-Ergebnis:

```json
{
  "config": {
    "type": "NEA",
    "testWords": [
      { "word": "ab", "inLanguage": true },
      { "word": "ba", "inLanguage": false },
      { "word": ["a", "b", "a"], "inLanguage": true }
    ]
  }
}
```

- `word`: Eingabewort. Entweder als String oder als Array von Symbolen.
- `inLanguage`: Erwartetes Ergebnis. Unterstuetzte Aliase sind `isInLanguage`, `inL` und `accepted`.
- Ein reiner String-Eintrag wie `"abba"` ist ebenfalls erlaubt und wird als `inLanguage: true` interpretiert.

Fuer Transduktoren (`MEALY`, `MOORE`) wird statt eines Bool-Werts das erwartete Ausgabewort angegeben:

```json
{
  "config": {
    "type": "MEALY",
    "testWords": [
      { "word": "ab", "expectedOutput": "01" },
      { "word": ["a", "a", "b"], "expectedOutput": ["x", "x", "y"] }
    ]
  }
}
```

- `expectedOutput`: Erwartetes Ausgabewort. Unterstuetzte Aliase sind `output`, `out` und `expected`.
- Auch hier sind Strings und Symbol-Arrays moeglich.

Hinweise:

- Fuer `TM` wird bewusst keine Test-Ansicht eingeblendet, weil Turing-Maschinen nicht zwingend terminieren.
- Beim Anklicken eines Testworts wird es in die Simulations-Eingabe uebernommen.
- Bei `MEALY` und `MOORE` gilt ein Test nur dann als bestanden, wenn die Eingabe vollstaendig verarbeitet wurde und das resultierende Ausgabewort exakt passt.

#### Eingebettete TaskView-Daten

Die View liest ihre Daten aus diesen HTML-Elementen:

- `defaultSubmission`: Standardinhalt im oben beschriebenen JSON-Format.
- `currentContent`: Bereits gespeicherter Inhalt. Wenn vorhanden, hat er Vorrang vor `defaultSubmission`.
- `description`: Aufgabenbeschreibung als Markdown.
- `tutorial`: Zusatzhinweise als HTML.
- `task-save-url`: Save-URL aus `data-url`.
- `task-submit-url`: Submit-URL aus `data-url`.
- `default-link`: Basis-URL fuer Home-, Save- und Submit-Links.

#### Hinweise zum Automateninhalt

- Im Editor gibt es keine vorgefertigten Alphabet-Presets mehr. In allen Modi bleibt nur noch die freie Alphabet-Eingabe erhalten.
- Fuer `DEA`, `MEALY` und `MOORE` wird bei leeren Vorlagen automatisch `allowPartial: false` gesetzt.
- Fuer `DKA`, `NKA`, `TM`, `MEALY` und `MOORE` wird bei leeren Vorlagen automatisch ein leeres `StackAlphabet` angelegt.
- Bei `lockAlphabetEditing: true` sind die Alphabet-Felder schreibgeschuetzt.
- Bei `lockAllowPartialEditing: true` ist der `allowPartial`-Schalter schreibgeschuetzt.
- Wenn weder `config` noch `content.type` einen Typ setzen, versucht der Editor den Typ heuristisch aus Zustands-Outputs und Transition-Labels abzuleiten.
