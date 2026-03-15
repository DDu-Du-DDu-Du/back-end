package com.ddudu.domain.user.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.aggregate.vo.DisplayOptions;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationItem;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import com.ddudu.domain.user.user.service.dto.UserSettingsInfo;
import com.ddudu.fixture.UserFixture;
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
  class 유저_세팅_조회_정보_생성_테스트 {

    @Test
    void 유저_옵션을_세팅_조회_정보로_변환한다() {
      // given
      User user = UserFixture.createRandomUser(
          UserFixture.getRandomId(),
          null,
          null,
          Options.builder()
              .display(DisplayOptions.builder()
                  .weekStartDay(WeekStartDay.MON)
                  .darkMode(true)
                  .build())
              .menuActivation(MenuActivationOptions.builder()
                  .calendar(MenuActivationItem.builder().active(false).priority(3).build())
                  .dashboard(MenuActivationItem.builder().active(true).priority(1).build())
                  .stats(MenuActivationItem.builder().active(false).priority(2).build())
                  .build())
              .appConnection(AppConnectionOptions.builder()
                  .realtimeSync(RealtimeSyncOptions.builder()
                      .notion(true)
                      .googleCalendar(true)
                      .microsoftTodo(false)
                      .build())
                  .build())
              .build(),
          null,
          null,
          null
      );

      // when
      UserSettingsInfo actual = userDomainService.createUserSettingsInfo(user);

      // then
      assertThat(actual.display().weekStartDay()).isEqualTo(WeekStartDay.MON);
      assertThat(actual.display().isDarkMode()).isTrue();
      assertThat(actual.menuActivation().calendar().isActive()).isFalse();
      assertThat(actual.menuActivation().calendar().priority()).isEqualTo(3);
      assertThat(actual.menuActivation().dashboard().isActive()).isTrue();
      assertThat(actual.menuActivation().dashboard().priority()).isEqualTo(1);
      assertThat(actual.menuActivation().stats().isActive()).isFalse();
      assertThat(actual.menuActivation().stats().priority()).isEqualTo(2);
      assertThat(actual.appConnection().realtimeSync().notion()).isTrue();
      assertThat(actual.appConnection().realtimeSync().googleCalendar()).isTrue();
      assertThat(actual.appConnection().realtimeSync().microsoftTodo()).isFalse();
    }

  }

}
