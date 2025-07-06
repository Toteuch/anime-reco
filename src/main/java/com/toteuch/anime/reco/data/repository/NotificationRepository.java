package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Notification;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByProfileSubAndReadAtNull(String sub, Sort sort);

    @Query("SELECT notif FROM Notification notif " +
            "INNER JOIN Profile profile ON notif.profile = profile " +
            "WHERE profile.sub = :sub " +
            "ORDER BY notif.readAt DESC NULLS FIRST, notif.createdAt DESC")
    Page<Notification> findByProfileSub(String sub, Pageable pageable);

    Notification findByProfileAndId(Profile profile, Long id);
}
