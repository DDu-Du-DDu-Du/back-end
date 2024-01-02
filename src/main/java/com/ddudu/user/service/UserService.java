package com.ddudu.user.service;

import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.common.exception.InvalidTokenException;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EMAIL);
    }

    UserBuilder userBuilder = User.builder()
        .email(email.getAddress())
        .password(request.password())
        .nickname(request.nickname())
        .passwordEncoder(passwordEncoder);

    if (Objects.nonNull(request.optionalUsername())) {
      if (userRepository.existsByOptionalUsername(request.optionalUsername())) {
        throw new DuplicateResourceException(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME);
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
        .orElseThrow(() -> new InvalidTokenException(UserErrorCode.INVALID_AUTHENTICATION));

    return UserResponse.from(user);
  }

}
