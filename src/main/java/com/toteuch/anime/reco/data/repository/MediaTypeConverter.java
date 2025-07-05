package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.MediaType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class MediaTypeConverter implements AttributeConverter<List<MediaType>, String> {

    @Override
    public String convertToDatabaseColumn(List<MediaType> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        AtomicReference<String> str = new AtomicReference<>("");
        attribute.forEach(s -> {
            str.set(str.get() + s.getMalCode());
            if (attribute.indexOf(s) != attribute.size() - 1) {
                str.set(str.get() + ",");
            }
        });
        return str.get();
    }

    @Override
    public List<MediaType> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        List<MediaType> mediaTypes = new ArrayList<>();
        String[] codes = dbData.split(",");
        for (String code : codes) {
            mediaTypes.add(Stream.of(MediaType.values())
                    .filter(s -> s.getMalCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new));
        }
        return mediaTypes;
    }
}
