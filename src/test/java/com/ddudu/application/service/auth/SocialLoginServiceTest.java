package com.ddudu.application.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ddudu.application.dto.authentication.request.SocialRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.enums.ProviderType;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.auth.SocialResourcePort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

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
    String accessToken = response.accessToken();
    Long actual = user.getId();
    Long expected = jwtDecoder.decode(accessToken)
        .getClaim("user");

    assertThat(actual).isEqualTo(expected);
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

    assertThat(user).isPresent();

    Long actual = user.get()
        .getId();
    Long expected = jwtDecoder.decode(response.accessToken())
        .getClaim("user");

    assertThat(actual).isEqualTo(expected);
  }

}
