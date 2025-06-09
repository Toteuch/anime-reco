package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchFilterRepository extends JpaRepository<SearchFilter, Long> {
    SearchFilter findByProfileAndName(Profile profile, String name);
}
