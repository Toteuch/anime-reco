package com.toteuch.anime.reco.data.api.response.animelistuser;

public class Paging {
    private String next;
    private String previous;

    public Paging() {
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
