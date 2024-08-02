package com.ddudu.application.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.ddudu.application.dto.user.MeResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.MissingResourceException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GetMyInfoServiceTest {

  @Autowired
  GetMyInfoService getMyInfoService;

  @Autowired
  SignUpPort signUpPort;

  @Test
  void 내_정보_불러오기를_성공한다() {
    // given
    User expected = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    MeResponse actual = getMyInfoService.getMyInfo(expected.getId());

    // then
    assertThat(actual)
        .hasFieldOrPropertyWithValue("id", expected.getId())
        .hasFieldOrPropertyWithValue("username", expected.getUsername())
        .hasFieldOrPropertyWithValue("nickname", expected.getNickname())
        .hasFieldOrPropertyWithValue("profileImageUrl", expected.getProfileImageUrl())
        .hasFieldOrPropertyWithValue("authority", expected.getAuthority());
  }

  @Test
  void 존재하지_않는_사용자는_내_정보_불러오기를_할_수_없다() {
    // given
    long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable getMyInfo = () -> getMyInfoService.getMyInfo(invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(getMyInfo)
        .withMessage(UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName());
  }

}
