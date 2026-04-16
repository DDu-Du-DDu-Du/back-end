package com.modoo.infra.mysql.user.auth.entiy;

import com.modoo.domain.user.auth.aggregate.RefreshToken;
import com.modoo.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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
      name = "current_token",
      nullable = false
  )
  private String currentToken;

  @Column(name = "previous_token")
  private String previousToken;

  @Column(name = "refreshed_at")
  private LocalDateTime refreshedAt;

  public static RefreshTokenEntity from(RefreshToken refreshToken) {
    return RefreshTokenEntity.builder()
        .id(refreshToken.getId())
        .currentToken(refreshToken.getCurrentToken())
        .previousToken(refreshToken.getPreviousToken())
        .refreshedAt(refreshToken.getRefreshedAt())
        .family(refreshToken.getFamily())
        .userId(refreshToken.getUserId())
        .build();
  }

  public RefreshToken toDomain() {
    return RefreshToken.builder()
        .id(id)
        .currentToken(currentToken)
        .previousToken(previousToken)
        .refreshedAt(refreshedAt)
        .family(family)
        .userId(userId)
        .build();
  }

}
