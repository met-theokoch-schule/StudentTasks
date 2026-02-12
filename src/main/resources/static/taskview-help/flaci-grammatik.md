Mit diesem Aufgabentyp kann Grammatiken und Syntaxdiagramme erstellen. Er basiert auf flaci.com.

#### Bsp. Aufgabenbeschreibung:
Aufgabenbeschreibung als Markdown-Text.

#### Bsp. Tutorial
wird nicht unterstützt

#### Bsp. Standard-Vorlage
```
{
"config": {
  "showTransformieren":false,
  "startView": "grammatik",
  "showModeSwitch": true,
  "showPruefen": false,
  "showRandomWord": false,
  "showExport": false,
  "testWords": [
    { "word": "ab", "inLanguage": true },
    { "word": "a", "inLanguage": false }
  ]
},
  "content": {}
}
```
- `showTransformieren`: Soll der Transformeiren Button von Flaci gezeigt werden, der es ermöglicht gewisse Operationen wie das vereinfachen der Regeln auszuführen.
- `startView`: In welcher Ansicht die Aufgabe startet `grammatik` oder `syntaxdiagramm`
- `showModeSwitch`: Erlaubt das Umschalten zwischen Grammatik und Syntaxdiagramm.
- `showPruefen`: Ob der Button, der überprüft dass es eine L1 bzw. reguläre Grammatik ist, angezeigt wird.
- `showRandomWord`: Ob der Button zum erzeugen eines Zufallswortes angezeigt wird.
- `showExport`: Ob der Button "Exportieren" angezeigt wird, dieser ermöglicht es eine JSON Datei herunterzuladen, um es in der Vollversion von Flaci zu nutzen (Umwandlung zu Automaten ect.)
- `testWords`: Wörter, für die man mit einem Buttondruck einen Test durchführen kann.
- `content`: Der Inhalt, der dem Lernenden als Basis gezeigt wird. Direkt den `content` Teil aus /debug/content/{taskId} einfügen.
