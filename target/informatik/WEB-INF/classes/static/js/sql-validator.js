// SQL Validator - FINAL VERSION
// Mit vollständigen Feedback-Messages

const PRAISE_MESSAGES = [
    "🎉 Perfekt gelöst!",
    "⭐ Hervorragende Arbeit!",
    "✨ Glänzend gemacht!",
    "🚀 Du rockst das!",
    "👏 Absolut korrekt!",
    "💯 100% richtig!",
    "🏆 Champion-Lösung!",
    "🌟 Einfach großartig!",
    "🎯 Zielgenau gelöst!",
    "💪 Starke Leistung!"
];

function getRandomPraise() {
    return PRAISE_MESSAGES[Math.floor(Math.random() * PRAISE_MESSAGES.length)];
}

function validateSolution(taskId, studentCode, task, studentResults) {
    console.log(`🔍 Validating solution for task ${taskId}`);
    
    if (!task.solutionCode) {
        console.warn('No solution code provided for task:', taskId);
        return;
    }
    
    if (!task.validation) {
        console.warn('No validation config provided for task:', taskId);
        return;
    }
    
    try {
        const solutionResults = db.exec(task.solutionCode);
        console.log('📊 Student results:', studentResults);
        console.log('📊 Solution results:', solutionResults);
        
        const comparison = compareSQLResults(studentResults, solutionResults, task.validation);
        console.log('🔍 Comparison result:', comparison);
        
        markResultTable(comparison);
        
        const status = comparison.isCorrect ? 'correct' : 'incorrect';
        updateTaskStatus(taskId, status);
        console.log(`✅ Validation complete: ${status}`);
        
    } catch (e) {
        console.error('Error validating solution:', e);
        updateTaskStatus(taskId, 'incorrect');
    }
}

function compareSQLResults(studentResults, solutionResults, validation) {
    if (studentResults.length === 0 && solutionResults.length === 0) {
        return { 
            isCorrect: true, 
            rowComparisons: [], 
            errors: [],
            messages: [getRandomPraise()],
            studentColumns: [],
            solutionColumns: [],
            invalidColumnIndices: [],
            tooManyColumnsIndices: []
        };
    }
    
    if (studentResults.length !== solutionResults.length) {
        return { 
            isCorrect: false, 
            rowComparisons: [],
            errors: ['Unterschiedliche Anzahl Resultsets'],
            messages: [],
            studentColumns: [],
            solutionColumns: [],
            invalidColumnIndices: [],
            tooManyColumnsIndices: []
        };
    }
    
    if (studentResults.length === 0) {
        return { 
            isCorrect: false, 
            rowComparisons: [], 
            errors: ['Keine Ergebnisse'],
            messages: [],
            studentColumns: [],
            solutionColumns: [],
            invalidColumnIndices: [],
            tooManyColumnsIndices: []
        };
    }
    
    const studentData = studentResults[0];
    const solutionData = solutionResults[0];
    
    const result = {
        isCorrect: true,
        rowComparisons: [],
        errors: [],
        messages: [],
        studentColumns: studentData.columns,
        solutionColumns: solutionData.columns,
        invalidColumnIndices: [],
        tooManyColumnsIndices: []
    };
    
    validateColumns(result, studentData.columns, solutionData.columns, validation);
    validateRows(result, studentData.values, solutionData.values, validation);
    
    result.isCorrect = result.errors.length === 0 && result.rowComparisons.every(r => r.isCorrect !== false);
    
    if (result.isCorrect) {
        result.messages.push(getRandomPraise());
    }
    
    return result;
}

function validateColumns(result, studentColumns, solutionColumns, validation) {
    const solutionColumnSet = new Set(solutionColumns);
    
    // Überflüssige Spalten prüfen (extraColumnsAllowed = false)
    if (!validation.extraColumnsAllowed) {
        for (let i = 0; i < studentColumns.length; i++) {
            if (!solutionColumnSet.has(studentColumns[i])) {
                result.tooManyColumnsIndices.push(i);
            }
        }
        if (result.tooManyColumnsIndices.length > 0) {
            result.errors.push(`Zu viele Spalten: ${result.tooManyColumnsIndices.map(i => studentColumns[i]).join(', ')}`);
        }
    }
    
    // Fehlende Spalten prüfen - ist ein Fehler, aber nicht rot markiert
    const studentColumnSet = new Set(studentColumns);
    const missingColumns = [];
    for (let i = 0; i < solutionColumns.length; i++) {
        if (!studentColumnSet.has(solutionColumns[i])) {
            missingColumns.push(solutionColumns[i]);
        }
    }
    if (missingColumns.length > 0) {
        result.errors.push(`Fehlende Spalten: ${missingColumns.join(', ')}`);
    }
    
    // Spaltennamen prüfen (columnNamesMustMatch = true)
    // Nur für rot-Markierung (nicht für Fehlermeldungen, die sind bereits in "Zu viele Spalten" abgedeckt)
    if (validation.columnNamesMustMatch && !validation.extraColumnsAllowed) {
        for (let i = 0; i < studentColumns.length; i++) {
            if (!solutionColumnSet.has(studentColumns[i])) {
                result.invalidColumnIndices.push(i);
                // Keine redundante Fehlermeldung - wird schon in "Zu viele Spalten" abgedeckt
            }
        }
    }
}

function validateRows(result, studentValues, solutionValues, validation) {
    const sharedColumns = result.solutionColumns.filter((col) =>
        result.studentColumns.includes(col)
    );
    const studentRelevantIndices = sharedColumns.map((col) =>
        result.studentColumns.indexOf(col)
    );
    const solutionRelevantIndices = sharedColumns.map((col) =>
        result.solutionColumns.indexOf(col)
    );

    // Beide Seiten werden in dieselbe Spaltenreihenfolge gebracht,
    // damit vertauschte SELECT-Spalten keinen Fehlvergleich auslösen.
    const extractStudentRelevant = (studentRow) => {
        return studentRelevantIndices.map(idx => studentRow[idx]);
    };

    const extractSolutionRelevant = (solutionRow) => {
        return solutionRelevantIndices.map(idx => solutionRow[idx]);
    };
    
    let sortError = null;
    if (validation.orderMatters) {
        sortError = checkSortingOrder(studentValues, solutionValues, extractStudentRelevant, extractSolutionRelevant);
    }
    
    const serializeRow = (row) => JSON.stringify(row);
    const remainingSolutionCounts = new Map();

    for (const solutionRow of solutionValues) {
        const key = serializeRow(extractSolutionRelevant(solutionRow));
        remainingSolutionCounts.set(key, (remainingSolutionCounts.get(key) || 0) + 1);
    }

    // Zähler für zusammengefasste Fehlermeldungen
    let extraRowsCount = 0;
    let missingRowsCount = 0;
    
    // Jede Schüler-Zeile darf nur so oft matchen, wie sie in der Musterlösung vorkommt.
    for (let stuIdx = 0; stuIdx < studentValues.length; stuIdx++) {
        const stuRow = studentValues[stuIdx];
        const stuRelevant = extractStudentRelevant(stuRow);
        const rowKey = serializeRow(stuRelevant);
        const remainingMatches = remainingSolutionCounts.get(rowKey) || 0;

        if (remainingMatches > 0) {
            remainingSolutionCounts.set(rowKey, remainingMatches - 1);
            result.rowComparisons.push({
                rowIndex: stuIdx,
                isCorrect: true,
                status: 'correct',
                cellComparisons: stuRow.map(v => ({ status: 'correct', studentValue: v }))
            });
        } else {
            result.rowComparisons.push({
                rowIndex: stuIdx,
                isCorrect: false,
                status: 'extra',
                cellComparisons: stuRow.map(v => ({ status: 'incorrect', studentValue: v }))
            });
            extraRowsCount++;
        }
    }
    
    for (const remainingMatches of remainingSolutionCounts.values()) {
        if (remainingMatches > 0) {
            missingRowsCount += remainingMatches;
        }
    }
    
    // Zusammengefasste Fehlermeldungen
    if (extraRowsCount > 0) {
        result.errors.push(`${extraRowsCount} Zeile${extraRowsCount > 1 ? 'n' : ''} zu viel`);
    }
    if (missingRowsCount > 0) {
        result.errors.push(`${missingRowsCount} Zeile${missingRowsCount > 1 ? 'n' : ''} fehlt${missingRowsCount > 1 ? 'en' : ''}`);
    }
    
    if (sortError) {
        result.errors.push(sortError);
    }
}

function arraysEqual(a, b) {
    if (a.length !== b.length) return false;
    for (let i = 0; i < a.length; i++) {
        if (a[i] !== b[i]) return false;
    }
    return true;
}

function checkSortingOrder(studentValues, solutionValues, extractStudentRelevant, extractSolutionRelevant) {
    let lastSolIndex = -1;
    const outOfOrder = [];
    
    for (let stuIdx = 0; stuIdx < studentValues.length; stuIdx++) {
        const stuRelevant = extractStudentRelevant(studentValues[stuIdx]);
        
        for (let solIdx = 0; solIdx < solutionValues.length; solIdx++) {
            const solRelevant = extractSolutionRelevant(solutionValues[solIdx]);
            if (arraysEqual(stuRelevant, solRelevant)) {
                if (solIdx < lastSolIndex) {
                    outOfOrder.push(stuIdx);
                }
                lastSolIndex = Math.max(lastSolIndex, solIdx);
                break;
            }
        }
    }
    
    if (outOfOrder.length > 0) {
        return `Sortierung falsch: Zeilen ${outOfOrder.map(i => i + 1).join(', ')} außer Reihenfolge`;
    }
    return null;
}

function markResultTable(comparison) {
    const resultTables = document.getElementById('result-tables');
    if (!resultTables) return;
    
    const table = resultTables.querySelector('table');
    if (!table) return;
    
    const headers = table.querySelectorAll('thead th');
    
    if (comparison.tooManyColumnsIndices) {
        comparison.tooManyColumnsIndices.forEach(idx => {
            if (idx < headers.length) {
                headers[idx].style.color = '#ff6b6b';
                headers[idx].style.fontWeight = 'bold';
            }
        });
    }
    
    if (comparison.invalidColumnIndices) {
        comparison.invalidColumnIndices.forEach(idx => {
            if (idx < headers.length) {
                headers[idx].style.color = '#ff6b6b';
                headers[idx].style.fontWeight = 'bold';
            }
        });
    }
    
    const tbody = table.querySelector('tbody');
    if (!tbody) return;
    
    const rows = tbody.querySelectorAll('tr');
    comparison.rowComparisons.forEach((rowComp, idx) => {
        if (idx >= rows.length) return;
        
        const row = rows[idx];
        
        if (rowComp.status === 'correct') {
            row.style.backgroundColor = '#1e4620';
        } else if (rowComp.status === 'extra') {
            row.style.backgroundColor = '#3d1a1a';
        } else if (rowComp.status === 'incorrect') {
            row.style.backgroundColor = '#3d1a1a';
        }
    });
    
    // Feedback-Nachrichten
    let feedbackHtml = '';
    
    // Success Messages
    if (comparison.messages && comparison.messages.length > 0) {
        feedbackHtml += '<div style="color: #4CAF50; margin-top: 10px; font-size: 14px; font-weight: bold;">';
        feedbackHtml += comparison.messages.join('<br>');
        feedbackHtml += '</div>';
    }
    
    // Error Messages
    if (comparison.errors && comparison.errors.length > 0) {
        feedbackHtml += '<div style="color: #ff6b6b; margin-top: 10px; font-size: 12px;">';
        feedbackHtml += '<strong>Was kann noch verbessert werden:</strong><br>';
        comparison.errors.forEach(err => {
            feedbackHtml += `• ${err}<br>`;
        });
        feedbackHtml += '</div>';
    }
    
    if (feedbackHtml) {
        let feedbackDiv = resultTables.parentNode.querySelector('.validation-feedback');
        if (!feedbackDiv) {
            feedbackDiv = document.createElement('div');
            feedbackDiv.className = 'validation-feedback';
            resultTables.parentNode.appendChild(feedbackDiv);
        }
        feedbackDiv.innerHTML = feedbackHtml;
    }
}

function updateTaskStatus(taskId, status) {
    const statusEl = document.querySelector(`[data-task-id="${taskId}"] .task-status`);
    if (statusEl) {
        statusEl.textContent = status === 'correct' ? '✓' : '✗';
        statusEl.style.color = status === 'correct' ? '#4CAF50' : '#f44336';
    }
}
