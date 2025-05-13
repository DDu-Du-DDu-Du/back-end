package com.ddudu.domain.user.user.aggregate.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class FollowingStatusTest {

  @Nested
  class 수정_가능_여부_테스트 {

    @Test
    void FOLLOWING_상태는_수정_가능하다() {
      // when
      boolean isModifiable = FollowingStatus.FOLLOWING.isModifiable();

      // then
      Assertions.assertThat(isModifiable)
          .isTrue();
    }

    @Test
    void IGNORED_상태는_수정_가능하다() {
      // when
      boolean isModifiable = FollowingStatus.IGNORED.isModifiable();

      // then
      Assertions.assertThat(isModifiable)
          .isTrue();
    }

    @Test
    void REQUESTED_상태는_수정_불가능하다() {
      // when
      boolean isModifiable = FollowingStatus.REQUESTED.isModifiable();

      // then
      Assertions.assertThat(isModifiable)
          .isFalse();
    }

    @ParameterizedTest
    @EnumSource(
        value = FollowingStatus.class,
        names = {"FOLLOWING", "IGNORED"}
    )
    void REQUESTED가_아닌_상태는_수정_가능하다(FollowingStatus status) {
      // when
      boolean isModifiable = status.isModifiable();

      // then
      Assertions.assertThat(isModifiable)
          .isTrue();
    }

    @ParameterizedTest
    @EnumSource(
        value = FollowingStatus.class,
        names = {"REQUESTED"}
    )
    void REQUESTED_상태는_수정_불가능하다_파라미터화(FollowingStatus status) {
      // when
      boolean isModifiable = status.isModifiable();

      // then
      Assertions.assertThat(isModifiable)
          .isFalse();
    }

  }

}