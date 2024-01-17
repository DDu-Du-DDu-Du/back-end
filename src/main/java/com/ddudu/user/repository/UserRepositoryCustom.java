package com.ddudu.user.repository;

import com.ddudu.user.domain.User;
import com.ddudu.user.domain.UserSearchType;
import java.util.List;

public interface UserRepositoryCustom {

  List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType);

}
