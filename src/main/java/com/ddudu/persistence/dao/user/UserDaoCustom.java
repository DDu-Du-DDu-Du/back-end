package com.ddudu.persistence.dao.user;

import com.ddudu.application.user.domain.UserSearchType;
import com.ddudu.application.user.dto.FollowingSearchType;
import com.ddudu.persistence.entity.UserEntity;
import java.util.List;

public interface UserDaoCustom {

  List<UserEntity> findFromFollowingBySearchType(
      UserEntity follower, FollowingSearchType searchType
  );

  List<UserEntity> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
