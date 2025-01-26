package com.toteuch.anime.reco.domain.profile;

public enum NotificationType {
    NEW_FAVORITE("A new #ANIME has been favorited");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
