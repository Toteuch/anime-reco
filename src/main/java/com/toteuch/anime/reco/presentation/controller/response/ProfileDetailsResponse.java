package com.toteuch.anime.reco.presentation.controller.response;

public class ProfileDetailsResponse extends ExceptionResponse {

    private String sub;
    private String email;
    private String username;

    public ProfileDetailsResponse(String error) {
        super(error);
    }

    public ProfileDetailsResponse() {
        super(null);
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
