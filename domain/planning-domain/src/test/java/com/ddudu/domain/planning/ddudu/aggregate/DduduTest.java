package com.ddudu.domain.planning.ddudu.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu.DduduBuilder;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.fixture.DduduFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduTest {

  Long goalId;
  Long userId;

  @BeforeEach
  void setUp() {
    goalId = DduduFixture.getRandomId();
    userId = DduduFixture.getRandomId();
  }

  @Nested
  class 생성_테스트 {

    String name;

    @BeforeEach
    void setUp() {
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
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
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
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
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
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
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
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
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
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
    }

  }

  @Nested
  class 기능_테스트 {

    Long userId;
    Long goalId;
    Ddudu ddudu;

    @BeforeEach
    void setUp() {
      userId = DduduFixture.getRandomId();
      goalId = DduduFixture.getRandomId();
      ddudu = DduduFixture.createRandomDduduWithReference(goalId, userId, false, null);
    }

    @Nested
    class 권한_테스트 {

      @Test
      void 권한_확인을_성공한다() {
        // given

        // when
        ThrowingCallable check = () -> ddudu.validateDduduCreator(userId);

        // then
        Assertions.assertThatNoException()
            .isThrownBy(check);
      }

      @Test
      void 사용자의_아이디가_다르면_권한_확인을_실패한다() {
        // given
        long wrongUserId = DduduFixture.getRandomId();

        // when
        ThrowingCallable check = () -> ddudu.validateDduduCreator(wrongUserId);

        // then
        Assertions.assertThatExceptionOfType(SecurityException.class)
            .isThrownBy(check)
            .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
      }

    }

    @Nested
    class 기간_설정_테스트 {

      @Test
      void 기간_설정을_성공한다() {
        // given
        LocalTime now = LocalTime.now();

        // when
        Ddudu actual = ddudu.setUpPeriod(now, LocalTime.MAX);

        // then
        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", ddudu.getId())
            .hasFieldOrPropertyWithValue("userId", ddudu.getUserId())
            .hasFieldOrPropertyWithValue("name", ddudu.getName())
            .hasFieldOrPropertyWithValue("isPostponed", ddudu.isPostponed())
            .hasFieldOrPropertyWithValue("status", ddudu.getStatus())
            .hasFieldOrPropertyWithValue("goalId", ddudu.getGoalId())
            .hasFieldOrPropertyWithValue("beginAt", now)
            .hasFieldOrPropertyWithValue("endAt", LocalTime.MAX);
      }

      @Test
      void 시작_시간만_설정할_수_있다() {
        // given
        LocalTime now = LocalTime.now();
        LocalTime expectedEndTime = ddudu.getEndAt();

        // when
        Ddudu actual = ddudu.setUpPeriod(now, null);

        // then
        assertThat(actual.getBeginAt()).isEqualTo(now);
        assertThat(actual.getEndAt()).isEqualTo(expectedEndTime);
      }

      @Test
      void 종료_시간만_설정할_수_있다() {
        // given
        LocalTime now = LocalTime.now();
        LocalTime expectedBeginAt = ddudu.getBeginAt();

        // when
        Ddudu actual = ddudu.setUpPeriod(null, now);

        // then
        assertThat(actual.getBeginAt()).isEqualTo(expectedBeginAt);
        assertThat(actual.getEndAt()).isEqualTo(now);
      }

    }

    @Nested
    class 날짜_변경_테스트 {

      @Test
      void 이미_완료한_뚜두면_날짜_변경_시_상태가_변하지_않는다() {
        // given
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

        ddudu = ddudu.switchStatus(); // 완료 상태로 변경
        assertThat(ddudu.getStatus()).isEqualTo(DduduStatus.COMPLETE);

        // when
        Ddudu actual = ddudu.moveDate(newDate);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isFalse();
      }

      @Test
      void 완료_하지_않은_뚜두의_날짜를_기존_날짜_이후로_변경하면_미루기_상태가_된다() {
        // given
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

        // when
        Ddudu actual = ddudu.moveDate(newDate);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isTrue();
      }

      @Test
      void 완료_하지_않은_뚜두의_날짜를_기존_날짜_이전으로_변경하면_상태가_변하지_않는다() {
        // given
        LocalDate newDate = LocalDate.now()
            .minusDays(1);

        // when
        Ddudu actual = ddudu.moveDate(newDate);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isFalse();
      }

      @Test
      void 변경할_날짜가_누락되면_변경을_실패한다() {
        // given

        // when
        ThrowingCallable moveDate = () -> ddudu.moveDate(null);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(moveDate)
            .withMessage(DduduErrorCode.NULL_DATE_TO_MOVE.getCodeName());
      }

      @Test
      void 변경할_날짜가_예정_날짜보다_이전이어도_이미_미루기한_뚜두면_미루기_상태가_유지된다() {
        // given
        Ddudu postponedDdudu = DduduFixture.createRandomDduduWithReference(
            goalId, userId, true, null);
        LocalDate newDate = LocalDate.now()
            .minusDays(1);

        // when
        Ddudu actual = postponedDdudu.moveDate(newDate);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isTrue();
      }

    }

    @Nested
    class 상태_변경_테스트 {

      @Test
      void 미완료_뚜두는_완료_상태로_변경된다() {
        // given
        DduduStatus before = ddudu.getStatus();
        assertThat(before).isEqualTo(DduduStatus.UNCOMPLETED);

        // when
        Ddudu actual = ddudu.switchStatus();

        // then
        assertThat(actual.getStatus()).isEqualTo(DduduStatus.COMPLETE);
      }

      @Test
      void 완료_뚜두는_미완료_상태로_변경된다() {
        // given
        Ddudu completeDdudu = DduduFixture.createRandomDduduWithReference(
            goalId, userId, false, DduduStatus.COMPLETE);

        // when
        Ddudu actual = completeDdudu.switchStatus();

        // then
        assertThat(actual.getStatus()).isEqualTo(DduduStatus.UNCOMPLETED);
      }

    }

    @Nested
    class 이름_변경_테스트 {

      @Test
      void 뚜두_이름_변경을_성공한다() {
        // given
        String expected = DduduFixture.getRandomSentenceWithMax(50);

        // when
        Ddudu actual = ddudu.changeName(expected);

        // then
        assertThat(actual.getName()).isEqualTo(expected);
      }

      @Test
      void 이름이_50자를_넘으면_변경을_실패한다() {
        // given
        String longName = DduduFixture.getRandomSentence(51, 100);

        // when
        ThrowingCallable changeName = () -> ddudu.changeName(longName);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(changeName)
            .withMessage(DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
      }

    }

  }

  @Nested
  class 복제_테스트 {

    Long userId;
    Long goalId;
    Ddudu ddudu;

    @BeforeEach
    void setUp() {
      userId = DduduFixture.getRandomId();
      goalId = DduduFixture.getRandomId();
      ddudu = DduduFixture.createRandomDduduWithReference(goalId, userId, false, null);
    }

    @Test
    void 뚜두_복제를_성공한다() {
      // given
      LocalDate tomorrow = LocalDate.now()
          .plusDays(1);

      // when
      Ddudu replica = ddudu.reproduceOnDate(tomorrow);

      // then
      assertThat(replica).isNotEqualTo(ddudu);
      assertThat(replica)
          .hasFieldOrPropertyWithValue("goalId", ddudu.getGoalId())
          .hasFieldOrPropertyWithValue("userId", ddudu.getUserId())
          .hasFieldOrPropertyWithValue("name", ddudu.getName())
          .hasFieldOrPropertyWithValue("status", DduduStatus.UNCOMPLETED)
          .hasFieldOrPropertyWithValue("isPostponed", false)
          .hasFieldOrPropertyWithValue("scheduledOn", tomorrow)
          .hasFieldOrPropertyWithValue("beginAt", ddudu.getBeginAt())
          .hasFieldOrPropertyWithValue("endAt", ddudu.getEndAt());
    }

    @Test
    void 같은_날로_복제를_시도하면_실패한다() {
      // given
      LocalDate newDate = ddudu.getScheduledOn();

      // when
      ThrowingCallable reproduce = () -> ddudu.reproduceOnDate(newDate);

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(reproduce)
          .withMessage(DduduErrorCode.UNABLE_TO_REPRODUCE_ON_SAME_DATE.getCodeName());
    }

  }

}
