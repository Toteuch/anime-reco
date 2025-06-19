package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.anime.AlternativeTitleType;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.AlternativeTitle;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;
import com.toteuch.anime.reco.domain.profile.SearchFilterService;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;
import com.toteuch.anime.reco.presentation.controller.response.AnimeListResultResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AnimeController {

    private static final Logger log = LoggerFactory.getLogger(AnimeController.class);

    @Autowired
    private SearchFilterService searchFilterService;
    @Autowired
    private AnimeService animeService;

    @PostMapping("/anime/search")
    public AnimeListResultResponse search(@RequestBody SearchFilterPojo searchFilterPojo) {
        try {
            SearchFilter searchFilter = searchFilterService.validate(searchFilterPojo);
            Page<Anime> animeList = animeService.search(searchFilter, searchFilterPojo.getPageNumber());
            return new AnimeListResultResponse(getAnimeListPojo(animeList), animeList.getNumber(),
                    animeList.getTotalPages());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new AnimeListResultResponse(ex.getMessage());
        }
    }

    private List<AnimePojo> getAnimeListPojo(Page<Anime> animeList) {
        List<AnimePojo> animeListPojo = new ArrayList<>();
        if (animeList != null && !animeList.isEmpty()) {
            for (Anime anime : animeList.stream().toList()) {
                AnimePojo pojo = new AnimePojo();
                pojo.setId(anime.getId());
                pojo.setMainMediumUrl(anime.getMainPictureMediumUrl());
                List<String> altTitles = new ArrayList<>();
                for (AlternativeTitle altTitle : anime.getAlternativeTitles()) {
                    if (altTitle.getType() == AlternativeTitleType.EN && !StringUtils.isAllBlank(altTitle.getText())) {
                        altTitles.add(anime.getTitle());
                        pojo.setTitle(altTitle.getText());
                    } else if (!StringUtils.isAllBlank(altTitle.getText())) {
                        altTitles.add(altTitle.getText());
                    }
                }
                if (pojo.getTitle() == null) {
                    pojo.setTitle(anime.getTitle());
                }
                pojo.setAltTitles(altTitles);
                animeListPojo.add(pojo);
            }
        }
        return animeListPojo;
    }
}
