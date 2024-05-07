package com.ddudu.old.user.service;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import com.ddudu.old.user.dto.request.UpdateProfileRequest;
import com.ddudu.old.user.dto.response.ToggleOptionResponse;
import com.ddudu.old.user.dto.response.UserProfileResponse;
import com.ddudu.old.user.dto.response.UsersResponse;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public UserProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
    User user = findUser(id);

    user.applyProfileUpdate(request.nickname(), request.introduction());

    userRepository.update(user);

    return UserProfileResponse.from(user);
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
    return UserSearchType.OPTIONAL_USERNAME;
  }

  private User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));
  }

}
