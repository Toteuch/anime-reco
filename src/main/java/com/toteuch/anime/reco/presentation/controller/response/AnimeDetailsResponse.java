package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.anime.pojo.AnimeDetailsPojo;

public class AnimeDetailsResponse extends ExceptionResponse {
    private AnimeDetailsPojo animeDetails;

    public AnimeDetailsResponse(String errorMsg) {
        super(errorMsg);
    }

    public AnimeDetailsResponse(AnimeDetailsPojo animeDetails) {
        this.animeDetails = animeDetails;
    }

    public AnimeDetailsPojo getAnimeDetails() {
        return animeDetails;
    }

    public void setAnimeDetails(AnimeDetailsPojo animeDetails) {
        this.animeDetails = animeDetails;
    }
}
