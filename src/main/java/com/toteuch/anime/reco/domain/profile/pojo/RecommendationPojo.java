package com.toteuch.anime.reco.domain.profile.pojo;

import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;

import java.util.List;

public class RecommendationPojo {
    List<AnimePojo> animeList;
    String recoLabel;
    Long filterId;

    public RecommendationPojo() {
    }

    public List<AnimePojo> getAnimeList() {
        return animeList;
    }

    public void setAnimeList(List<AnimePojo> animeList) {
        this.animeList = animeList;
    }

    public String getRecoLabel() {
        return recoLabel;
    }

    public void setRecoLabel(String recoLabel) {
        this.recoLabel = recoLabel;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }
}
