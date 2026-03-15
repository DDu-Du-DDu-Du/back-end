package com.ddudu.domain.user.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.fixture.UserFixture;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserDomainServiceTest {

  static UserDomainService userDomainService;

  @BeforeAll
  static void setUp() {
    userDomainService = new UserDomainService();
  }

  @Nested
  class 회원_생성_테스트 {

    @Test
    void 가입_유저를_생성한다() {
      // given
      AuthProvider authProvider = UserFixture.createRandomAuthProvider();

      // when
      User firstUser = userDomainService.createFirstUser(authProvider);

      // then
      assertThat(firstUser.getAuthProviders()).containsOnly(authProvider);
    }

  }

  @Nested
  class 옵션_변환_테스트 {

    @Test
    void 유저_세팅_요청값으로_옵션을_생성한다() {
      // given
      User user = UserFixture.createRandomUserWithId();

      // when
      Options actual = userDomainService.buildUpdatedOptions(
          user,
          "mon",
          true,
          false,
          4,
          true,
          5,
          false,
          6,
          true,
          false,
          true
      );

      // then
      assertThat(actual.getDisplay().getWeekStartDay()).isEqualTo(WeekStartDay.MON);
      assertThat(actual.getDisplay().isDarkMode()).isTrue();
      assertThat(actual.getMenuActivation().getCalendar().isActive()).isFalse();
      assertThat(actual.getMenuActivation().getCalendar().getPriority()).isEqualTo(4);
      assertThat(actual.getMenuActivation().getDashboard().isActive()).isTrue();
      assertThat(actual.getMenuActivation().getDashboard().getPriority()).isEqualTo(5);
      assertThat(actual.getMenuActivation().getStats().isActive()).isFalse();
      assertThat(actual.getMenuActivation().getStats().getPriority()).isEqualTo(6);
      assertThat(actual.getAppConnection().getRealtimeSync().isNotion()).isTrue();
      assertThat(actual.getAppConnection().getRealtimeSync().isGoogleCalendar()).isFalse();
      assertThat(actual.getAppConnection().getRealtimeSync().isMicrosoftTodo()).isTrue();
      assertThat(actual.isAllowingFollowsAfterApproval()).isEqualTo(user.isAllowingFollowsAfterApproval());
      assertThat(actual.isTemplateNotification()).isEqualTo(user.isNotifyingTemplate());
      assertThat(actual.isDduduNotification()).isEqualTo(user.isNotifyingDdudu());
    }

    @Test
    void 주_시작_요일이_유효하지_않으면_옵션_생성을_실패한다() {
      // given
      User user = UserFixture.createRandomUserWithId();

      // when
      ThrowingCallable buildUpdatedOptions = () -> userDomainService.buildUpdatedOptions(
          user,
          "tue",
          true,
          false,
          1,
          false,
          2,
          false,
          3,
          false,
          false,
          false
      );

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(buildUpdatedOptions)
          .withMessage(UserErrorCode.INVALID_WEEK_START_DAY.getCodeName());
    }

  }

}
