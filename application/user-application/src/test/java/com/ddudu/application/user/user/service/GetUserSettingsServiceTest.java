package com.ddudu.application.user.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.user.response.UserSettingsResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.ddudu.domain.user.user.aggregate.vo.DisplayOptions;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationItem;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import com.ddudu.fixture.UserFixture;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GetUserSettingsServiceTest {

  @Autowired
  GetUserSettingsService getUserSettingsService;

  @Autowired
  SignUpPort signUpPort;

  @Test
  void 내_세팅_불러오기를_성공한다() {
    // given
    User expected = signUpPort.save(UserFixture.createRandomUser(
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
                .stats(MenuActivationItem.builder().active(true).priority(2).build())
                .build())
            .appConnection(AppConnectionOptions.builder()
                .realtimeSync(RealtimeSyncOptions.builder()
                    .notion(true)
                    .googleCalendar(false)
                    .microsoftTodo(true)
                    .build())
                .build())
            .build(),
        null,
        null,
        null
    ));

    // when
    UserSettingsResponse actual = getUserSettingsService.getUserSettings(expected.getId());

    // then
    assertThat(actual.display().weekStartDay()).isEqualTo(WeekStartDay.MON);
    assertThat(actual.display().isDarkMode()).isTrue();
    assertThat(actual.menuActivation().calendar().isActive()).isFalse();
    assertThat(actual.menuActivation().calendar().priority()).isEqualTo(3);
    assertThat(actual.menuActivation().dashboard().isActive()).isTrue();
    assertThat(actual.menuActivation().dashboard().priority()).isEqualTo(1);
    assertThat(actual.menuActivation().stats().isActive()).isTrue();
    assertThat(actual.menuActivation().stats().priority()).isEqualTo(2);
    assertThat(actual.appConnection().realtimeSync().notion()).isTrue();
    assertThat(actual.appConnection().realtimeSync().googleCalendar()).isFalse();
    assertThat(actual.appConnection().realtimeSync().microsoftTodo()).isTrue();
  }

  @Test
  void 존재하지_않는_사용자는_내_세팅_불러오기를_할_수_없다() {
    // given
    long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable getUserSettings = () -> getUserSettingsService.getUserSettings(invalidId);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(getUserSettings)
        .withMessage(UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName());
  }

}
