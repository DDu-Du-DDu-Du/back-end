package com.ddudu.old.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.user.domain.vo.Options;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class OptionsTest {

  @Test
  void 옵션은_기본으로_꺼져_있는_상태로_생성된다() {
    Options options = new Options();

    assertThat(options.isAllowingFollowsAfterApproval()).isFalse();
  }

  @Test
  void 팔로잉_허락_후_팔로잉_추가_기능을_활성화한다() {
    // given
    Options options = new Options();

    // when
    options.switchOptions();

    // then
    assertThat(options.isAllowingFollowsAfterApproval()).isTrue();
  }

  @Test
  void 팔로잉_허락_후_팔로잉_추가_기능을_비활성화한다() {
    // given
    Options options = Options.builder()
        .allowingFollowsAfterApproval(true)
        .build();

    // when
    options.switchOptions();

    // then
    assertThat(options.isAllowingFollowsAfterApproval()).isFalse();
  }

}
