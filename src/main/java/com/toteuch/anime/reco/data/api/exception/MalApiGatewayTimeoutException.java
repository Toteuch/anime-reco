package com.toteuch.anime.reco.data.api.exception;

import org.springframework.http.HttpStatusCode;

public class MalApiGatewayTimeoutException extends MalApiException {
    public MalApiGatewayTimeoutException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
