package com.ddudu.user.service;

import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  private final UserRepository userRepository;

  public SignUpResponse signUp(SignUpRequest request) {
    Email email = new Email(request.email());

    if (userRepository.existsByEmail(email)) {
      throw new DuplicateKeyException("이미 존재하는 이메일입니다.");
    }

    UserBuilder userBuilder = User.builder()
        .email(email.getAddress())
        .password(request.password())
        .nickname(request.nickname())
        .passwordEncoder(PASSWORD_ENCODER);

    if (Objects.nonNull(request.optionalUsername())) {
      if (userRepository.existsByOptionalUsername(request.optionalUsername())) {
        throw new DuplicateKeyException("이미 존재하는 아이디입니다.");
      }

      userBuilder.optionalUsername(request.optionalUsername());
    }

    return SignUpResponse.from(userRepository.save(userBuilder.build()));
  }

}
