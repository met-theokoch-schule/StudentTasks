
-- Insert default roles (H2-kompatible Syntax)
MERGE INTO roles (id, name, description) KEY(id) VALUES 
(1, 'STUDENT', 'Schüler-Rolle für Aufgabenbearbeitung'),
(2, 'TEACHER', 'Lehrer-Rolle für Aufgabenverwaltung'),
(3, 'ADMIN', 'Administrator-Rolle für Systemverwaltung');

-- Insert default groups (H2-kompatible Syntax)
MERGE INTO groups (id, name, description) KEY(id) VALUES 
(1, 'Klasse 10A', 'Mathematik Klasse 10A'),
(2, 'Klasse 10B', 'Mathematik Klasse 10B'),
(3, 'Informatik AG', 'Informatik Arbeitsgemeinschaft');
