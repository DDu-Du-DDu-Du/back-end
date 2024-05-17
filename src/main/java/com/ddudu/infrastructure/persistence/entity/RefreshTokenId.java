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

  public static RefreshTokenId from(String userFamilyValue) {
    String[] userFamily = userFamilyValue.split("-");

    return RefreshTokenId.builder()
        .userId(Long.parseLong(userFamily[0]))
        .family(Integer.parseInt(userFamily[1]))
        .build();
  }

  public UserFamily toDomain() {
    return UserFamily.builder()
        .userId(userId)
        .family(family)
        .build();
  }

}
