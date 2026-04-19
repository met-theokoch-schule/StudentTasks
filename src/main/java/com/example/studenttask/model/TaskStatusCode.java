package com.example.studenttask.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TaskStatusCode {

    NICHT_BEGONNEN("NICHT_BEGONNEN"),
    IN_BEARBEITUNG("IN_BEARBEITUNG"),
    ABGEGEBEN("ABGEGEBEN"),
    UEBERARBEITUNG_NOETIG("ÜBERARBEITUNG_NÖTIG"),
    VOLLSTAENDIG("VOLLSTÄNDIG");

    private static final Map<String, TaskStatusCode> LOOKUP = Arrays.stream(values())
            .collect(Collectors.toMap(
                    code -> normalize(code.databaseName),
                    Function.identity()
            ));

    static {
        for (TaskStatusCode code : values()) {
            LOOKUP.put(normalize(code.name()), code);
        }
    }

    private final String databaseName;

    TaskStatusCode(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public static Optional<TaskStatusCode> fromName(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(LOOKUP.get(normalize(value)));
    }

    private static String normalize(String value) {
        return value
                .trim()
                .replace("Ä", "AE")
                .replace("Ö", "OE")
                .replace("Ü", "UE")
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .toUpperCase(Locale.ROOT);
    }
}
