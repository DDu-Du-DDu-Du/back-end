package com.ddudu.user.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
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
        .introduction(request.introduction())
        .passwordEncoder(passwordEncoder);

    if (Objects.nonNull(request.optionalUsername())) {
      if (userRepository.existsByOptionalUsername(request.optionalUsername())) {
        throw new DuplicateResourceException(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME);
      }

      userBuilder.optionalUsername(request.optionalUsername());
    }

    return SignUpResponse.from(userRepository.save(userBuilder.build()));
  }

  @Transactional
  public UserProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
    User user = findUser(id);

    user.applyProfileUpdate(request.nickname(), request.introduction());

    return UserProfileResponse.from(user);
  }

  public UserResponse updateEmail(Long userId, UpdateEmailRequest request) {
    User user = findUser(userId);
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
  public UpdatePasswordResponse updatePassword(Long userId, UpdatePasswordRequest request) {
    User user = findUser(userId);
    Password password = user.getPassword();
    Password newPassword = new Password(request.password(), passwordEncoder);

    if (password.check(request.password(), passwordEncoder)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EXISTING_PASSWORD);
    }

    user.applyPasswordUpdate(newPassword);

    return new UpdatePasswordResponse(PASSWORD_UPDATE_SUCCESS);
  }

  public UserResponse findById(Long userId) {
    User user = findUser(userId);

    return UserResponse.from(user);
  }

  @Transactional
  public ToggleOptionResponse switchOption(Long id) {
    User user = findUser(id);
    Options options = user.getOptions();

    options.switchOptions();

    return ToggleOptionResponse.from(user);
  }

  private User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
  }

}
