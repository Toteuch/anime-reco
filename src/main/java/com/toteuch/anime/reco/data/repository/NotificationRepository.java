package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Notification;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByProfileSubAndReadAtNull(String sub, Sort sort);

    List<Notification> findByProfileSub(String sub, Sort sort);

    Notification findByProfileAndId(Profile profile, Long id);
}
