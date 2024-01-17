package com.ddudu.auth.service;

import com.ddudu.auth.dto.request.LoginRequest;
import com.ddudu.auth.dto.response.LoginResponse;
import com.ddudu.auth.dto.response.MeResponse;
import com.ddudu.auth.exception.AuthErrorCode;
import com.ddudu.auth.jwt.JwtIssuer;
import com.ddudu.common.exception.BadCredentialsException;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.InvalidTokenException;
import com.ddudu.config.properties.JwtProperties;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.Password;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
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

    User user = userRepository.findByEmail(email)
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
