package com.toteuch.anime.reco.data.api.exception;

import org.springframework.http.HttpStatusCode;

public class MalApiListVisibilityException extends MalApiException {
    public MalApiListVisibilityException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
