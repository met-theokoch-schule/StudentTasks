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
**title**: Wird momentan nicht benutzt.<br>
**content**: Inhalt einer Tutorialseite als marked.js Text.

#### Bsp. Standard-Vorlage
Direkt den Code aus /debug/content/{taskId} einfügen.

#### Initialisierung
Es ist nötig zuerst ein Turtle Objekt zu erzeugen nachdem die turtle Bibliothek importiert wurde *ODER* die Basisfunktionaltitäten direkt zu nutzen, nachdem man diese als einzelne Funktionen importiert hat.
```python
import turtle
t = turtle.Turtle()
t.forward(100)
```
*ODER*
```python
from turtle import *
forward(100)
```

#### Turtle Befehle
- `forward(distance)` / `fd(distance)` — Vorwärts bewegen.
- `backward(distance)` / `back(distance)` / `bk(distance)` — Rückwärts bewegen.
- `right(angle)` / `rt(angle)` — Rechts drehen.
- `left(angle)` / `lt(angle)` — Links drehen.
- `up()` / `penup()` / `pu()` — Stift heben.
- `down()` / `pendown()` / `pd()` — Stift senken.
- `goto(x, y)` / `setpos(x, y)` / `setposition(x, y)` — Zu Position springen.
- `setx(x)` — X setzen.
- `sety(y)` — Y setzen.
- `setheading(to_angle)` / `seth(to_angle)` — Richtung setzen.
- `home()` — Zur Startposition.
- `circle(radius)` — Kreis/Bogen zeichnen.
- `dot(size, color)` — Punkt zeichnen.
- `speed(speed)` — Geschwindigkeit setzen.
- `color(pencolor)` — Stift/Füllfarbe setzen.
- `pencolor(color)` — Stiftfarbe setzen.
- `fillcolor(color)` — Füllfarbe setzen.
- `pensize(width)` / `width(width)` — Stiftdicke setzen.
- `begin_fill()` — Füllung starten.
- `end_fill()` — Füllung beenden.
- `begin_poly()` — Polygon starten.
- `end_poly()` — Polygon beenden.
- `get_poly()` — Polygonpunkte holen.
- `stamp()` — Stempel setzen.
- `clearstamp(stampid)` — Stempel löschen.
- `clearstamps(n)` — Stempel löschen.
- `undo()` — Letzte Aktion rückgängig.
- `reset()` — Turtle zurücksetzen.
- `clear()` — Zeichnung löschen.
- `hideturtle()` / `ht()` — Turtle verstecken.
- `showturtle()` / `st()` — Turtle zeigen.
- `isdown()` — Stift unten?
- `isvisible()` — Turtle sichtbar?
- `pos()` / `position()` — Position holen.
- `xcor()` — X-Koordinate holen.
- `ycor()` — Y-Koordinate holen.
- `heading()` — Blickrichtung holen.
- `distance(x, y)` — Distanz berechnen.
- `towards(x, y)` — Richtung zu Punkt.
- `clone()` — Turtle klonen.
- `shape(name)` — Form setzen.
- `shapesize(stretch_wid, stretch_len, outline)` / `turtlesize(stretch_wid, stretch_len, outline)` — Formgröße setzen.
- `tilt(angle)` — Neigung setzen.
- `tiltangle(angle)` — Neigungswinkel holen.
- `settiltangle(angle)` — Neigungswinkel setzen.
- `shearfactor(shear)` — Scherfaktor setzen.
- `shapetransform(t11, t12, t21, t22)` — Formtransform setzen.
- `resizemode(rmode)` — Größenmodus setzen.
- `filling()` — Füllstatus prüfen.
- `write(arg)` — Text schreiben.
- `onclick(fun)` — Klick-Callback binden.
- `ondrag(fun)` — Drag-Callback binden.
- `onrelease(fun)` — Release-Callback binden.
- `setundobuffer(size)` — Undo-Buffer setzen.

Weitere Infos zum verwendeten Turtel in https://github.com/RaspberryPiFoundation/turtle bzw. https://docs.python.org/3/library/turtle.html