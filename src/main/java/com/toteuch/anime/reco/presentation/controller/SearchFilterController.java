package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.MediaType;
import com.toteuch.anime.reco.domain.anime.Status;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.SearchFilterService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;
import com.toteuch.anime.reco.presentation.controller.response.ListSearchFilterResponse;
import com.toteuch.anime.reco.presentation.controller.response.SearchFilterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SearchFilterController {

    @Autowired
    private SearchFilterService searchFilterService;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private ProfileService profileService;

    @PostMapping("/search-filter")
    public SearchFilterResponse saveFilter(@RequestBody SearchFilterPojo body) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            try {
                return new SearchFilterResponse(getSearchFilterPojo(searchFilterService.saveSearchFilter(profile,
                        body)));
            } catch (AnimeRecoException ex) {
                return new SearchFilterResponse(ex.getMessage());
            }
        } else {
            return new SearchFilterResponse("You must be logged to save a filter.");
        }
    }

    @PutMapping("/search-filter/{id}/up")
    public ListSearchFilterResponse moveUp(@PathVariable Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            return new ListSearchFilterResponse(getListSearchFilterPojo(searchFilterService.moveUp(profile, id)));
        } else {
            return new ListSearchFilterResponse("You must be logged in.");
        }
    }

    @PutMapping("/search-filter/{id}/down")
    public ListSearchFilterResponse moveDown(@PathVariable Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            return new ListSearchFilterResponse(getListSearchFilterPojo(searchFilterService.moveDown(profile, id)));
        } else {
            return new ListSearchFilterResponse("You must be logged in.");
        }
    }

    @GetMapping("/search-filter")
    public ListSearchFilterResponse getSearchFilterList() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            List<SearchFilter> searchFilterList = searchFilterService.getAllSearchFilter(profile);
            return new ListSearchFilterResponse(getListSearchFilterPojo(searchFilterList));
        } else {
            return new ListSearchFilterResponse("You must be logged in.");
        }
    }

    @GetMapping("/search-filter/{id}")
    public SearchFilterResponse getSearchFilter(@PathVariable Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            SearchFilter searchFilter = searchFilterService.getSearchFilter(profile, id);
            if (searchFilter != null) {
                return new SearchFilterResponse(getSearchFilterPojo(searchFilter));
            } else {
                return new SearchFilterResponse("Internal error.");
            }
        } else {
            return new SearchFilterResponse("You must be logged in.");
        }
    }

    @GetMapping("/search-filter/media-type")
    public Map<String, String> getMediaTypes() {
        return MediaType.getMediaTypesMap();
    }

    @GetMapping("/search-filter/status")
    public Map<String, String> getStatus() {
        return Status.getStatusMap();
    }

    @GetMapping("/search-filter/genre")
    public Map<Long, String> getGenres() {
        return animeService.getGenresMap();
    }

    private List<SearchFilterPojo> getListSearchFilterPojo(List<SearchFilter> searchFilters) {
        List<SearchFilterPojo> pojos = new ArrayList<>();
        if (searchFilters != null) searchFilters.forEach(sf -> pojos.add(getSearchFilterPojo(sf)));
        return pojos;
    }

    private SearchFilterPojo getSearchFilterPojo(SearchFilter searchFilter) {
        SearchFilterPojo pojo = new SearchFilterPojo();
        pojo.setId(searchFilter.getId());
        pojo.setName(searchFilter.getName());
        pojo.setTitle(searchFilter.getTitle());
        if (searchFilter.getMediaTypes() != null)
            pojo.setMediaTypes(MediaType.getMalCodes(searchFilter.getMediaTypes()));
        if (searchFilter.getStatusList() != null)
            pojo.setStatusList(Status.getMalCodes(searchFilter.getStatusList()));
        pojo.setMinSeasonYear(searchFilter.getMinSeasonYear());
        pojo.setMaxSeasonYear(searchFilter.getMaxSeasonYear());
        if (searchFilter.getGenres() != null)
            pojo.setGenres(Genre.getGenresIds(searchFilter.getGenres()));
        if (searchFilter.getNegativeGenres() != null)
            pojo.setNegativeGenres(Genre.getGenresIds(searchFilter.getNegativeGenres()));
        return pojo;
    }
}
