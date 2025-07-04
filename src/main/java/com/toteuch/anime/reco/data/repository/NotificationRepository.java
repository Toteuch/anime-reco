package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Notification;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByProfileSubAndReadAtNull(String sub, Sort sort);

    @Query(value = "SELECT n.* FROM notification n JOIN profile p ON n.profile_id = p.id " +
            "WHERE p.sub = :sub ORDER BY n.read_at IS NULL DESC, n.created_at DESC LIMIT :limit", nativeQuery = true)
    List<Notification> findByProfileSub(String sub, int limit);

    Notification findByProfileAndId(Profile profile, Long id);
}
