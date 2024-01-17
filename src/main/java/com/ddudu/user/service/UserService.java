package com.ddudu.user.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UpdateEmailResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UsersResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.UserRepository;
import java.util.List;
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
    verifyUniqueEmail(request.email());

    if (Objects.nonNull(request.optionalUsername()) && userRepository.existsByOptionalUsername(
        request.optionalUsername())) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME);
    }

    User user = User.builder()
        .email(request.email())
        .password(request.password())
        .nickname(request.nickname())
        .introduction(request.introduction())
        .optionalUsername(request.optionalUsername())
        .passwordEncoder(passwordEncoder)
        .build();

    User saved = userRepository.save(user);

    return SignUpResponse.from(saved);
  }

  @Transactional
  public UserProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
    User user = findUser(id);

    user.applyProfileUpdate(request.nickname(), request.introduction());

    return UserProfileResponse.from(user);
  }

  public UpdateEmailResponse updateEmail(Long userId, UpdateEmailRequest request) {
    User user = findUser(userId);
    String newEmail = request.email();

    if (user.isSameEmail(newEmail)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EXISTING_EMAIL);
    }

    verifyUniqueEmail(newEmail);
    user.applyEmailUpdate(newEmail);

    return new UpdateEmailResponse(newEmail.getAddress());
  }

  @Transactional
  public UpdatePasswordResponse updatePassword(Long userId, UpdatePasswordRequest request) {
    User user = findUser(userId);

    user.applyPasswordUpdate(request.password(), passwordEncoder);

    return new UpdatePasswordResponse(PASSWORD_UPDATE_SUCCESS);
  }

  public UserProfileResponse findById(Long userId) {
    User user = findUser(userId);

    return UserProfileResponse.from(user);
  }

  public UsersResponse findFollowees(Long id) {
    User user = findUser(id);

    List<User> followees = userRepository.findFolloweesOfUser(user);

    return UsersResponse.from(followees);
  }

  @Transactional
  public ToggleOptionResponse switchOption(Long id) {
    User user = findUser(id);

    user.switchOptions();

    return ToggleOptionResponse.from(user);
  }

  private User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
  }

  private void verifyUniqueEmail(String email) {
    Email newEmail = new Email(email);

    if (userRepository.existsByEmail(newEmail)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EMAIL);
    }
  }

}
