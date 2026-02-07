Ein Python-Sorting-Editor wird benutzt um ein Array während der Manipulation darzustellen. Dafür wird heftig in das Skript eingegriffen, so dass jeder read und write auf einem Array abgegriffen wird. Da dies von Python nicht unterstützt wird ist es empfehlenswert den Code "einfach" zu halten und auf überflüssige Änderungen (ändern des Array-Names zB) zu verzichten.

Der Name des Arrays ist **sortarray**.

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