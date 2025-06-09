package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.SearchFilterRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.MediaType;
import com.toteuch.anime.reco.domain.anime.Status;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFilterService {
    private static final Logger log = LoggerFactory.getLogger(SearchFilterService.class);

    @Autowired
    private SearchFilterRepository repo;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AnimeService animeService;

    public SearchFilter createFilter(String sub, String name, List<MediaType> mediaTypes, List<Status> statusList,
                                     Integer minSeasonYear, Integer maxSeasonYear, List<Long> genreIds,
                                     List<Long> negativeGenreIds) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (profile == null) throw new AnimeRecoException("Profile not found");
        SearchFilter searchFilter = repo.findByProfileAndName(profile, name);
        if (searchFilter != null) throw new AnimeRecoException("Name already used");
        searchFilter = new SearchFilter();
        searchFilter.setProfile(profile);
        searchFilter.setName(name);
        searchFilter.setMediaTypes(mediaTypes);
        searchFilter.setStatusList(statusList);
        List<Genre> genres = new ArrayList<>();
        if (genreIds != null) genreIds.forEach(g -> genres.add(animeService.getGenreById(g)));
        searchFilter.setGenres(genres);
        List<Genre> negativeGenres = new ArrayList<>();
        if (negativeGenreIds != null) negativeGenreIds.forEach((g -> negativeGenres.add(animeService.getGenreById(g))));
        searchFilter.setNegativeGenres(negativeGenres);
        searchFilter.setMinSeasonYear(minSeasonYear == null || minSeasonYear == 0 ? null : minSeasonYear);
        searchFilter.setMaxSeasonYear(maxSeasonYear == null || maxSeasonYear == 0 ? null : maxSeasonYear);
        searchFilter = repo.save(searchFilter);
        log.debug("SearchFilter {} created for user {}", name, profile.getUser().getUsername());
        return searchFilter;
    }
}
