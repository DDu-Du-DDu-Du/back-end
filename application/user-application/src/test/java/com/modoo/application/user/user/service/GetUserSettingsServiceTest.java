package com.modoo.application.user.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.user.response.UserSettingsResponse;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.common.exception.UserErrorCode;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.UserFixture;
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
    User expected = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    UserSettingsResponse actual = getUserSettingsService.getUserSettings(expected.getId());

    // then
    assertThat(actual.display()
        .weekStartDay()).isEqualTo(expected.getWeekStartDay());
    assertThat(actual.display()
        .isDarkMode()).isEqualTo(expected.isDarkMode());
    assertThat(actual.menuActivation()
        .calendar()
        .isActive())
        .isEqualTo(expected.isActiveCalendar());
    assertThat(actual.menuActivation()
        .calendar()
        .priority())
        .isEqualTo(expected.getPriorityCalendar());
    assertThat(actual.menuActivation()
        .dashboard()
        .isActive())
        .isEqualTo(expected.isActiveDashboard());
    assertThat(actual.menuActivation()
        .dashboard()
        .priority())
        .isEqualTo(expected.getPriorityDashboard());
    assertThat(actual.menuActivation()
        .stats()
        .isActive())
        .isEqualTo(expected.isActiveStats());
    assertThat(actual.menuActivation()
        .stats()
        .priority())
        .isEqualTo(expected.getPriorityStats());
    assertThat(actual.appConnection()
        .realtimeSync()
        .notion())
        .isEqualTo(expected.isRealtimeSyncNotion());
    assertThat(actual.appConnection()
        .realtimeSync()
        .googleCalendar())
        .isEqualTo(expected.isRealtimeSyncGoogleCalendar());
    assertThat(actual.appConnection()
        .realtimeSync()
        .microsoftTodo())
        .isEqualTo(expected.isRealtimeSyncMicrosoftTodo());
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
