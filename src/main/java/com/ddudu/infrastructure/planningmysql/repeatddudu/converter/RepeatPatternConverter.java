package com.ddudu.infrastructure.planningmysql.repeatddudu.converter;

import com.ddudu.domain.planning.repeatddudu.aggregate.DailyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.MonthlyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.WeeklyRepeatPattern;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import java.util.Optional;
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
      throw new IllegalArgumentException("Error converting RepeatPattern to JSON", e);
    }
  }

  @Override
  public RepeatPattern convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }

    try {
      JsonNode jsonNode = objectMapper.readTree(dbData);
      String type = Optional.ofNullable(jsonNode.get("repeatType"))
          .orElseGet(() -> jsonNode.get("type"))
          .asText();

      return switch (type) {
        case "DAILY" -> objectMapper.treeToValue(jsonNode, DailyRepeatPattern.class);
        case "WEEKLY" -> getWeeklyRepeatPattern(jsonNode);
        case "MONTHLY" -> getMonthlyRepeatPattern(jsonNode);
        default -> throw new IllegalArgumentException("Unknown type: " + type);
      };
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting JSON to RepeatPattern", e);
    }
  }

  private WeeklyRepeatPattern getWeeklyRepeatPattern(JsonNode jsonNode)
      throws JsonProcessingException {
    List<String> repeatDaysOfWeek = parseJsonNodeToList(jsonNode.get("repeatDaysOfWeek"));
    return new WeeklyRepeatPattern(repeatDaysOfWeek);
  }

  private MonthlyRepeatPattern getMonthlyRepeatPattern(JsonNode jsonNode)
      throws JsonProcessingException {
    List<Integer> repeatDaysOfMonth = parseJsonNodeToList(jsonNode.get("repeatDaysOfMonth"));
    boolean isLastDayOfMonth = jsonNode.get("lastDay")
        .asBoolean();
    return new MonthlyRepeatPattern(repeatDaysOfMonth, isLastDayOfMonth);
  }

  private <T> T parseJsonNodeToList(JsonNode jsonNode)
      throws JsonProcessingException {
    if (jsonNode == null || jsonNode.isNull()) {
      throw new IllegalArgumentException("Missing expected JSON field");
    }
    return objectMapper.readValue(jsonNode.toString(), new TypeReference<>() {
    });
  }

}
