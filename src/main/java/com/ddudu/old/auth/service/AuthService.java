package com.ddudu.old.auth.service;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.auth.dto.response.MeResponse;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;

  public MeResponse loadUser(Long loginId) {
    User user = userRepository.findById(loginId)
        .orElseThrow(() -> new InvalidTokenException(AuthErrorCode.INVALID_AUTHENTICATION));

    return MeResponse.from(user);
  }

}
