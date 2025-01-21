package com.toteuch.anime.reco.data.api.response.animedetails;

public class AlternativeTitlesResponse {
    private String[] synonyms;
    private String en;
    private String ja;

    public AlternativeTitlesResponse() {
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getJa() {
        return ja;
    }

    public void setJa(String ja) {
        this.ja = ja;
    }
}
