// Überprüfe Rollen und leite entsprechend weiter
        boolean isTeacher = false;
        boolean isStudent = false;

        for (Role role : user.getRoles()) {
            if ("TEACHER".equals(role.getName())) {
                isTeacher = true;
            }
            if ("STUDENT".equals(role.getName())) {
                isStudent = true;
            }
        }

        System.out.println("🎭 Role evaluation:");
        System.out.println("   - Is Teacher: " + isTeacher);
        System.out.println("   - Is Student: " + isStudent);
        System.out.println("   - Available roles: " + user.getRoles().toArray());

        if (isTeacher) {
            return "redirect:/teacher/dashboard";
        } else if (isStudent) {
            return "redirect:/student/dashboard";
        } else {
            // Für alle anderen zeige das allgemeine Dashboard
            return "dashboard";
        }