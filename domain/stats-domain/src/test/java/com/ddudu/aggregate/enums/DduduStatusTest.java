package com.ddudu.aggregate.enums;

import static org.assertj.core.api.Assertions.assertThatRuntimeException;

import com.ddudu.common.exception.StatsErrorCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduStatusTest {

  @ParameterizedTest
  @NullAndEmptySource
  void 통계를_위한_뚜두_상태는_항상_유효해야한다(String value) {
    // given
    ThrowingCallable create = () -> DduduStatus.from(value);

    // when
    assertThatRuntimeException().isThrownBy(create)
        .withMessage(StatsErrorCode.INVALID_DDUDU_STATUS.getCodeName());
  }

}