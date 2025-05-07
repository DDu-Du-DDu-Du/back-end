package com.ddudu.application.user.auth.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.service.GoalDomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.service.UserDomainService;
import com.ddudu.application.dto.auth.request.SocialRequest;
import com.ddudu.application.dto.auth.response.TokenResponse;
import com.ddudu.application.port.auth.in.SocialLoginUseCase;
import com.ddudu.application.port.auth.out.SignUpPort;
import com.ddudu.application.port.auth.out.SocialResourcePort;
import com.ddudu.application.port.auth.out.TokenManipulationPort;
import com.ddudu.application.port.goal.out.SaveGoalPort;
import com.ddudu.application.port.user.out.UserLoaderPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SocialLoginService implements SocialLoginUseCase {

  private final TokenManager tokenManager;
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

    List<Goal> defaultGoals = goalDomainService.createDefaultGoals(user.getId());
    saveGoalPort.saveAll(defaultGoals);

    return createTokenSet(user);
  }

  private TokenResponse createTokenSet(User user) {
    String accessToken = tokenManager.createAccessToken(user);
    Integer nextFamily = tokenManipulationPort.getNextFamilyOfUser(user.getId());
    RefreshToken refreshToken = tokenManager.createRefreshToken(user, nextFamily);

    tokenManipulationPort.save(refreshToken);

    return new TokenResponse(accessToken, refreshToken.getTokenValue());
  }

}
