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
class LanguageTest {

  @ParameterizedTest
  @ValueSource(strings = {"KO", "ko", "Ko"})
  void KO_대소문자_입력을_성공한다(String input) {
    // given

    // when
    Language actual = Language.get(input);

    // then
    assertThat(actual).isEqualTo(Language.KO);
  }

  @ParameterizedTest
  @ValueSource(strings = {"EN", "en", "En"})
  void EN_대소문자_입력을_성공한다(String input) {
    // given

    // when
    Language actual = Language.get(input);

    // then
    assertThat(actual).isEqualTo(Language.EN);
  }

  @ParameterizedTest
  @ValueSource(strings = {"JP", "", " "})
  void 유효하지_않은_입력이면_실패한다(String input) {
    // given

    // when
    ThrowingCallable getLanguage = () -> Language.get(input);

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(getLanguage)
        .withMessage(UserErrorCode.INVALID_LANGUAGE.getCodeName());
  }

}
