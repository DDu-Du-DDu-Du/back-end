package com.ddudu.domain.user.user.aggregate.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class OptionSubVoTest {

  @Test
  void DisplayOptions의_기본값을_생성한다() {
    // given

    // when
    DisplayOptions actual = DisplayOptions.builder().build();

    // then
    assertThat(actual.getWeekStartDay()).isEqualTo(WeekStartDay.SUN);
    assertThat(actual.isDarkMode()).isFalse();
  }

  @Test
  void MenuActivationOptions의_기본값을_생성한다() {
    // given

    // when
    MenuActivationOptions actual = MenuActivationOptions.builder().build();

    // then
    assertThat(actual.getCalendar().isActive()).isTrue();
    assertThat(actual.getCalendar().getPriority()).isEqualTo(1);
    assertThat(actual.getDashboard().isActive()).isTrue();
    assertThat(actual.getDashboard().getPriority()).isEqualTo(2);
    assertThat(actual.getStats().isActive()).isTrue();
    assertThat(actual.getStats().getPriority()).isEqualTo(3);
  }

  @Test
  void AppConnectionOptions의_기본값을_생성한다() {
    // given

    // when
    AppConnectionOptions actual = AppConnectionOptions.builder().build();

    // then
    assertThat(actual.getRealtimeSync().isNotion()).isFalse();
    assertThat(actual.getRealtimeSync().isGoogleCalendar()).isFalse();
    assertThat(actual.getRealtimeSync().isMicrosoftTodo()).isFalse();
  }

}
