package com.ddudu.auth.service;

import com.ddudu.auth.dto.request.LoginRequest;
import com.ddudu.auth.dto.response.LoginResponse;
import com.ddudu.auth.jwt.JwtIssuer;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.Password;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtIssuer jwtIssuer;

  public LoginResponse login(LoginRequest request) {
    Email email = new Email(request.email());
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("입력하신 이메일은 없는 이메일입니다."));
    Password userPassword = user.getPassword();

    if (!userPassword.check(request.password(), passwordEncoder)) {
      throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
    }

    Map<String, Object> claims = new HashMap<>();

    claims.put("user", user.getId());
    claims.put("auth", user.getAuthority());

    String jwt = jwtIssuer.issue(claims, Duration.ofMinutes(15));

    return new LoginResponse(jwt);
  }

}
