package com.ddudu.infra.mysql.planning.repeatddudu.converter;

import com.ddudu.infra.mysql.planning.repeatddudu.entity.RepeatInfoEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepeatInfoEntityConverterTest {

  @Autowired
  RepeatInfoConverter repeatInfoConverter;

  @Test
  void toDatabaseColumn() {
    // Given
    RepeatInfoEntity dailyRepeatPattern = new RepeatInfoEntity(null, null, null);
    RepeatInfoEntity weeklyRepeatPattern = new RepeatInfoEntity(List.of("월"), null, null);
    RepeatInfoEntity monthlyRepeatPattern = new RepeatInfoEntity(null, List.of(1, 15), false);

    // When
    String dailyString = repeatInfoConverter.convertToDatabaseColumn(dailyRepeatPattern);
    System.out.println("dailyString = " + dailyString);
    String weeklyString = repeatInfoConverter.convertToDatabaseColumn(weeklyRepeatPattern);
    System.out.println("weeklyString = " + weeklyString);
    String monthlyString = repeatInfoConverter.convertToDatabaseColumn(monthlyRepeatPattern);
    System.out.println("monthlyString = " + monthlyString);

    RepeatInfoEntity daily = repeatInfoConverter.convertToEntityAttribute(dailyString);
    RepeatInfoEntity weekly = repeatInfoConverter.convertToEntityAttribute(weeklyString);
    RepeatInfoEntity monthly = repeatInfoConverter.convertToEntityAttribute(monthlyString);

    String dailyAgain = repeatInfoConverter.convertToDatabaseColumn(daily);
    System.out.println("dailyAgain = " + dailyAgain);
    String weeklyAgain = repeatInfoConverter.convertToDatabaseColumn(weekly);
    System.out.println("weeklyAgain = " + weeklyAgain);
    String monthlyAgain = repeatInfoConverter.convertToDatabaseColumn(monthly);
    System.out.println("monthlyAgain = " + monthlyAgain);
  }

}
