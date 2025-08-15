
#!/usr/bin/env python3
"""
Skript zur Erstellung eines PDF-Dokuments aus den Anleitungsdateien
"""

import markdown
import pdfkit
import os
from pathlib import Path

def read_markdown_file(filepath):
    """Liest eine Markdown-Datei und gibt den Inhalt zurück"""
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            return file.read()
    except FileNotFoundError:
        print(f"Warnung: Datei {filepath} nicht gefunden")
        return ""
    except Exception as e:
        print(f"Fehler beim Lesen von {filepath}: {e}")
        return ""

def markdown_to_html(markdown_content, title=""):
    """Konvertiert Markdown zu HTML mit Styling"""
    md = markdown.Markdown(extensions=['tables', 'fenced_code', 'toc'])
    html_content = md.convert(markdown_content)
    
    # HTML Template mit CSS Styling
    html_template = f"""
    <!DOCTYPE html>
    <html lang="de">
    <head>
        <meta charset="utf-8">
        <title>{title}</title>
        <style>
            body {{
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                color: #333;
                max-width: 800px;
                margin: 0 auto;
                padding: 20px;
            }}
            h1 {{
                color: #2c3e50;
                border-bottom: 3px solid #3498db;
                padding-bottom: 10px;
                margin-top: 30px;
            }}
            h2 {{
                color: #34495e;
                border-bottom: 2px solid #95a5a6;
                padding-bottom: 5px;
                margin-top: 25px;
            }}
            h3 {{
                color: #2c3e50;
                margin-top: 20px;
            }}
            h4 {{
                color: #34495e;
                margin-top: 15px;
            }}
            code {{
                background-color: #f8f9fa;
                padding: 2px 4px;
                border-radius: 3px;
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                color: #e74c3c;
            }}
            pre {{
                background-color: #2c3e50;
                color: #ecf0f1;
                padding: 15px;
                border-radius: 5px;
                overflow-x: auto;
                margin: 15px 0;
            }}
            pre code {{
                background: none;
                color: #ecf0f1;
                padding: 0;
            }}
            table {{
                border-collapse: collapse;
                width: 100%;
                margin: 15px 0;
            }}
            th, td {{
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }}
            th {{
                background-color: #f2f2f2;
                font-weight: bold;
            }}
            tr:nth-child(even) {{
                background-color: #f9f9f9;
            }}
            ul, ol {{
                margin-bottom: 15px;
                padding-left: 30px;
            }}
            li {{
                margin-bottom: 5px;
            }}
            blockquote {{
                border-left: 4px solid #3498db;
                margin-left: 0;
                padding-left: 20px;
                font-style: italic;
                color: #555;
            }}
            .page-break {{
                page-break-before: always;
            }}
            .document-title {{
                text-align: center;
                font-size: 2.5em;
                color: #2c3e50;
                margin-bottom: 30px;
                border-bottom: none;
            }}
            .section-title {{
                page-break-before: always;
                margin-top: 0;
            }}
        </style>
    </head>
    <body>
        {html_content}
    </body>
    </html>
    """
    return html_template

def replace_unicode_symbols(text):
    """Ersetzt Unicode-Symbole durch HTML-kompatible Alternativen"""
    # Ersetze Unicode-Checkmarks und X-Marks
    text = text.replace('✅', '<span style="color: green; font-weight: bold;">[✓]</span>')
    text = text.replace('❌', '<span style="color: red; font-weight: bold;">[✗]</span>')
    text = text.replace('⚠️', '<span style="color: orange; font-weight: bold;">[!]</span>')
    return text

def create_combined_pdf():
    """Erstellt ein kombiniertes PDF aus allen drei Anleitungen"""
    
    # Dateipfade definieren
    files = [
        ("struktog.config.md", "Struktog Konfigurationsübersicht"),
        ("hamsterAnleitung.md", "Hamstersimulator Anleitung"),
        ("hidden-functions.md", "Debug Content Viewer")
    ]
    
    # Gesamten HTML-Inhalt sammeln
    combined_html = """
    <!DOCTYPE html>
    <html lang="de">
    <head>
        <meta charset="utf-8">
        <title>Programmier-Anleitungen</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                color: #333;
                max-width: 800px;
                margin: 0 auto;
                padding: 20px;
            }
            h1 {
                color: #2c3e50;
                border-bottom: 3px solid #3498db;
                padding-bottom: 10px;
                margin-top: 30px;
            }
            h2 {
                color: #34495e;
                border-bottom: 2px solid #95a5a6;
                padding-bottom: 5px;
                margin-top: 25px;
            }
            h3 {
                color: #2c3e50;
                margin-top: 20px;
            }
            h4 {
                color: #34495e;
                margin-top: 15px;
            }
            code {
                background-color: #f8f9fa;
                padding: 2px 4px;
                border-radius: 3px;
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                color: #e74c3c;
            }
            pre {
                background-color: #2c3e50;
                color: #ecf0f1;
                padding: 15px;
                border-radius: 5px;
                overflow-x: auto;
                margin: 15px 0;
            }
            pre code {
                background: none;
                color: #ecf0f1;
                padding: 0;
            }
            table {
                border-collapse: collapse;
                width: 100%;
                margin: 15px 0;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            th {
                background-color: #f2f2f2;
                font-weight: bold;
            }
            tr:nth-child(even) {
                background-color: #f9f9f9;
            }
            ul, ol {
                margin-bottom: 15px;
                padding-left: 30px;
            }
            li {
                margin-bottom: 5px;
            }
            blockquote {
                border-left: 4px solid #3498db;
                margin-left: 0;
                padding-left: 20px;
                font-style: italic;
                color: #555;
            }
            .page-break {
                page-break-before: always;
            }
            .document-title {
                text-align: center;
                font-size: 2.5em;
                color: #2c3e50;
                margin-bottom: 30px;
                border-bottom: none;
            }
            .section-title {
                page-break-before: always;
                margin-top: 0;
            }
        </style>
    </head>
    <body>
        <h1 class="document-title">Programmier-Anleitungen</h1>
        <div style="text-align: center; margin-bottom: 40px; color: #666;">
            <p>Vollständige Dokumentation für:</p>
            <ul style="list-style: none; padding: 0;">
                <li>• Struktog Konfiguration</li>
                <li>• Hamstersimulator</li>
                <li>• Debug-Funktionen</li>
            </ul>
        </div>
    """
    
    md = markdown.Markdown(extensions=['tables', 'fenced_code', 'toc'])
    
    # Jede Datei einzeln verarbeiten
    for i, (filepath, title) in enumerate(files):
        print(f"Verarbeite {filepath}...")
        
        content = read_markdown_file(filepath)
        if content:
            # Unicode-Symbole ersetzen
            content = replace_unicode_symbols(content)
            
            # Seitenumbruch vor jeder neuen Sektion (außer der ersten)
            if i > 0:
                combined_html += '<div class="page-break"></div>'
            
            combined_html += f'<h1 class="section-title">{title}</h1>'
            combined_html += md.convert(content)
        else:
            combined_html += f'<h1 class="section-title">{title}</h1>'
            combined_html += f'<p><em>Datei {filepath} konnte nicht gelesen werden.</em></p>'
    
    combined_html += "</body></html>"
    
    # PDF-Optionen konfigurieren
    options = {
        'page-size': 'A4',
        'margin-top': '0.75in',
        'margin-right': '0.75in',
        'margin-bottom': '0.75in',
        'margin-left': '0.75in',
        'encoding': "UTF-8",
        'no-outline': None,
        'enable-local-file-access': None,
        'disable-smart-shrinking': '',
        'print-media-type': '',
        'dpi': 300
    }
    
    output_filename = "Programmier_Anleitungen.pdf"
    
    try:
        # PDF erstellen
        print("Erstelle PDF...")
        pdfkit.from_string(combined_html, output_filename, options=options)
        print(f"✓ PDF erfolgreich erstellt: {output_filename}")
        
        # Dateigröße anzeigen
        file_size = os.path.getsize(output_filename)
        print(f"  Dateigröße: {file_size / 1024:.1f} KB")
        
    except Exception as e:
        print(f"✗ Fehler beim Erstellen des PDFs: {e}")
        print("Hinweis: Stellen Sie sicher, dass wkhtmltopdf installiert ist.")

def main():
    """Hauptfunktion"""
    print("=" * 50)
    print("PDF-Generator für Programmier-Anleitungen")
    print("=" * 50)
    
    # Prüfen ob alle Dateien existieren
    required_files = ["struktog.config.md", "hamsterAnleitung.md", "hidden-functions.md"]
    missing_files = []
    
    for file in required_files:
        if not Path(file).exists():
            missing_files.append(file)
    
    if missing_files:
        print("Warnung: Folgende Dateien wurden nicht gefunden:")
        for file in missing_files:
            print(f"  - {file}")
        print()
    
    create_combined_pdf()
    print("=" * 50)

if __name__ == "__main__":
    main()
