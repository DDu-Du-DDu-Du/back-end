package com.ddudu.application.auth.service;

import com.ddudu.application.auth.jwt.JwtIssuer;
import com.ddudu.application.auth.dto.request.LoginRequest;
import com.ddudu.application.auth.dto.response.LoginResponse;
import com.ddudu.application.auth.dto.response.MeResponse;
import com.ddudu.application.auth.exception.AuthErrorCode;
import com.ddudu.application.common.exception.BadCredentialsException;
import com.ddudu.application.common.exception.DataNotFoundException;
import com.ddudu.application.common.exception.InvalidTokenException;
import com.ddudu.application.config.properties.JwtProperties;
import com.ddudu.application.user.domain.Email;
import com.ddudu.application.user.domain.Password;
import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.domain.UserRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtIssuer jwtIssuer;
  private final JwtProperties jwtProperties;

  public LoginResponse login(LoginRequest request) {
    Email email = new Email(request.email());

    User user = userRepository.findByEmail(email.getAddress())
        .orElseThrow(() -> new DataNotFoundException(AuthErrorCode.EMAIL_NOT_EXISTING));
    Password userPassword = user.getPassword();

    if (!userPassword.check(request.password(), passwordEncoder)) {
      throw new BadCredentialsException(AuthErrorCode.BAD_CREDENTIALS);
    }

    Map<String, Object> claims = new HashMap<>();

    claims.put("user", user.getId());
    claims.put("auth", user.getAuthority());

    String jwt = jwtIssuer.issue(claims, Duration.ofMinutes(jwtProperties.getExpiredAfter()));

    return new LoginResponse(jwt);
  }

  public MeResponse loadUser(Long loginId) {
    User user = userRepository.findById(loginId)
        .orElseThrow(() -> new InvalidTokenException(AuthErrorCode.INVALID_AUTHENTICATION));

    return MeResponse.from(user);
  }

}
