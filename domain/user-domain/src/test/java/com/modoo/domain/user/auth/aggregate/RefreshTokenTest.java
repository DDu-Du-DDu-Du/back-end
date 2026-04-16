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
    void 현재_토큰이_null이면_tokenValue를_현재_토큰으로_사용한다() {
      // given

      // when
      RefreshToken refreshToken = RefreshToken.builder()
          .userId(userId)
          .family(family)
          .tokenValue(tokenValue)
          .currentToken(null)
          .build();

      // then
      assertThat(refreshToken.getCurrentToken()).isEqualTo(tokenValue);
      assertThat(refreshToken.getTokenValue()).isEqualTo(tokenValue);
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
    void 아이디가_같으면_true를_반환한다() {
      // given
      Long refreshTokenId = UserFixture.getRandomId();
      RefreshToken tokenWithId = RefreshToken.builder()
          .id(refreshTokenId)
          .userId(UserFixture.getRandomId())
          .family(UserFixture.getRandomPositive())
          .currentToken("current-token")
          .build();

      // when
      boolean actual = tokenWithId.hasSameId(refreshTokenId);

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void tokenValue_비교는_현재_토큰과_비교한다() {
      // given

      // when
      boolean actual = refreshToken.hasSameTokenValue("current-token");

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
    void 이전_토큰이_null이고_비교값도_null이면_true를_반환한다() {
      // given
      RefreshToken tokenWithoutPrevious = RefreshToken.builder()
          .userId(UserFixture.getRandomId())
          .family(UserFixture.getRandomPositive())
          .currentToken("current-token")
          .previousToken(null)
          .refreshedAt(LocalDateTime.now())
          .build();

      // when
      boolean actual = tokenWithoutPrevious.hasSamePreviousToken(null);

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

    @Test
    void 갱신_시각이_삼분_경계면_true를_반환한다() {
      // given
      LocalDateTime now = LocalDateTime.now();
      RefreshToken refreshedAtBoundary = RefreshToken.builder()
          .userId(UserFixture.getRandomId())
          .family(UserFixture.getRandomPositive())
          .currentToken("current-token")
          .refreshedAt(now.minusMinutes(3))
          .build();

      // when
      boolean actual = refreshedAtBoundary.isWithinGracePeriod(now);

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 갱신_시각이_삼분을_초과하면_false를_반환한다() {
      // given
      LocalDateTime now = LocalDateTime.now();
      RefreshToken refreshedBeforeGracePeriod = RefreshToken.builder()
          .userId(UserFixture.getRandomId())
          .family(UserFixture.getRandomPositive())
          .currentToken("current-token")
          .refreshedAt(now.minusMinutes(3).minusNanos(1))
          .build();

      // when
      boolean actual = refreshedBeforeGracePeriod.isWithinGracePeriod(now);

      // then
      assertThat(actual).isFalse();
    }

    @Test
    void 갱신_시각이_null이면_false를_반환한다() {
      // given
      RefreshToken tokenWithoutRefreshedAt = RefreshToken.builder()
          .userId(UserFixture.getRandomId())
          .family(UserFixture.getRandomPositive())
          .currentToken("current-token")
          .refreshedAt(null)
          .build();

      // when
      boolean actual = tokenWithoutRefreshedAt.isWithinGracePeriod(LocalDateTime.now());

      // then
      assertThat(actual).isFalse();
    }
  }

}
