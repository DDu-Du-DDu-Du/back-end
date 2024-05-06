package com.ddudu.old.user.domain;

import com.ddudu.old.user.dto.FollowingSearchType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

  boolean existsByOptionalUsername(String optionalUsername);

  boolean existsByEmail(String email);

  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);

  List<User> findFromFollowingBySearchType(User user, FollowingSearchType searchType);

  List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType searchType);

  void update(User user);

  void delete(User user);

}
