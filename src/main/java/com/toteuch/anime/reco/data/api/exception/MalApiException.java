package com.toteuch.anime.reco.data.api.exception;

import org.springframework.http.HttpStatusCode;

public class MalApiException extends Exception {
    private HttpStatusCode statusCode;
    private String message;

    public MalApiException(HttpStatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
