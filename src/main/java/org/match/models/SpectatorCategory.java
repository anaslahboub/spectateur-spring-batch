package org.match.models;

import java.util.Arrays;

public enum SpectatorCategory {
    PREMIERE_VISITE("Première visite", 1, 1),
    SPECTATEUR_OCCASIONNEL("Spectateur occasionnel", 2, 3),
    SPECTATEUR_REGULIER("Spectateur régulier", 4, 6),
    SUPER_FAN("Super fan", 7, Integer.MAX_VALUE);

    private final String label;
    private final int minMatches;
    private final int maxMatches;

    SpectatorCategory(String label, int minMatches, int maxMatches) {
        this.label = label;
        this.minMatches = minMatches;
        this.maxMatches = maxMatches;
    }

    public String getLabel() { return label; }

    public static SpectatorCategory fromMatchCount(long count) {
        return Arrays.stream(values())
                .filter(cat -> count >= cat.minMatches && count <= cat.maxMatches)
                .findFirst()
                .orElse(SUPER_FAN);
    }
}