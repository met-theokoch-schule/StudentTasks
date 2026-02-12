Der HTML-CSS-Editor erlaubt das erstellen von HTML + CSS + Bilder Seiten. Die Vorschau wird bei Änderungen automatisch aktualisiert. Bilder sind in dem abgespeicherten Content eingebettet und haben aus diesem Grund eine relativ kleine maximale Größe.

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