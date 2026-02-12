Endliche Automaten und Kellerautomaten simulieren und testen.

#### Bsp. Aufgabenbeschreibung:
Aufgabenbeschreibung als Markdown-Text.

#### Bsp. Tutorial
wird nicht unterstützt

#### Bsp. Standard-Vorlage
```
{
    "type": "DFA",
    "content": '',
    "allowConvert": false,
    "controlWords": [
      {
        "word": "abbba",
        "shouldAccept": true
      },
      {
        "word": "aba",
        "shouldAccept": false
      }
    ]
  }
```
- `type`:<br>`DFA`: Deterministischer Endlicher Automat<br>`NFA`: Nichtdeterministischer Endlicher Automat<br>`PDA`: Kellerautomat
- `allowConvert`: Erlaubt das Wechseln zwischen NFA und DFA und die automatische Umwandlung mit der Potenzmengenkonstruktion.
- `content`: Den Code aus /debug/content/{taskId} in `content` einfügen. Die Single Quotes sorgen dafür, dass man die quotes nicht escapen muss und werden vom Taskview abgefangen.
- `controlWords`: Ein Array mit Wörtern, die fest vorgegeben sind, weitere sind möglich und werden pro Bearbeitung abgespeichert.