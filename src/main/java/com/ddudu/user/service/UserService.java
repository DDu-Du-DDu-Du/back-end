package com.ddudu.user.service;

import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

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

}
