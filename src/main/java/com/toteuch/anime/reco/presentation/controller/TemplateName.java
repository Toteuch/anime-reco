package com.toteuch.anime.reco.presentation.controller;

public enum TemplateName {
    HOME("home"),
    PROFILE("profile"),
    NOTIFICATIONS("notifications"),
    SEARCH("search");

    private final String code;

    TemplateName(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
