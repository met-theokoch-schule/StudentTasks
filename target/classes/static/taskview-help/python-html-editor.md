Ein Python-Editor der in einem HTML läuft und eine Brücke zum HTML mit [Brython – offizielle Webseite](https://brython.info/) herstellt.

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
**title**: Wird momentan nicht benutzt.
**content**: Inhalt einer Tutorialseite als marked.js Text.

#### Bsp. Standard-Vorlage
Direkt den Code aus /debug/content/{taskId} einfügen.

#### Beispiel für Brython
Python Datei:
```python
# Willkommen im Python-Editor
from browser import document, html
from math import sqrt

def calculate(event=None):
    try:
        p = float(document["p"].value)
        q = float(document["q"].value)
        diskriminante = (p / 2)**2 - q

        if diskriminante < 0:
            document["result"].text = "Keine reellen Lösungen."
        else:
            x1 = -p / 2 + sqrt(diskriminante)
            x2 = -p / 2 - sqrt(diskriminante)
            if x1 == x2:
                document["result"].text = f"x = {x1:.4f}"
            else:
                document["result"].text = f"x₁ = {x1:.4f}, x₂ = {x2:.4f}"
    except ValueError:
        document["result"].text = "Bitte gültige Zahlen eingeben."

document["calc_button"].bind("click", calculate)
```
HTML Datei:
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>p-q-Formel Rechner</title>
</head>
<body>
    <h2>p-q-Formel Rechner</h2>

    <label for="p">p:</label>
    <input type="number" id="p" step="any"><br><br>

    <label for="q">q:</label>
    <input type="number" id="q" step="any"><br><br>

    <button id="calc_button">Berechnen</button>

    <h3>Ergebnis:</h3>
    <div id="result"></div>

</body>
</html>
```