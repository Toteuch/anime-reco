package com.toteuch.anime.reco.domain.anime;

import com.toteuch.anime.reco.domain.anime.entity.Season;
import org.apache.commons.lang3.StringUtils;

public enum SeasonLabel {
    FALL("fall", "Fall"),
    SPRING("spring", "Spring"),
    SUMMER("summer", "Summer"),
    WINTER("winter", "Winter");

    private final String malCode;
    private final String label;

    SeasonLabel(String malCode, String label) {
        this.malCode = malCode;
        this.label = label;
    }

    public static String getLabel(Season season) {
        if (season != null) {
            for (SeasonLabel sl : SeasonLabel.values()) {
                if (StringUtils.equals(sl.malCode, season.getSeason())) {
                    return sl.label;
                }
            }
        }
        return null;
    }

    public String getMalCode() {
        return malCode;
    }

    public String getLabel() {
        return label;
    }
}
