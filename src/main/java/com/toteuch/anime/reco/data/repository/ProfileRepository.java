package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findBySub(String sub);

    Profile findByEmail(String email);

    Profile findByUserUsername(String username);
}
