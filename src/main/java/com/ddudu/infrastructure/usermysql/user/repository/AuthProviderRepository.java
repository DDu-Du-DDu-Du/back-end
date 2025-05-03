package com.ddudu.infrastructure.usermysql.user.repository;

import com.ddudu.infrastructure.usermysql.user.entity.AuthProviderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProviderEntity, Long> {

  Optional<AuthProviderEntity> findByProviderIdAndProviderType(
      String providerId, String providerType
  );

}
