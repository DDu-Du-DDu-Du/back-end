package com.ddudu.infrastructure.persistence.repository.auth;

import com.ddudu.infrastructure.persistence.entity.AuthProviderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProviderEntity, Long> {

  Optional<AuthProviderEntity> findByProviderIdAndProviderType(
      String providerId, String providerType
  );

}
