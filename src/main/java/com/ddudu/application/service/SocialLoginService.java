package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.authentication.dto.request.SocialRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;
import com.ddudu.application.domain.authentication.service.AuthDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.application.domain.user.service.UserDomainService;
import com.ddudu.application.port.in.SocialLoginUseCase;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.SocialResourcePort;
import com.ddudu.application.port.out.TokenManipulationPort;
import com.ddudu.application.port.out.UserLoaderPort;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SocialLoginService implements SocialLoginUseCase {

  private final AuthDomainService authDomainService;
  private final UserDomainService userDomainService;
  private final SocialResourcePort socialResourcePort;
  private final UserLoaderPort userLoaderPort;
  private final SignUpPort signUpPort;
  private final TokenManipulationPort tokenManipulationPort;

  @Override
  public TokenResponse login(SocialRequest request) {
    AuthProvider authProvider = socialResourcePort.retrieveSocialResource(request);
    Optional<User> user = userLoaderPort.loadSocialUser(authProvider);

    return user.map(this::createTokenSet)
        .orElseGet(() -> signUp(authProvider));
  }

  private TokenResponse signUp(AuthProvider authProvider) {
    User newUser = userDomainService.createFirstUser(authProvider);
    User user = signUpPort.save(newUser);

    return createTokenSet(user);
  }

  private TokenResponse createTokenSet(User user) {
    String accessToken = authDomainService.createAccessToken(user);
    Integer nextFamily = tokenManipulationPort.getNextFamilyOfUser(user.getId());
    RefreshToken refreshToken = authDomainService.createRefreshToken(user, nextFamily);

    tokenManipulationPort.save(refreshToken);

    return new TokenResponse(accessToken, refreshToken.getHashedToken());
  }

}
