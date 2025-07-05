package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchFilterRepository extends JpaRepository<SearchFilter, Long> {
    SearchFilter findByProfileAndName(Profile profile, String name);

    SearchFilter findByProfileOrderByFilterIndexDesc(Profile profile, Limit limit);

    List<SearchFilter> findByProfileOrderByFilterIndex(Profile profile);

    SearchFilter findByProfileAndId(Profile profile, Long id);
}
