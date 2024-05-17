package com.ddudu.infrastructure.persistence.repository.user;

import com.ddudu.infrastructure.persistence.dto.FullUser;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import java.util.List;
import java.util.Optional;

public interface UserQueryRepository {

  List<UserEntity> findFromFollowingBySearchType(
      UserEntity follower, FollowingSearchType searchType
  );

  Optional<FullUser> fetchFullUserById(Long id);

  List<UserEntity> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
