package com.ddudu.user.repository;

import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

  boolean existsByEmail(Email email);

  boolean existsByOptionalUsername(String existsByOptionalUsername);

  Optional<User> findByEmail(Email email);

}
