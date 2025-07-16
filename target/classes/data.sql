
-- Insert default roles
INSERT OR IGNORE INTO roles (id, name, description) VALUES 
(1, 'STUDENT', 'Schüler-Rolle für Aufgabenbearbeitung'),
(2, 'TEACHER', 'Lehrer-Rolle für Aufgabenverwaltung'),
(3, 'ADMIN', 'Administrator-Rolle für Systemverwaltung');

-- Insert default groups
INSERT OR IGNORE INTO groups (id, name, description) VALUES 
(1, 'Klasse 10A', 'Mathematik Klasse 10A'),
(2, 'Klasse 10B', 'Mathematik Klasse 10B'),
(3, 'Informatik AG', 'Informatik Arbeitsgemeinschaft');
