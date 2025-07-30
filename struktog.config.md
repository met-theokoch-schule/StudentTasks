
# Struktog Konfigurationsübersicht

Dieses Dokument bietet eine Übersicht über alle verfügbaren Konfigurationsoptionen in der Struktog-Anwendung.

## Verfügbare Konfigurationen

### 1. **default** (Standardkonfiguration)
Die Basiskonfiguration mit allen aktivierten Knotentypen. Diese dient als Standard-Fallback-Konfiguration.

### 2. **python** 
Vollständige Python-Konfiguration mit allen verfügbaren Programmierkonstrukten, identisch zur Standardkonfiguration, aber speziell für die Python-Programmierausbildung angepasst.

### 3. **python_simple**
Vereinfachte Python-Konfiguration für Anfänger. Nur grundlegende Elemente sind aktiviert:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen
- Code-Generierungsbutton

Alle Schleifentypen, Verzweigungen, Funktionen und erweiterte Konstrukte sind deaktiviert.

### 4. **python_if**
Python-Konfiguration mit Fokus auf bedingte Anweisungen:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen  
- Verzweigungsknoten (if/else-Anweisungen)
- Code-Generierungsbutton

Schleifen, Funktionen und andere erweiterte Konstrukte sind deaktiviert.

### 5. **python_loop**
Python-Konfiguration mit Fokus auf Schleifenkonstrukte:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen
- Zählerschleifen (for-Schleifen)
- Kopfschleifen (while-Schleifen)
- Code-Generierungsbutton

Verzweigungen, Funktionen und andere Konstrukte sind deaktiviert.

### 6. **python_for**
Python-Konfiguration speziell für zählergesteuerte Schleifen:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen
- Nur Zählerschleifen (for-Schleifen)
- Code-Generierungsbutton

Alle anderen Schleifentypen und erweiterte Konstrukte sind deaktiviert.

### 7. **python_while**
Python-Konfiguration speziell für bedingungsgesteuerte Schleifen:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen
- Nur Kopfschleifen (while-Schleifen)
- Code-Generierungsbutton

Alle anderen Schleifentypen und erweiterte Konstrukte sind deaktiviert.

### 8. **python_if_loop**
Python-Konfiguration mit Kombinationen aus Bedingungen und Schleifen:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen
- Verzweigungsknoten (if/else)
- Zählerschleifen (for-Schleifen)
- Kopfschleifen (while-Schleifen)
- Code-Generierungsbutton

Funktionen und erweiterte Konstrukte sind deaktiviert.

### 9. **python_function**
Erweiterte Python-Konfiguration mit Funktionen:
- Eingabe-/Ausgabefelder
- Aufgabenanweisungen
- Alle Schleifentypen (Zähler- und Kopfschleifen)
- Verzweigungsknoten
- Funktionsblöcke
- Code-Generierungsbutton

Try-Catch-Blöcke und Fallunterscheidungen sind deaktiviert.

### 10. **standard**
Vollständige Konfiguration mit allen verfügbaren Knotentypen aktiviert, einschließlich:
- Alle Schleifentypen (Zähler-, Kopf- und Fußschleifen)
- Verzweigungsknoten
- Fallunterscheidungen
- Funktionsblöcke
- Try-Catch-Blöcke
- Code-Generierungsbutton

## Funktionsvergleichsmatrix

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
| **Code-anzeigen Button** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

## Beschreibung der Knotentypen

### Grundlegende Knoten
- **Eingabe-Knoten**: Eingabe-Feld - für Benutzereingabe-Operationen
- **Ausgabe-Knoten**: Ausgabe-Feld - für die Anzeige von Ausgaben
- **Aufgaben-Knoten**: Anweisung - für allgemeine Programmanweisungen

### Schleifenknoten
- **Zählerschleife**: Zählergesteuerte Schleife - for/for-in-Schleifen
- **Kopfschleife**: Kopfgesteuerte Schleife - while-Schleifen
- **Fußschleife**: Fußgesteuerte Schleife - do-while-Schleifen

### Kontrollfluss-Knoten
- **Verzweigungsknoten**: Verzweigung - if/else-Bedingungsanweisungen
- **Fallunterscheidung**: Fallunterscheidung - switch/case-Anweisungen

### Erweiterte Knoten
- **Funktionsknoten**: Funktionsblock - Funktionsdefinitionen
- **Try-Catch-Knoten**: Try-Catch-Block - Ausnahmebehandlung

### Spezielle Elemente
- **Einfügeknoten**: Platzhalter für das Einfügen neuer Elemente
- **Fall einfügen**: Spezieller Fall für switch-Anweisungen
- **Platzhalter**: Leerer Platzhalter in der Struktur

## Farbcodierung

Jeder Knotentyp hat eine spezifische Farbe für visuelle Unterscheidung:
- **Eingabe-/Ausgabe-/Aufgabenknoten**: `rgb(253, 237, 206)` (Hellgelb)
- **Schleifenknoten**: `rgb(220, 239, 231)` (Hellgrün)
- **Verzweigungs-/Fall-/Try-Catch-Knoten**: `rgb(250, 218, 209)` (Hellorange)
- **Funktionsknoten**: `rgb(255, 255, 255)` (Weiß)
- **Einfüge-/Platzhalterknoten**: `rgb(255, 255, 243)` (Sehr hellgelb)

## Verwendungsempfehlungen

- **Anfänger**: Beginnen Sie mit `python_simple` für grundlegende Programmierkonzepte
- **Bedingungen**: Verwenden Sie `python_if` beim Unterrichten von if/else-Anweisungen
- **Schleifen**: Verwenden Sie `python_for` oder `python_while` für spezifische Schleifentypen, oder `python_loop` für beide
- **Fortgeschrittene**: Verwenden Sie `python_if_loop` für die Kombination von Bedingungen und Schleifen
- **Erweiterte Funktionen**: Verwenden Sie `python_function` oder `standard` für vollständige Programmierkonstrukte
- **Alle Funktionen**: Verwenden Sie `standard`, wenn alle Sprachfeatures benötigt werden
