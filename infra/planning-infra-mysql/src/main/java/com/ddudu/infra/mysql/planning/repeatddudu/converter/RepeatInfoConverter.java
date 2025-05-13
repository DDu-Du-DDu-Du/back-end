package com.ddudu.infra.mysql.planning.repeatddudu.converter;

import com.ddudu.infra.mysql.planning.repeatddudu.entity.RepeatInfoEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Converter
@Component
@RequiredArgsConstructor
public class RepeatInfoConverter implements AttributeConverter<RepeatInfoEntity, String> {

  private final ObjectMapper objectMapper;

  @Override
  public String convertToDatabaseColumn(RepeatInfoEntity attribute) {
    if (attribute == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting RepeatInfo to JSON", e);
    }
  }

  @Override
  public RepeatInfoEntity convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }

    try {
      return objectMapper.readValue(dbData, RepeatInfoEntity.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting JSON to RepeatInfo", e);
    }
  }

}
