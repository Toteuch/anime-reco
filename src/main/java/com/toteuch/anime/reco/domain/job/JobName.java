package com.toteuch.anime.reco.domain.job;

public enum JobName {
    PROCESS_USER_SIMILARITY("Compute user similarities"),
    PROCESS_ANIME_RECOMMENDATION("Compute recommendations"),
    CLEAR_OLD_DATA("Refresh computed data");

    private final String label;

    JobName(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
