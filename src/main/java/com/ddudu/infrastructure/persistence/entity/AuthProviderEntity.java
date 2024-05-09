package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class AuthProviderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false
  )
  private UserEntity user;

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

  public static AuthProviderEntity from(AuthProvider authProvider, UserEntity user) {
    return AuthProviderEntity.builder()
        .user(user)
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
