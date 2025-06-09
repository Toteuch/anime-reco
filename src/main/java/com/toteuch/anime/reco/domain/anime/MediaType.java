package com.toteuch.anime.reco.domain.anime;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum MediaType {
    TV("tv"),
    MOVIE("movie"),
    OVA("ova"),
    TV_SPECIAL("tv_special"),
    SPECIAL("special"),
    ONLINE("ona"),
    MUSIC("music"),
    COMMERCIAL("cm"),
    PILOT("pv"),
    UNKNOWN("unknown");

    private final String malCode;

    MediaType(String malCode) {
        this.malCode = malCode;
    }

    public static List<MediaType> parseMediaTypes(List<String> malCodeList) {
        List<MediaType> mediaTypes = new ArrayList<>();
        if (malCodeList == null) return mediaTypes;
        malCodeList.forEach(mc -> {
            for (MediaType mt : MediaType.values()) {
                if (StringUtils.equalsIgnoreCase(mt.malCode, mc)) {
                    mediaTypes.add(mt);
                    break;
                }
            }
        });
        return mediaTypes;
    }

    public static List<String> getMalCode(List<MediaType> mediaTypes) {
        List<String> malCodes = new ArrayList<>();
        mediaTypes.forEach(mt -> malCodes.add(mt.getMalCode()));
        return malCodes;
    }

    public String getMalCode() {
        return malCode;
    }
}
