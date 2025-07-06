package com.toteuch.anime.reco.domain.anime;

import com.toteuch.anime.reco.domain.anime.entity.Season;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

public enum SeasonLabel {
    FALL("fall", "Fall", Calendar.SEPTEMBER, 21),
    SPRING("spring", "Spring", Calendar.MARCH, 20),
    SUMMER("summer", "Summer", Calendar.JUNE, 21),
    WINTER("winter", "Winter", Calendar.DECEMBER, 21);

    private final String malCode;
    private final String label;
    private final Integer fromMonth;
    private final Integer fromDay;

    SeasonLabel(String malCode, String label, Integer fromMonth, Integer fromDay) {
        this.malCode = malCode;
        this.label = label;
        this.fromMonth = fromMonth;
        this.fromDay = fromDay;
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

    public Integer getFromMonth() {
        return fromMonth;
    }

    public Integer getFromDay() {
        return fromDay;
    }
}
