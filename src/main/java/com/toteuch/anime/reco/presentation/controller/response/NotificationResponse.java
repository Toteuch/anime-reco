package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.profile.pojo.NotificationPojo;

import java.util.List;

public class NotificationResponse extends ExceptionResponse {
    List<NotificationPojo> notifications;
    NotificationPojo notification;
    Integer pageNumber;
    Integer totalElements;

    public NotificationResponse() {
    }

    public NotificationResponse(String error) {
        super(error);
    }

    public NotificationResponse(List<NotificationPojo> notifications, Integer totalElements, Integer pageNumber) {
        super(null);
        this.notifications = notifications;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
    }

    public NotificationResponse(NotificationPojo notification) {
        this.notification = notification;
    }

    public List<NotificationPojo> getNotifications() {
        return this.notifications;
    }

    public NotificationPojo getNotification() {
        return notification;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getTotalElements() {
        return totalElements;
    }
}
