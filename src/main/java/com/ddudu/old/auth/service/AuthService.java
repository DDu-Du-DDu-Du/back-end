package com.ddudu.old.auth.service;

import com.ddudu.old.auth.dto.request.LoginRequest;
import com.ddudu.old.auth.dto.response.LoginResponse;
import com.ddudu.old.auth.dto.response.MeResponse;
import com.ddudu.old.auth.exception.AuthErrorCode;
import com.ddudu.old.auth.jwt.JwtIssuer;
import com.ddudu.presentation.api.exception.BadCredentialsException;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.InvalidTokenException;
import com.ddudu.old.config.properties.JwtProperties;
import com.ddudu.old.user.domain.Email;
import com.ddudu.old.user.domain.Password;
import com.ddudu.old.user.domain.User;
import com.ddudu.old.user.domain.UserRepository;
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
