package com.toteuch.anime.reco.data.api.response.animelistuser;

public class AnimeListUserResponse {
    private Data[] data;
    private Paging paging;

    public AnimeListUserResponse() {
    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
