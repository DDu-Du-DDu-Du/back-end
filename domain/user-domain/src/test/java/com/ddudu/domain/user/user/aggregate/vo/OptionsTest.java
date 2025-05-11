package com.ddudu.domain.user.user.aggregate.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class OptionsTest {

  @Nested
  class 옵션_생성_테스트 {

    @Test
    void 옵션을_생성한다() {
      // given
      boolean allowFollowsAfterApproval = true;
      boolean templateNotification = false;
      boolean dduduNotification = false;

      // when
      Options actual = Options.builder()
          .allowingFollowsAfterApproval(allowFollowsAfterApproval)
          .templateNotification(templateNotification)
          .dduduNotification(dduduNotification)
          .build();

      // then
      assertThat(actual.isAllowingFollowsAfterApproval()).isTrue();
      assertThat(actual.isTemplateNotification()).isFalse();
      assertThat(actual.isDduduNotification()).isFalse();
    }

    @Test
    void 입력이_없으면_기본_값이_적용된다() {
      // given

      // when
      Options actual = Options.builder()
          .build();

      // then
      assertThat(actual.isAllowingFollowsAfterApproval()).isFalse();
      assertThat(actual.isTemplateNotification()).isTrue();
      assertThat(actual.isDduduNotification()).isTrue();
    }

  }

}