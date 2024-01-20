package com.ddudu.user.repository;

import com.ddudu.user.domain.User;
import com.ddudu.user.domain.UserSearchType;
import com.ddudu.user.dto.FollowingSearchType;
import java.util.List;

public interface UserRepositoryCustom {

  List<User> findFromFollowingBySearchType(User follower, FollowingSearchType searchType);

  List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
