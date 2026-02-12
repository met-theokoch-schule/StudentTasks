Ein Hamstersimulator für Python in HTML,CSS,JS.

#### Bsp. Aufgabenbeschreibung:
Aufgabe als Markdown Text. Wird durch marked.js geparsed.

#### Bsp. Tutorial
```
[
    {
        "title": "HTML Grundlagen",
        "content": "# Tutorial 1:/nHTML Grundlagen\n\nText"
    },
    {
        "title": "CSS Grundlagen",
        "content": "# Tutorial 2"
    },
    {
        "title": "Layout mit CSS",
        "content": "# Tutorial 3"
    }
]
```
**title**: Wird momentan nicht benutzt.<br>
**content**: Inhalt einer Tutorialseite als marked.js Text.

#### Bsp. Standard-Vorlage
```
{
  "configurations": [
    {
      "name": "Einfach",
      "hamsterX": 1,
      "hamsterY": 1,
      "hamsterDirection": "east",
      "hamsterGrains": 0,
      "territory": [
        "w|0|0|w|w|0|0|w|0|0|w|w|0|0|w",
        "0|2|1|0|0|2|1|0|2|1|0|0|2|1|0"
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
  "defaultContent": "# Hamster-Programm\nvor()\nlinksUm()\nvor()\nnimm()\ngib()"
}
```
**configurations**:Die unterschiedlichen Territorien und Startpositionen, welche einfach gewechselt werden können.<br>
**name**: nicht benutzt momentan<br>
**hamsterX**, **hamsterY**: Startcoordianten von oben links gezählt.<br>
**hamsterDirection**: north, west, east, south - Blickrichtung des Hamsters.<br>
**hamsterGrains**: Körner im Maul beim Start<br>
**territory**: Ein Zeilenweises Array des Territoriums. **w**: Wand, **number**: Anzahl der Körner auf dem Feld, 0 bedeutet leeres Feld.<br>
**defaultContent**: Der Test im Python Editor beim ersten Laden der Aufgabe.