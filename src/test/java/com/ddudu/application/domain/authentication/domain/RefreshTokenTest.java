package com.ddudu.application.domain.authentication.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.authentication.domain.vo.UserFamily;
import com.ddudu.fixture.UserFixture;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RefreshTokenTest {

  @Nested
  class 생성_테스트 {

    Long userId;
    int family;
    String tokenValue;

    @BeforeEach
    void setUp() {
      userId = UserFixture.getRandomId();
      family = UserFixture.getRandomPositive();
      tokenValue = UUID.randomUUID()
          .toString();
    }

    @Test
    void 유저_아이디와_패밀리로_생성한다() {
      // given

      // when
      RefreshToken refreshToken = RefreshToken.builder()
          .userId(userId)
          .family(family)
          .tokenValue(tokenValue)
          .build();

      // then
      assertThat(refreshToken.getUserId()).isEqualTo(userId);
      assertThat(refreshToken.getFamily()).isEqualTo(family);
    }

    @Test
    void 유저_패밀리_객체로_생성한다() {
      // given
      UserFamily userFamily = UserFamily.builder()
          .userId(userId)
          .family(family)
          .build();

      // when
      RefreshToken refreshToken = RefreshToken.builder()
          .userFamily(userFamily)
          .tokenValue(tokenValue)
          .build();

      // then
      assertThat(refreshToken.getUserId()).isEqualTo(userId);
      assertThat(refreshToken.getFamily()).isEqualTo(family);
    }

    @Test
    void 유저_패밀리와_아이디랑_패밀리가_동시에_존재하면_유저_패밀리로_생성한다() {
      // given
      UserFamily userFamily = UserFamily.builder()
          .userId(userId + 10)
          .family(family)
          .build();

      // when
      RefreshToken refreshToken = RefreshToken.builder()
          .userId(userId)
          .family(family)
          .userFamily(userFamily)
          .tokenValue(tokenValue)
          .build();

      // then
      assertThat(refreshToken.getUserId()).isEqualTo(userFamily.getUserId());
    }

  }

}