package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.old.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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

  @EmbeddedId
  private RefreshTokenId refreshTokenId;

  @Column(
      name = "token_value",
      nullable = false
  )
  private String tokenValue;

  public static RefreshTokenEntity from(RefreshToken refreshToken) {
    return RefreshTokenEntity.builder()
        .tokenValue(refreshToken.getTokenValue())
        .refreshTokenId(RefreshTokenId.builder()
            .userId(refreshToken.getUserId())
            .family(refreshToken.getFamily())
            .build())
        .build();
  }

  public RefreshToken toDomain() {
    return RefreshToken.builder()
        .tokenValue(tokenValue)
        .userFamily(refreshTokenId.toDomain())
        .build();
  }

  public int getFamily() {
    return refreshTokenId.getFamily();
  }

}
