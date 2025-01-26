package com.toteuch.anime.reco.domain.profile;

public enum NotificationType {
    NEW_FAVORITE("A new #ANIME has been favorited"),
    FAVORITE_ANIME_STATUS_CHANGED("The status of #ANIME has changed");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
