// SQL Relationenmodell Parser & Renderer
// Parst TABLE-Markup und rendert interaktive Tabellen-Diagramme

let lastActiveEditor = null;

// Parser: TABLE-Markup in strukturierte Daten umwandeln
function parseRelmodellMarkup(markup) {
    const lines = markup.split('\n').filter(line => {
        const trimmed = line.trim();
        return trimmed.length > 0 && !trimmed.startsWith('#');
    });

    const tables = [];

    for (const line of lines) {
        const match = line.match(/TABLE\s+(\w+)\s*\(([^)]+)\)/);
        if (!match) continue;

        const tableName = match[1];
        const attributesStr = match[2];
        
        const attributes = attributesStr.split(',').map(attr => {
            const trimmed = attr.trim();
            let name = trimmed;
            let isPK = false;
            let isFK = false;

            // Prüfe auf Primärschlüssel-Präfix (_)
            if (name.startsWith('_')) {
                isPK = true;
                name = name.substring(1);
            }

            // Prüfe auf Fremdschlüssel-Suffix (^)
            if (name.endsWith('^')) {
                isFK = true;
                name = name.substring(0, name.length - 1);
            }

            return { name, isPK, isFK };
        });

        tables.push({ name: tableName, attributes });
    }

    return { tables };
}

// Renderer: Strukturierte Daten in HTML String umwandeln
function renderRelmodell(parsedData) {
    if (!parsedData || !parsedData.tables || parsedData.tables.length === 0) {
        return '<div style="color: #999; padding: 10px;">Keine Tabellen im Relationenmodell definiert.</div>';
    }

    let html = '<div class="relmodell-list" style="font-family: Consolas, monospace; line-height: 1.8;">';

    for (let i = 0; i < parsedData.tables.length; i++) {
        const table = parsedData.tables[i];
        const isLast = i === parsedData.tables.length - 1;
        const marginStyle = isLast ? '' : ' style="margin-bottom: 12px;"';
        html += `<div class="relmodell-entry"${marginStyle}>`;
        html += `<span class="clickable relmodell-table-name" data-insert="${table.name}" title="Klick zum Einfügen in den Editor" style="font-weight: bold; color: #6dbfff;">${table.name}</span>`;
        html += `(`;
        
        const attrStrings = [];
        for (const attr of table.attributes) {
            let attrHTML = `<span class="clickable relmodell-attr-name" data-insert="${attr.name}" data-table="${table.name}" title="Klick zum Einfügen in den Editor">`;
            
            if (attr.isPK) {
                attrHTML += `<u>${attr.name}</u>`;
            } else {
                attrHTML += attr.name;
            }
            
            if (attr.isFK) {
                attrHTML += `<i class="fas fa-arrow-up" style="margin-left: 2px; font-size: 0.75em; color: #6dbfff;"></i>`;
            }
            
            attrHTML += `</span>`;
            attrStrings.push(attrHTML);
        }
        
        html += attrStrings.join(', ');
        html += `)`;
        html += `</div>`;
    }

    html += '</div>';
    return html;
}

// Click-Handler Setup für dynamische Elemente
function setupRelmodellClickHandlers() {
    // Suche nach allen clickable Elementen im erdOutput
    const erdOutput = document.getElementById('erdOutput');
    if (!erdOutput) return;

    const clickables = erdOutput.querySelectorAll('.clickable');
    clickables.forEach(elem => {
        elem.addEventListener('click', (e) => {
            e.stopPropagation();
            if (!lastActiveEditor) {
                console.warn('⚠️ Kein aktiver Editor - bitte zuerst in einen SQL-Editor klicken');
                return;
            }

            const insertValue = elem.getAttribute('data-insert');
            console.log(`📌 Inserting "${insertValue}" into editor`);
            
            const doc = lastActiveEditor.getDoc();
            const pos = doc.getCursor();
            doc.replaceRange(insertValue, pos);
            doc.setCursor({ line: pos.line, ch: pos.ch + insertValue.length });
            lastActiveEditor.focus();
        });
    });
}

// Hilfsfunktion: Erkennung ob Content Relationenmodell ist
function isRelmodellMarkup(content) {
    return typeof content === 'string' && content.trim().toUpperCase().startsWith('TABLE');
}

console.log("📄 sql-relmodell.js loaded");
