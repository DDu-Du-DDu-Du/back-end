package com.ddudu.user.repository;

import com.ddudu.user.domain.User;
import java.util.List;

public interface UserRepositoryCustom {

  List<User> findAllByKeyword(String keyword);

}
