package com.ddudu.user.repository;

import com.ddudu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsByOptionalUsername(String existsByOptionalUsername);

}
