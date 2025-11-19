const CodeAnalyzer = (function() {

    function parseDirectives(code) {
        const lines = code.split('\n');
        const metadata = {
            hasDisplayable: false,
            classes: []
        };

        let currentClass = null;
        let currentClassInfo = null;
        let inClass = false;
        let classIndent = 0;

        for (let i = 0; i < lines.length; i++) {
            const line = lines[i];
            const trimmed = line.trim();

            if (trimmed.startsWith('#@displayable')) {
                const nextLineIndex = i + 1;
                if (nextLineIndex < lines.length) {
                    let imageUrl = null;
                    let lookAhead = nextLineIndex;
                    
                    while (lookAhead < lines.length && lines[lookAhead].trim().startsWith('#@')) {
                        const directiveLine = lines[lookAhead].trim();
                        if (directiveLine.startsWith('#@image:')) {
                            imageUrl = directiveLine.substring(8).trim();
                        }
                        lookAhead++;
                    }

                    const classMatch = lines[lookAhead]?.trim().match(/^class\s+(\w+)/);
                    if (classMatch) {
                        currentClass = classMatch[1];
                        currentClassInfo = {
                            name: currentClass,
                            imageUrl: imageUrl,
                            attributes: []
                        };
                        metadata.classes.push(currentClassInfo);
                        metadata.hasDisplayable = true;
                        inClass = true;
                        classIndent = lines[lookAhead].search(/\S/);
                        i = lookAhead;
                    }
                }
            } else if (inClass && trimmed.startsWith('class ') && !trimmed.startsWith('class ' + currentClass)) {
                inClass = false;
                currentClass = null;
                currentClassInfo = null;
            } else if (inClass && currentClassInfo) {
                const lineIndent = line.search(/\S/);
                
                if (trimmed && lineIndent <= classIndent && !trimmed.startsWith('#')) {
                    inClass = false;
                    currentClass = null;
                    currentClassInfo = null;
                    continue;
                }

                if (trimmed.startsWith('#@show')) {
                    const showMatch = trimmed.match(/#@show\(\s*name\s*=\s*"([^"]+)"(?:\s*,\s*label\s*=\s*"([^"]+)")?\s*\)/);
                    if (showMatch) {
                        const attrName = showMatch[1];
                        const label = showMatch[2] || attrName;
                        currentClassInfo.attributes.push({
                            name: attrName,
                            label: label
                        });
                    } else {
                        const oldMatch = trimmed.match(/#@show\(label="([^"]+)"\)/);
                        if (oldMatch) {
                            const label = oldMatch[1];
                            let attrLookAhead = i + 1;
                            while (attrLookAhead < lines.length && lines[attrLookAhead].trim().startsWith('#@')) {
                                attrLookAhead++;
                            }
                            const nextLine = lines[attrLookAhead];
                            if (nextLine) {
                                const attrMatch = nextLine.trim().match(/self\.(\w+)\s*=/);
                                if (attrMatch) {
                                    const attrName = attrMatch[1];
                                    currentClassInfo.attributes.push({
                                        name: attrName,
                                        label: label
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }

        return metadata;
    }

    function extractClassMetadata(code) {
        const metadata = parseDirectives(code);
        
        if (!metadata.hasDisplayable) {
            return null;
        }

        return metadata.classes;
    }

    return {
        parseDirectives,
        extractClassMetadata
    };
})();
