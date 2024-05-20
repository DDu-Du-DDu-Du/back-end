package com.ddudu.application.domain.ddudu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.application.domain.ddudu.domain.Ddudu.DduduBuilder;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.fixture.DduduFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduTest {

  Long goalId;
  Long userId;
  String name;

  @BeforeEach
  void setUp() {
    goalId = DduduFixture.getRandomId();
    userId = DduduFixture.getRandomId();
    name = DduduFixture.getRandomSentenceWithMax(50);
  }

  @Test
  void 뚜두_생성을_성공한다() {
    // given

    // when
    Ddudu ddudu = Ddudu.builder()
        .goalId(goalId)
        .userId(userId)
        .name(name)
        .status(DduduStatus.COMPLETE)
        .isPostponed(true)
        .build();

    // then
    assertThat(ddudu).isNotNull();
  }

  @Test
  void 뚜두_생성_시_디폴트_값이_적용된다() {
    // given

    // when
    Ddudu ddudu = Ddudu.builder()
        .goalId(goalId)
        .userId(userId)
        .name(name)
        .build();

    // then
    assertThat(ddudu.getStatus()).isEqualTo(DduduStatus.UNCOMPLETED);
    assertThat(ddudu.isPostponed()).isFalse();
    assertThat(ddudu.getScheduledOn()).isEqualTo(LocalDate.now());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = " ")
  void 이름이_빈_값이면_생성을_실패한다(String blankName) {
    // given
    DduduBuilder builder = Ddudu.builder()
        .goalId(goalId)
        .userId(userId)
        .name(blankName);

    // when
    ThrowingCallable create = builder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(DduduErrorCode.BLANK_NAME.getCodeName());
  }

  @Test
  void 이름이_50자를_넘으면_생성을_실패한다() {
    // given
    String over50 = DduduFixture.getRandomSentence(51, 100);
    DduduBuilder builder = Ddudu.builder()
        .goalId(goalId)
        .userId(userId)
        .name(over50);

    // when
    ThrowingCallable create = builder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

  @Test
  void 목표가_없으면_생성을_실패한다() {
    // given
    DduduBuilder builder = Ddudu.builder()
        .userId(userId)
        .name(name);

    // when
    ThrowingCallable create = builder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(DduduErrorCode.NULL_GOAL_VALUE.getCodeName());
  }

  @Test
  void 사용자가_없으면_생성을_실패한다() {
    // given
    DduduBuilder builder = Ddudu.builder()
        .goalId(goalId)
        .name(name);

    // when
    ThrowingCallable create = builder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(DduduErrorCode.NULL_USER.getCodeName());
  }

  @Test
  void 시작_시간이_종료_시간보다_뒤면_생성을_실패한다() {
    // given
    DduduBuilder builder = Ddudu.builder()
        .goalId(goalId)
        .userId(userId)
        .name(name)
        .beginAt(LocalTime.now()
            .plusMinutes(1))
        .endAt(LocalTime.now());

    // when
    ThrowingCallable create = builder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
  }

}