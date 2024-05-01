package com.ddudu.user.repository;

import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.user.domain.UserSearchType;
import com.ddudu.user.dto.FollowingSearchType;
import java.util.List;

public interface UserDaoCustom {

  List<UserEntity> findFromFollowingBySearchType(
      UserEntity follower, FollowingSearchType searchType
  );

  List<UserEntity> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
