package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.profile.pojo.NotificationPojo;

import java.util.List;

public class NotificationResponse extends ExceptionResponse {
    List<NotificationPojo> notifications;
    NotificationPojo notification;

    public NotificationResponse() {
    }

    public NotificationResponse(String error) {
        super(error);
    }

    public NotificationResponse(List<NotificationPojo> notifications) {
        super(null);
        this.notifications = notifications;
    }

    public NotificationResponse(NotificationPojo notification) {
        this.notification = notification;
    }

    public List<NotificationPojo> getNotifications() {
        return this.notifications;
    }
}
