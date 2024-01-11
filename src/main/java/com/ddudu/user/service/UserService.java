package com.ddudu.user.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.common.exception.InvalidTokenException;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.Options;
import com.ddudu.user.domain.Password;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private static final String PASSWORD_UPDATE_SUCCESS = "비밀번호가 성공적으로 변경되었습니다.";

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

  @Transactional
  public UserProfileResponse updateProfile(Long loginId, Long id, UpdateProfileRequest request) {
    if (loginId != id) {
      throw new ForbiddenException(UserErrorCode.INVALID_AUTHORITY);
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));

    user.applyProfileUpdate(request.nickname(), request.introduction());

    return UserProfileResponse.from(user);
  }

  public UserResponse updateEmail(Long loginId, Long userId, UpdateEmailRequest request) {
    if (!loginId.equals(userId)) {
      throw new InvalidTokenException(UserErrorCode.INVALID_AUTHENTICATION);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
    Email newEmail = new Email(request.email());

    if (user.getEmail()
        .equals(newEmail.getAddress())) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EXISTING_EMAIL);
    }

    if (userRepository.existsByEmail(newEmail)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EMAIL);
    }

    user.applyEmailUpdate(newEmail);

    return UserResponse.from(user);
  }

  @Transactional
  public UpdatePasswordResponse updatePassword(
      Long loginId, Long userId, UpdatePasswordRequest request
  ) {
    if (!loginId.equals(userId)) {
      throw new InvalidTokenException(UserErrorCode.INVALID_AUTHENTICATION);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
    Password password = user.getPassword();
    Password newPassword = new Password(request.password(), passwordEncoder);

    if (password.check(request.password(), passwordEncoder)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EXISTING_PASSWORD);
    }

    user.applyPasswordUpdate(newPassword);

    return new UpdatePasswordResponse(PASSWORD_UPDATE_SUCCESS);
  }

  public UserResponse findById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));

    return UserResponse.from(user);
  }

  @Transactional
  public ToggleOptionResponse toggleOption(Long loginId, Long id) {
    if (!loginId.equals(id)) {
      throw new ForbiddenException(UserErrorCode.INVALID_AUTHENTICATION);
    }

    User user = userRepository.findById(loginId)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
    Options options = user.getOptions();

    options.toggleOptions();

    return ToggleOptionResponse.from(user);
  }

}
