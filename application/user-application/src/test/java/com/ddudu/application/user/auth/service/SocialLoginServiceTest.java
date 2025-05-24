package com.ddudu.application.user.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ddudu.application.common.dto.auth.request.SocialRequest;
import com.ddudu.application.common.dto.auth.response.TokenResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.auth.out.SocialResourcePort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.application.user.auth.jwt.TokenManager;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.ProviderType;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.fixture.UserFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class SocialLoginServiceTest {

  @Autowired
  SocialLoginService socialLoginService;

  @MockBean
  SocialResourcePort socialResourcePort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  TokenManager tokenManager;

  @Autowired
  JwtDecoder jwtDecoder;

  SocialRequest request;
  AuthProvider authProvider;

  @BeforeEach
  void setUp() {
    String socialToken = UUID.randomUUID()
        .toString();
    request = new SocialRequest(socialToken, ProviderType.KAKAO.name());
    authProvider = UserFixture.createRandomAuthProvider();
  }

  @Test
  void 유저가_존재하면_엑세스_토큰을_발급한다() {
    // given
    User user = signUpPort.save(UserFixture.createRandomSocialUser(authProvider));

    when(socialResourcePort.retrieveSocialResource(any(SocialRequest.class)))
        .thenReturn(authProvider);

    // when
    TokenResponse response = socialLoginService.login(request);

    // then
    Jwt jwt = jwtDecoder.decode(response.accessToken());
    Long actualUser = jwt.getClaim("user");
    String actualAuth = jwt.getClaim("auth");

    assertThat(actualUser).isEqualTo(user.getId());
    assertThat(actualAuth).isEqualTo(user.getAuthority()
        .getAuthority());
  }

  @Test
  void 유저가_존재하지_않으면_생성_후_엑세스_토큰을_발급한다() {
    // given
    when(socialResourcePort.retrieveSocialResource(any(SocialRequest.class)))
        .thenReturn(authProvider);

    // when
    TokenResponse response = socialLoginService.login(request);

    // then
    Optional<User> user = userLoaderPort.loadSocialUser(authProvider);

    assertThat(user)
        .isPresent();

    Long expected = user.get()
        .getId();
    Long actual = jwtDecoder.decode(response.accessToken())
        .getClaim("user");

    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void 유저가_생성된_경우_기본_목표가_생성된다() {
    // given
    when(socialResourcePort.retrieveSocialResource(any(SocialRequest.class)))
        .thenReturn(authProvider);

    // when
    socialLoginService.login(request);

    // then
    User user = userLoaderPort.loadSocialUser(authProvider)
        .get();
    List<Goal> goals = goalLoaderPort.findAllByUserAndPrivacyTypes(user.getId());

    assertThat(goals)
        .hasSize(3);
  }

}
