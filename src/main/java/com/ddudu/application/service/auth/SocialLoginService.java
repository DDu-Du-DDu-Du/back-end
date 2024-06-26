package com.ddudu.application.service.auth;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.authentication.service.AuthDomainService;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.application.domain.user.service.UserDomainService;
import com.ddudu.application.dto.authentication.request.SocialRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;
import com.ddudu.application.port.in.auth.SocialLoginUseCase;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.auth.SocialResourcePort;
import com.ddudu.application.port.out.auth.TokenManipulationPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SocialLoginService implements SocialLoginUseCase {

  private final AuthDomainService authDomainService;
  private final UserDomainService userDomainService;
  private final GoalDomainService goalDomainService;
  private final SocialResourcePort socialResourcePort;
  private final UserLoaderPort userLoaderPort;
  private final SignUpPort signUpPort;
  private final TokenManipulationPort tokenManipulationPort;
  private final SaveGoalPort saveGoalPort;

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

    List<Goal> defaultGoals = goalDomainService.createDefaultGoals(user);
    saveGoalPort.saveAll(defaultGoals);

    return createTokenSet(user);
  }

  private TokenResponse createTokenSet(User user) {
    String accessToken = authDomainService.createAccessToken(user);
    Integer nextFamily = tokenManipulationPort.getNextFamilyOfUser(user.getId());
    RefreshToken refreshToken = authDomainService.createRefreshToken(user, nextFamily);

    tokenManipulationPort.save(refreshToken);

    return new TokenResponse(accessToken, refreshToken.getTokenValue());
  }

}
