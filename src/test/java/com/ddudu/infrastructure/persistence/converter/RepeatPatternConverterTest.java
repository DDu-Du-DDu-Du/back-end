package com.ddudu.infrastructure.persistence.converter;

import com.ddudu.domain.planning.repeatddudu.aggregate.DailyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.MonthlyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.WeeklyRepeatPattern;
import com.ddudu.infrastructure.planningmysql.repeatddudu.converter.RepeatPatternConverter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepeatPatternConverterTest {

  @Autowired
  RepeatPatternConverter repeatPatternConverter;

  @Test
  void toDatabaseColumn() {
    // Given
    RepeatPattern dailyRepeatPattern = new DailyRepeatPattern();
    RepeatPattern weeklyRepeatPattern = new WeeklyRepeatPattern(List.of("월"));
    RepeatPattern monthlyRepeatPattern = new MonthlyRepeatPattern(List.of(1, 15), false);

    // When
    String dailyString = repeatPatternConverter.convertToDatabaseColumn(dailyRepeatPattern);
    System.out.println("dailyString = " + dailyString);
    String weeklyString = repeatPatternConverter.convertToDatabaseColumn(weeklyRepeatPattern);
    System.out.println("weeklyString = " + weeklyString);
    String monthlyString = repeatPatternConverter.convertToDatabaseColumn(monthlyRepeatPattern);
    System.out.println("monthlyString = " + monthlyString);

    RepeatPattern daily = repeatPatternConverter.convertToEntityAttribute(dailyString);
    RepeatPattern weekly = repeatPatternConverter.convertToEntityAttribute(weeklyString);
    RepeatPattern monthly = repeatPatternConverter.convertToEntityAttribute(monthlyString);

    String dailyAgain = repeatPatternConverter.convertToDatabaseColumn(daily);
    System.out.println("dailyAgain = " + dailyAgain);
    String weeklyAgain = repeatPatternConverter.convertToDatabaseColumn(weekly);
    System.out.println("weeklyAgain = " + weeklyAgain);
    String monthlyAgain = repeatPatternConverter.convertToDatabaseColumn(monthly);
    System.out.println("monthlyAgain = " + monthlyAgain);
  }

}
