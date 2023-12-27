package com.ddudu.user.service;

import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponse signUp(SignUpRequest request) {
    Email email = new Email(request.email());

    if (userRepository.existsByEmail(email)) {
      throw new DuplicateKeyException("이미 존재하는 이메일입니다.");
    }

    UserBuilder userBuilder = User.builder()
        .email(email.getAddress())
        .password(request.password())
        .nickname(request.nickname())
        .passwordEncoder(passwordEncoder);

    if (Objects.nonNull(request.optionalUsername())) {
      if (userRepository.existsByOptionalUsername(request.optionalUsername())) {
        throw new DuplicateKeyException("이미 존재하는 아이디입니다.");
      }

      userBuilder.optionalUsername(request.optionalUsername());
    }

    if (Objects.nonNull(request.introduction())) {
      userBuilder.introduction(request.introduction());
    }

    return SignUpResponse.from(userRepository.save(userBuilder.build()));
  }

  public UserResponse loadFromToken(JwtAuthToken jwtAuthToken) {
    Long userId = jwtAuthToken.getUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new AccessDeniedException("잘못된 토큰입니다."));

    return UserResponse.from(user);
  }

}
