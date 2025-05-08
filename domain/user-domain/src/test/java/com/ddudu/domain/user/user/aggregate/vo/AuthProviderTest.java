package com.ddudu.domain.user.user.aggregate.vo;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.domain.user.user.aggregate.enums.ProviderType;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider.AuthProviderBuilder;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.fixture.UserFixture;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthProviderTest {

  @Nested
  class 생성_테스트 {

    String providerType;
    String providerId;

    @BeforeEach
    void setUp() {
      providerType = ProviderType.KAKAO.name();
      providerId = String.valueOf(UserFixture.getRandomId());
    }

    @Test
    void 공급자_객체_생성을_성공한다() {
      // given
      AuthProviderBuilder builder = AuthProvider.builder()
          .providerType(providerType)
          .providerId(providerId);

      // when
      ThrowingCallable build = builder::build;

      // then
      assertThatNoException().isThrownBy(build);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 공급자_아이디가_없으면_생성을_실패한다(String blankId) {
      // given
      AuthProviderBuilder builder = AuthProvider.builder()
          .providerType(providerType)
          .providerId(blankId);

      // when
      ThrowingCallable build = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(build)
          .withMessage(UserErrorCode.BLANK_PROVIDER_ID.getCodeName());
    }

  }

}