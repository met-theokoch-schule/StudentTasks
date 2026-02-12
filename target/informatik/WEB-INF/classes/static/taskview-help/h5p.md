h5p's werden per iFrame eingebunden und werden extern bereitgestellt. Diese externe Bereitstellung muss die xAPI Nachrichten von dem h5p an den Parentframe weiterleiten.

Korrekt bearbeitete h5p werden ohne Lehrerinteraktion als vollständig markiert.

#### Bsp. Aufgabenbeschreibung:
```
{ 
  "url": "https://theokoch.schule/informatik/h5p/bubblesort/",
  "allowedOrigins": ["https://theokoch.schule"],
  "matchVerbId": "http://adlnet.gov/expapi/verbs/answered", 
  "passScoreScaled": 0.8 
}
```
**url**: URL zum h5p<br>
**allowedOrigins**: Die Quellserver für das h5p, wird benötigt um xAPI Nachrichten zu empfangen.<br>
**matchVerbId**: xAPI Nachricht, die für das automatisch als Vollständig markieren benutzt wird.<br>
Bei Einzelfragen Content häufig:
```
http://adlnet.gov/expapi/verbs/answered
```
bei Fragenkatalogen
```
http://adlnet.gov/expapi/verbs/completed
```
**passScoreScaled**: Die Prozentzahl, die erreicht werden muss, damit die Aufgabe als "Vollständig" markiert wird.

#### Bsp. Tutorial
wird nicht unterstützt

#### Bsp. Standard-Vorlage
wird nicht unterstützt