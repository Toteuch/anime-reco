package com.toteuch.anime.reco.domain.anime;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public enum MediaType {
    TV("tv", "TV"),
    MOVIE("movie", "Movie"),
    OVA("ova", "OVA"),
    TV_SPECIAL("tv_special", "TV Special"),
    SPECIAL("special", "Special"),
    ONLINE("ona", "Online"),
    MUSIC("music", "Music"),
    COMMERCIAL("cm", "Commercial"),
    PILOT("pv", "Pilot"),
    UNKNOWN("unknown", "Unknown");

    private final String malCode;
    private final String label;

    MediaType(String malCode, String label) {
        this.malCode = malCode;
        this.label = label;
    }

    public static Map<String, String> getMediaTypesMap() {
        Map<String, String> mtMap = new HashMap<>();
        Arrays.stream(MediaType.values()).toList().forEach(mt -> mtMap.put(mt.getMalCode(), mt.getLabel()));
        return mtMap;
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

    public String getLabel() {
        return label;
    }
}
