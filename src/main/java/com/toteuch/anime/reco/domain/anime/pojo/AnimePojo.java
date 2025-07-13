package com.toteuch.anime.reco.domain.anime.pojo;

import java.util.List;

public class AnimePojo {
    Long id;
    String title;
    String mainMediumUrl;
    List<String> altTitles;
    String tag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainMediumUrl() {
        return mainMediumUrl;
    }

    public void setMainMediumUrl(String mainMediumUrl) {
        this.mainMediumUrl = mainMediumUrl;
    }

    public List<String> getAltTitles() {
        return altTitles;
    }

    public void setAltTitles(List<String> altTitles) {
        this.altTitles = altTitles;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
