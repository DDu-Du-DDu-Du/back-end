package com.ddudu.old.persistence.dao.user;

import com.ddudu.old.persistence.entity.UserEntity;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import java.util.List;

public interface UserDaoCustom {

  List<UserEntity> findFromFollowingBySearchType(
      UserEntity follower, FollowingSearchType searchType
  );

  List<UserEntity> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
