package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.profile.NotificationService;
import com.toteuch.anime.reco.domain.profile.entities.Notification;
import com.toteuch.anime.reco.domain.profile.pojo.NotificationPojo;
import com.toteuch.anime.reco.presentation.controller.response.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NotificationController {

    private static final int NOTIF_DISPLAYED_COUNT = 5;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("notifications/list")
    public NotificationResponse getNotificationList() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            // List all notifications for user
            List<Notification> notifications = notificationService.getAllNotifications(oidcUser.getSubject(), NOTIF_DISPLAYED_COUNT);
            return getNotificationsResponse(notifications);
        } else {
            return new NotificationResponse("Not authenticated");
        }
    }

    @PostMapping("notifications/{id}/read")
    public NotificationResponse read(@PathVariable Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            try {
                Notification notification = notificationService.read(oidcUser.getSubject(), id);
                return getNotificationResponse(notification);
            } catch (AnimeRecoException e) {
                return new NotificationResponse(e.getMessage());
            }
        } else {
            return new NotificationResponse("Not authenticated");
        }
    }

    private NotificationResponse getNotificationResponse(Notification notification) {
        return new NotificationResponse(getNotificationPojo(notification));
    }

    private NotificationResponse getNotificationsResponse(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new NotificationResponse(new ArrayList<>());
        }
        List<NotificationPojo> pojos = new ArrayList<>();
        for (Notification notification : notifications) {
            pojos.add(getNotificationPojo(notification));
        }
        return new NotificationResponse(pojos);
    }

    private NotificationPojo getNotificationPojo(Notification notification) {
        NotificationPojo pojo = new NotificationPojo();
        pojo.setAnimeTitle(notification.getAnime().getTitle());
        pojo.setId(notification.getId());
        pojo.setCreatedAt(notification.getCreatedAt());
        pojo.setType(notification.getType());
        pojo.setReadAt(notification.getReadAt());
        pojo.setMainMediumUrl(notification.getAnime().getMainPictureMediumUrl());
        pojo.setAnimeId(notification.getAnime().getId());
        return pojo;
    }
}
