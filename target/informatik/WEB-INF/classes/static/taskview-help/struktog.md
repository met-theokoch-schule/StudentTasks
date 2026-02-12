Ermöglicht es einfach Struktogramme zu erstellen.

#### Bsp. Aufgabenbeschreibung:
Aufgabe als Markdown Text. Wird durch marked.js geparsed.

#### Bsp. Tutorial
wird nicht unterstützt

#### Bsp. Standard-Vorlage
Beispiel für ein leeres Struktogramm:
```
{
    "version": "1.4.0",
    "config": "python",
    "showCodeButton": false,
    "tree": {
      "id": "root-1",
      "type": "InsertNode",
      "followElement": {
        "type": "Placeholder"
      }
    }
  }
```

Den tree aus /debug/content/{taskId} extrahieren und einfügen für nicht leere Beispiele.<br>
`config` (mögliche Werte): 
 - `python`
 - `python_simple`
 - `python_if`
 - `python_loop`
 - `python_for`
 - `python_while`
 - `python_if_loop`
 - `python_function`

`showCodeButton`: Legt fest, ob der Button, der einem Boilerplate Code aus dem Struktogramm erzeugt, angezeigt wird.

#### Funktionsvergleichsmatrix

| Funktion | default | python | python_simple | python_if | python_loop | python_for | python_while | python_if_loop | python_function | standard |
|----------|---------|--------|---------------|-----------|-------------|------------|--------------|----------------|-----------------|----------|
| **Eingabe-Knoten** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Ausgabe-Knoten** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Aufgaben-Knoten** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Zählerschleife** | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ |
| **Kopfschleife** | ✅ | ✅ | ❌ | ❌ | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ |
| **Fußschleife** | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| **Verzweigungsknoten** | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| **Fallunterscheidung** | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| **Funktionsknoten** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| **Try-Catch-Knoten** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |