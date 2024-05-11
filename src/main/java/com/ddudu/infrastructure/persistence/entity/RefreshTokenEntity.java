package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.old.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
  @Column(name = "token_value")
  private String tokenValue;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(
      name = "family",
      nullable = false
  )
  private Integer family;

  public static RefreshTokenEntity from(RefreshToken refreshToken) {
    return RefreshTokenEntity.builder()
        .tokenValue(refreshToken.getTokenValue())
        .userId(refreshToken.getUserId())
        .family(refreshToken.getFamily())
        .build();
  }

  public RefreshToken toDomain() {
    return RefreshToken.builder()
        .tokenValue(tokenValue)
        .userId(userId)
        .family(family)
        .build();
  }

}
