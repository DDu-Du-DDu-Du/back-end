package com.modoo.domain.user.user.aggregate.enums;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.common.exception.UserErrorCode;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class WeekStartDayTest {

  @ParameterizedTest
  @ValueSource(strings = {"MON", "mon", "Mon"})
  void MON_대소문자_입력을_성공한다(String input) {
    // given

    // when
    WeekStartDay actual = WeekStartDay.get(input);

    // then
    assertThat(actual).isEqualTo(WeekStartDay.MON);
  }

  @ParameterizedTest
  @ValueSource(strings = {"SUN", "sun", "Sun"})
  void SUN_대소문자_입력을_성공한다(String input) {
    // given

    // when
    WeekStartDay actual = WeekStartDay.get(input);

    // then
    assertThat(actual).isEqualTo(WeekStartDay.SUN);
  }

  @ParameterizedTest
  @ValueSource(strings = {"TUE", "", " "})
  void 유효하지_않은_입력이면_실패한다(String input) {
    // given

    // when
    ThrowingCallable getWeekStartDay = () -> WeekStartDay.get(input);

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(getWeekStartDay)
        .withMessage(UserErrorCode.INVALID_WEEK_START_DAY.getCodeName());
  }

}
