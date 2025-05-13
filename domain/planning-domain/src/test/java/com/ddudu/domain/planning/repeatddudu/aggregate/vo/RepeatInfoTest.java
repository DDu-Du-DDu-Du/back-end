package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RepeatInfoTest {

  @Test
  void 일_반복_뚜두_정보를_생성한다() {
    // given

    // when
    RepeatInfo actual = RepeatInfo.day();

    // then
    assertThat(actual).usingRecursiveAssertion()
        .allFieldsSatisfy(Objects::isNull);
  }

  @Test
  void 주_반복_뚜두_정보를_생성한다() {
    // given
    List<String> repeatDaysOfWeek = List.of("월", "화");

    // when
    RepeatInfo actual = RepeatInfo.week(repeatDaysOfWeek);

    // then
    assertThat(actual.repeatDaysOfWeek()).isEqualTo(repeatDaysOfWeek);
  }

  @Test
  void 월_반복_뚜두_정보를_쌩성한다() {
    // given
    List<Integer> repeatDaysOfMonth = List.of(1, 10, 20);
    Boolean lastDayOfMonth = true;

    // when
    RepeatInfo actual = RepeatInfo.month(repeatDaysOfMonth, lastDayOfMonth);

    // then
    assertThat(actual.repeatDaysOfMonth()).isEqualTo(repeatDaysOfMonth);
    assertThat(actual.lastDayOfMonth()).isEqualTo(lastDayOfMonth);
  }

}