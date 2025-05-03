package com.ddudu.infrastructure.usermysql.user.entity;

import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.infrastructure.commonmysql.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_providers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class AuthProviderEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(
      name = "provider_type",
      length = 50,
      nullable = false
  )
  private String providerType;

  @Column(
      name = "provider_id",
      length = 100
  )
  private String providerId;

  public static AuthProviderEntity from(AuthProvider authProvider, Long userId) {
    return AuthProviderEntity.builder()
        .userId(userId)
        .providerId(authProvider.getProviderId())
        .providerType(authProvider.getProviderType())
        .build();
  }

  public AuthProvider toDomain() {
    return AuthProvider.builder()
        .providerId(providerId)
        .providerType(providerType)
        .build();
  }

}
