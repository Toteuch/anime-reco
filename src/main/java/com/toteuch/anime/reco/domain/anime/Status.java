package com.toteuch.anime.reco.domain.anime;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum Status {
    FINISHED("finished_airing"),
    ONGOING("currently_airing"),
    COMING("not_yet_aired");
    private final String malCode;

    Status(String mal_code) {
        this.malCode = mal_code;
    }

    public static List<Status> parseStatusList(List<String> malCodeList) {
        List<Status> statusList = new ArrayList<>();
        if (malCodeList == null) return statusList;
        malCodeList.forEach(mc -> {
            for (Status s : Status.values()) {
                if (StringUtils.equalsIgnoreCase(s.malCode, mc)) {
                    statusList.add(s);
                    break;
                }
            }
        });
        return statusList;
    }

    public static List<String> getMalCodes(List<Status> statusList) {
        List<String> malCodes = new ArrayList<>();
        statusList.forEach(s -> malCodes.add(s.getMalCode()));
        return malCodes;
    }

    public String getMalCode() {
        return malCode;
    }
}
