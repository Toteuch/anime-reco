package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.MediaType;
import com.toteuch.anime.reco.domain.anime.Status;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.profile.SearchFilterService;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;
import com.toteuch.anime.reco.presentation.controller.response.SearchFilterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchFilterController {

    @Autowired
    private SearchFilterService searchFilterService;
    @Autowired
    private AnimeService animeService;

    @PostMapping("/search-filter")
    public SearchFilterResponse create(@RequestBody SearchFilterPojo body) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            try {
                SearchFilter searchFilter = searchFilterService.createFilter(
                        oidcUser.getSubject(),
                        body.getName(),
                        MediaType.parseMediaTypes(body.getMediaTypes()),
                        Status.parseStatusList(body.getStatusList()),
                        body.getMinSeasonYear(),
                        body.getMaxSeasonYear(),
                        body.getGenres(),
                        body.getNegativeGenres());
                return new SearchFilterResponse(getSearchFilterPojo(searchFilter));
            } catch (AnimeRecoException e) {
                return new SearchFilterResponse(e.getMessage());
            }
        } else {
            return new SearchFilterResponse("User not authenticated");
        }
    }

    private SearchFilterPojo getSearchFilterPojo(SearchFilter searchFilter) {
        SearchFilterPojo pojo = new SearchFilterPojo();
        pojo.setId(searchFilter.getId());
        pojo.setName(searchFilter.getName());
        pojo.setMediaTypes(MediaType.getMalCode(searchFilter.getMediaTypes()));
        pojo.setStatusList(Status.getMalCodes(searchFilter.getStatusList()));
        pojo.setMinSeasonYear(searchFilter.getMinSeasonYear());
        pojo.setMaxSeasonYear(searchFilter.getMaxSeasonYear());
        pojo.setGenres(Genre.getGenresIds(searchFilter.getGenres()));
        pojo.setNegativeGenres(Genre.getGenresIds(searchFilter.getNegativeGenres()));
        return pojo;
    }
}
