package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.old.common.BaseEntity;
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
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class RefreshTokenEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(
      name = "family",
      nullable = false
  )
  private int family;

  @Column(
      name = "token_value",
      nullable = false
  )
  private String tokenValue;

  public static RefreshTokenEntity from(RefreshToken refreshToken) {
    return RefreshTokenEntity.builder()
        .id(refreshToken.getId())
        .tokenValue(refreshToken.getTokenValue())
        .family(refreshToken.getFamily())
        .userId(refreshToken.getUserId())
        .build();
  }

  public RefreshToken toDomain() {
    return RefreshToken.builder()
        .id(id)
        .tokenValue(tokenValue)
        .family(family)
        .userId(userId)
        .build();
  }

}
