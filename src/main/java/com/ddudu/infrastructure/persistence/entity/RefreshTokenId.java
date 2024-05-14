package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.authentication.domain.vo.UserFamily;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class RefreshTokenId {

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

  public UserFamily toDomain() {
    return UserFamily.builder()
        .userFamilyValue(userId + " " + family)
        .build();
  }

}
