package com.ddudu.application.service;

import com.ddudu.application.domain.authentication.dto.request.SocialRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;
import com.ddudu.application.domain.authentication.service.AuthService;
import com.ddudu.application.domain.user.domain.AuthProvider;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.service.UserService;
import com.ddudu.application.port.in.SocialLoginUseCase;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.SocialResourcePort;
import com.ddudu.application.port.out.UserLoaderPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginService implements SocialLoginUseCase {

  private final AuthService authService;
  private final UserService userService;
  private final SocialResourcePort socialResourcePort;
  private final UserLoaderPort userLoaderPort;
  private final SignUpPort signUpPort;

  @Override
  public TokenResponse login(SocialRequest request) {
    AuthProvider authProvider = socialResourcePort.retrieveSocialResource(request);
    Optional<User> user = userLoaderPort.loadSocialUser(authProvider);

    if (user.isPresent()) {
      String accessToken = authService.createAccessToken(user.get());

      return new TokenResponse(accessToken);
    }

    return signUp(authProvider);
  }

  private TokenResponse signUp(AuthProvider authProvider) {
    User newUser = userService.create(authProvider);
    User user = signUpPort.create(newUser);
    String accessToken = authService.createAccessToken(user);

    return new TokenResponse(accessToken);
  }

}
