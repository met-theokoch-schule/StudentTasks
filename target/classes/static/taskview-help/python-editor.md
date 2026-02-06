Ein Python-Editor und Runner komplett Clientseitig.

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

#### Objektorientierte Programmierung
Obejkte können als "Objektkarten" oberhalb der Konsole angezeigt werden. Dafür werden die Klassen markiert:
```
#@displayable
#@image: https://theokoch.schule/informatik/images/python/magierin.png
class Heldin:
    #@show(name="__name", label="Name")
    #@show(name="__magie", label="Magie")
    def __init__(self, name):
        self.__name = name
        self.__magie = 100
```