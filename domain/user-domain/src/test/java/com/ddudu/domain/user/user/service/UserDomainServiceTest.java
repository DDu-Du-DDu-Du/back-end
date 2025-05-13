package com.ddudu.domain.user.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.fixture.UserFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserDomainServiceTest {

  static UserDomainService userDomainService;

  @BeforeAll
  static void setUp() {
    userDomainService = new UserDomainService();
  }

  @Nested
  class 회원_생성_테스트 {

    @Test
    void 가입_유저를_생성한다() {
      // given
      AuthProvider authProvider = UserFixture.createRandomAuthProvider();

      // when
      User firstUser = userDomainService.createFirstUser(authProvider);

      // then
      assertThat(firstUser.getAuthProviders()).containsOnly(authProvider);
    }

  }

}