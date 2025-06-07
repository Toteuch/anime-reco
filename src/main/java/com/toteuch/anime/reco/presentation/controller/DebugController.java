package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.presentation.controller.response.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {
    @Autowired
    private AnimeService animeService;

    @PostMapping("/debug/refresh-old-anime")
    public ExceptionResponse refreshAnime() {
        animeService.refreshOldAnimeDetails();
        return new ExceptionResponse();
    }
}
