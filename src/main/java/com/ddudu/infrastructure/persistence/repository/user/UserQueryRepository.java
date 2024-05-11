package com.ddudu.infrastructure.persistence.repository.user;

import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import java.util.List;

public interface UserQueryRepository {

  List<UserEntity> findFromFollowingBySearchType(
      UserEntity follower, FollowingSearchType searchType
  );

  List<UserEntity> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
