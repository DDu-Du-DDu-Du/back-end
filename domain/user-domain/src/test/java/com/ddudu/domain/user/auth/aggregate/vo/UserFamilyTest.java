package com.ddudu.domain.user.auth.aggregate.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.domain.user.auth.aggregate.vo.UserFamily.UserFamilyBuilder;
import com.ddudu.common.exception.AuthErrorCode;
import com.ddudu.fixture.UserFixture;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserFamilyTest {

  @Nested
  class 생성_테스트 {

    Long userId;
    int family;

    @BeforeEach
    void setUp() {
      userId = UserFixture.getRandomId();
      family = UserFixture.getRandomPositive();
    }

    @Test
    void 아이디와_패밀리_번호로_유저_패밀리_생성을_성공한다() {
      // given

      // when
      UserFamily actual = UserFamily.builder()
          .userId(userId)
          .family(family)
          .build();

      // then
      assertThat(actual.getUserId()).isEqualTo(userId);
      assertThat(actual.getFamily()).isEqualTo(family);
    }

    @Test
    void 아이디가_null이면_유저_패밀리_생성을_실패한다() {
      // given
      UserFamilyBuilder builder = UserFamily.builder()
          .family(family);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AuthErrorCode.INVALID_USER_ID_FOR_REFRESH_TOKEN.getCodeName());
    }

    @Test
    void 문자열_형식으로_유저_패밀리_생성을_성공한다() {
      // given
      String userFamilyValue = userId + "-" + family;

      // when
      UserFamily actual = UserFamily.builderWithString()
          .userFamilyValue(userFamilyValue)
          .buildWithString();

      // then
      assertThat(actual.getUserId()).isEqualTo(userId);
      assertThat(actual.getFamily()).isEqualTo(family);
    }

    @Test
    void 문자열_형식이_잘못되면_유저_패밀리_생성을_실패한다() {
      // given
      String userFamilyValue = userId + " " + family;

      // when
      ThrowingCallable create = () -> UserFamily.builderWithString()
          .userFamilyValue(userFamilyValue)
          .buildWithString();

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AuthErrorCode.UNABLE_TO_PARSE_USER_FAMILY_VALUE.getCodeName());
    }

    @Test
    void 문자열의_아이디가_잘못되면_유저_패밀리_생성을_실패한다() {
      // given
      String userFamilyValue = "userId-" + family;

      // when
      ThrowingCallable create = () -> UserFamily.builderWithString()
          .userFamilyValue(userFamilyValue)
          .buildWithString();

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AuthErrorCode.INVALID_USER_ID_FOR_REFRESH_TOKEN.getCodeName());
    }

    @Test
    void 문자열의_패밀리가_잘못되면_유저_패밀리_생성을_실패한다() {
      // given
      String userFamilyValue = userId + "-family";

      // when
      ThrowingCallable create = () -> UserFamily.builderWithString()
          .userFamilyValue(userFamilyValue)
          .buildWithString();

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AuthErrorCode.INVALID_REFRESH_TOKEN_FAMILY.getCodeName());
    }

  }

}