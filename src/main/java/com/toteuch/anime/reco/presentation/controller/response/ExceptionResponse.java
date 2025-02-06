package com.toteuch.anime.reco.presentation.controller.response;

public class ExceptionResponse {
    private String error;

    public ExceptionResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
