package com.modoo.infra.mysql.user.user.converter;

import com.modoo.domain.user.user.aggregate.enums.TimeFormat;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Objects;

@Converter
public class TimeFormatConverter implements AttributeConverter<TimeFormat, String> {

  @Override
  public String convertToDatabaseColumn(TimeFormat attribute) {
    if (Objects.isNull(attribute)) {
      return null;
    }

    return attribute.getCode();
  }

  @Override
  public TimeFormat convertToEntityAttribute(String dbData) {
    if (Objects.isNull(dbData)) {
      return null;
    }

    return TimeFormat.get(dbData);
  }

}
