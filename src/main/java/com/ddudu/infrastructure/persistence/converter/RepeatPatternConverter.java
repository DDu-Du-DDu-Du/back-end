package com.ddudu.infrastructure.persistence.converter;

import com.ddudu.application.domain.repeat_ddudu.domain.RepeatPattern;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Converter
@Component
@RequiredArgsConstructor
public class RepeatPatternConverter implements AttributeConverter<RepeatPattern, String> {

  private final ObjectMapper objectMapper;

  @Override
  public String convertToDatabaseColumn(RepeatPattern attribute) {
    if (attribute == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting list to JSON", e);
    }
  }

  @Override
  public RepeatPattern convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }

    try {
      return objectMapper.readValue(dbData, RepeatPattern.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error converting JSON to list", e);
    }
  }

}
