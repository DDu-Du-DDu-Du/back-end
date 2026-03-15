package com.ddudu.application.user.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.user.request.UpdateUserSettingsRequest;
import com.ddudu.application.common.dto.user.response.UserSettingsResponse;
import com.ddudu.application.common.port.user.out.UserCommandPort;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.UserFixture;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class UpdateUserSettingsServiceTest {

  @Autowired
  UpdateUserSettingsService updateUserSettingsService;

  @Autowired
  UserCommandPort userCommandPort;

  private User user;

  @BeforeEach
  void setUp() {
    // given
    user = userCommandPort.save(UserFixture.createRandomUserWithId());

    // when

    // then
  }

  @Test
  void 로그인_유저_세팅_변경을_성공한다() {
    // given
    UpdateUserSettingsRequest request = createRequest("mon", true, false, 11);

    // when
    UserSettingsResponse actual = updateUserSettingsService.update(user.getId(), request);

    // then
    assertThat(actual.display().weekStartDay()).isEqualTo("MON");
    assertThat(actual.display().isDarkMode()).isTrue();
    assertThat(actual.menuActivation().calendar().isActive()).isFalse();
    assertThat(actual.menuActivation().calendar().priority()).isEqualTo(11);
    assertThat(actual.appConnection().realtimeSync().notion()).isTrue();
    assertThat(actual.appConnection().realtimeSync().googleCalendar()).isFalse();
    assertThat(actual.appConnection().realtimeSync().microsoftTodo()).isTrue();
  }

  @Test
  void 로그인_유저가_없으면_예외를_반환한다() {
    // given
    long missingId = UserFixture.getRandomId();
    UpdateUserSettingsRequest request = createRequest("sun", false, true, 3);

    // when
    ThrowingCallable updateSettings = () -> updateUserSettingsService.update(missingId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(updateSettings)
        .withMessage(UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName());
  }

  private UpdateUserSettingsRequest createRequest(
      String weekStartDay,
      boolean darkMode,
      boolean isActive,
      int priority
  ) {
    return UpdateUserSettingsRequest.builder()
        .display(new UpdateUserSettingsRequest.Display(weekStartDay, darkMode))
        .menuActivation(new UpdateUserSettingsRequest.MenuActivation(
            new UpdateUserSettingsRequest.MenuActivation.MenuActivationItem(isActive, priority),
            new UpdateUserSettingsRequest.MenuActivation.MenuActivationItem(true, priority + 1),
            new UpdateUserSettingsRequest.MenuActivation.MenuActivationItem(false, priority + 2)
        ))
        .appConnection(new UpdateUserSettingsRequest.AppConnection(
            new UpdateUserSettingsRequest.AppConnection.RealtimeSync(true, false, true)
        ))
        .build();
  }

}
