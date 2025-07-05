package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;

import java.util.List;

public class ListSearchFilterResponse extends ExceptionResponse {

    List<SearchFilterPojo> searchFilters;

    public ListSearchFilterResponse(String message) {
        super(message);
    }

    public ListSearchFilterResponse(List<SearchFilterPojo> searchFilters) {
        this.searchFilters = searchFilters;
    }

    public List<SearchFilterPojo> getSearchFilters() {
        return searchFilters;
    }

    public void setSearchFilters(List<SearchFilterPojo> searchFilters) {
        this.searchFilters = searchFilters;
    }
}
