package com.ddudu.infra.mysql.user.user.repository;

import com.ddudu.infra.mysql.user.user.entity.AuthProviderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProviderEntity, Long> {

  Optional<AuthProviderEntity> findByProviderIdAndProviderType(
      String providerId,
      String providerType
  );

}
