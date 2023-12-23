package com.ddudu.user.repository;

import com.ddudu.user.domain.Email;
import com.ddudu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(Email email);

  boolean existsByOptionalUsername(String existsByOptionalUsername);

}
