package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;

public class SearchFilterResponse extends ExceptionResponse {
    private SearchFilterPojo searchFilter;

    public SearchFilterResponse(String message) {
        super(message);
    }

    public SearchFilterResponse(SearchFilterPojo searchFilter) {
        this.searchFilter = searchFilter;
    }

    public SearchFilterPojo getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(SearchFilterPojo searchFilter) {
        this.searchFilter = searchFilter;
    }
}
