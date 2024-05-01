package com.ddudu.application.user.service;

import static java.util.Objects.isNull;

import com.ddudu.application.common.exception.DataNotFoundException;
import com.ddudu.application.common.exception.DuplicateResourceException;
import com.ddudu.application.user.domain.UserRepository;
import com.ddudu.application.user.dto.response.SignUpResponse;
import com.ddudu.application.user.dto.response.UpdatePasswordResponse;
import com.ddudu.application.user.dto.response.UserProfileResponse;
import com.ddudu.application.user.dto.response.UsersResponse;
import com.ddudu.application.user.domain.Email;
import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.domain.UserSearchType;
import com.ddudu.application.user.dto.FollowingSearchType;
import com.ddudu.application.user.dto.request.SignUpRequest;
import com.ddudu.application.user.dto.request.UpdateEmailRequest;
import com.ddudu.application.user.dto.request.UpdatePasswordRequest;
import com.ddudu.application.user.dto.request.UpdateProfileRequest;
import com.ddudu.application.user.dto.response.ToggleOptionResponse;
import com.ddudu.application.user.dto.response.UpdateEmailResponse;
import com.ddudu.application.user.exception.UserErrorCode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
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
        .passwordEncoder(passwordEncoder)
        .nickname(request.nickname())
        .introduction(request.introduction())
        .optionalUsername(request.optionalUsername())
        .build();

    User saved = userRepository.save(user);

    return SignUpResponse.from(saved);
  }

  @Transactional
  public UserProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
    User user = findUser(id);

    user.applyProfileUpdate(request.nickname(), request.introduction());

    userRepository.update(user);

    return UserProfileResponse.from(user);
  }

  @Transactional
  public UpdateEmailResponse updateEmail(Long userId, UpdateEmailRequest request) {
    User user = findUser(userId);
    String newEmail = request.email();

    if (user.isSameEmail(newEmail)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EXISTING_EMAIL);
    }

    verifyUniqueEmail(newEmail);
    user.applyEmailUpdate(newEmail);

    userRepository.update(user);

    return new UpdateEmailResponse(user.getEmail());
  }

  @Transactional
  public UpdatePasswordResponse updatePassword(Long userId, UpdatePasswordRequest request) {
    User user = findUser(userId);

    user.applyPasswordUpdate(request.password(), passwordEncoder);

    userRepository.update(user);

    return new UpdatePasswordResponse(PASSWORD_UPDATE_SUCCESS);
  }

  public UserProfileResponse findById(Long userId) {
    User user = findUser(userId);

    return UserProfileResponse.from(user);
  }

  public UsersResponse findFromFollowings(Long id, FollowingSearchType searchType) {
    User user = findUser(id);

    List<User> users = userRepository.findFromFollowingBySearchType(user, searchType);

    return UsersResponse.from(users);
  }

  @Transactional
  public ToggleOptionResponse switchOption(Long id) {
    User user = findUser(id);

    user.switchOptions();

    userRepository.update(user);

    return ToggleOptionResponse.from(user);
  }

  public List<UserProfileResponse> search(String keyword, UserSearchType searchType) {
    if (Strings.isBlank(keyword)) {
      return Collections.emptyList();
    }

    if (isNull(searchType)) {
      searchType = determineSearchType(keyword);
    }

    List<User> users = userRepository.findAllByKeywordAndSearchType(keyword, searchType);

    return users.stream()
        .map(UserProfileResponse::from)
        .toList();
  }

  private UserSearchType determineSearchType(String keyword) {
    if (Email.isValidEmail(keyword)) {
      return UserSearchType.EMAIL;
    }
    return UserSearchType.OPTIONAL_USERNAME;
  }

  private User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
  }

  private void verifyUniqueEmail(String email) {
    Email newEmail = new Email(email);

    if (userRepository.existsByEmail(newEmail.getAddress())) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EMAIL);
    }

  }

}
