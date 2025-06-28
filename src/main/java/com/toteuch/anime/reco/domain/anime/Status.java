package com.toteuch.anime.reco.domain.anime;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public enum Status {
    COMING("not_yet_aired", "Incoming"),
    ONGOING("currently_airing", "Ongoing"),
    FINISHED("finished_airing", "Completed");


    private final String malCode;
    private final String label;

    Status(String mal_code, String label) {
        this.malCode = mal_code;
        this.label = label;
    }

    public static Map<String, String> getStatusMap() {
        Map<String, String> statusMap = new HashMap<>();
        Arrays.stream(values()).toList().forEach(s -> statusMap.put(s.malCode, s.label));
        return statusMap;
    }

    public static Status getByMalCode(String malCode) {
        for (Status s : Status.values()) {
            if (StringUtils.equalsIgnoreCase(s.malCode, malCode)) {
                return s;
            }
        }
        return null;
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

    public String getLabel() {
        return label;
    }
}
