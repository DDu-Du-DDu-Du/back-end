package com.modoo.domain.user.auth.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.domain.user.auth.aggregate.vo.UserFamily;
import com.modoo.fixture.UserFixture;
import java.time.LocalDateTime;
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
      tokenValue = UUID.randomUUID().toString();
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
      assertThat(refreshToken.getCurrentToken()).isEqualTo(tokenValue);
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

  @Nested
  class 상태_비교_테스트 {

    RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
      // given
      refreshToken = RefreshToken.builder()
          .userId(UserFixture.getRandomId())
          .family(UserFixture.getRandomPositive())
          .currentToken("current-token")
          .previousToken("previous-token")
          .refreshedAt(LocalDateTime.now())
          .build();

      // when

      // then
    }

    @Test
    void 현재_토큰이_같으면_true를_반환한다() {
      // given

      // when
      boolean actual = refreshToken.hasSameCurrentToken("current-token");

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 이전_토큰이_같으면_true를_반환한다() {
      // given

      // when
      boolean actual = refreshToken.hasSamePreviousToken("previous-token");

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 갱신_시각이_삼분_이내면_true를_반환한다() {
      // given

      // when
      boolean actual = refreshToken.isWithinGracePeriod(LocalDateTime.now());

      // then
      assertThat(actual).isTrue();
    }
  }

}
