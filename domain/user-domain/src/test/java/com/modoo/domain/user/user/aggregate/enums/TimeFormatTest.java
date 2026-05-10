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
class TimeFormatTest {

  @ParameterizedTest
  @ValueSource(strings = {"12H", "12h"})
  void 열두시간제_대소문자_입력을_성공한다(String input) {
    // given

    // when
    TimeFormat actual = TimeFormat.get(input);

    // then
    assertThat(actual).isEqualTo(TimeFormat.TWELVE_HOUR);
  }

  @ParameterizedTest
  @ValueSource(strings = {"24H", "24h"})
  void 스물네시간제_대소문자_입력을_성공한다(String input) {
    // given

    // when
    TimeFormat actual = TimeFormat.get(input);

    // then
    assertThat(actual).isEqualTo(TimeFormat.TWENTY_FOUR_HOUR);
  }

  @ParameterizedTest
  @ValueSource(strings = {"AMPM", "", " "})
  void 유효하지_않은_입력이면_실패한다(String input) {
    // given

    // when
    ThrowingCallable getTimeFormat = () -> TimeFormat.get(input);

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(getTimeFormat)
        .withMessage(UserErrorCode.INVALID_TIME_FORMAT.getCodeName());
  }

}
