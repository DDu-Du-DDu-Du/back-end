package com.ddudu.domain.user.user.aggregate.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
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
          .display(DisplayOptions.builder()
              .weekStartDay(WeekStartDay.MON)
              .darkMode(true)
              .build())
          .menuActivation(MenuActivationOptions.builder()
              .calendar(MenuActivationItem.builder()
                  .active(false)
                  .priority(4)
                  .build())
              .dashboard(MenuActivationItem.builder()
                  .active(true)
                  .priority(5)
                  .build())
              .stats(MenuActivationItem.builder()
                  .active(false)
                  .priority(6)
                  .build())
              .build())
          .appConnection(AppConnectionOptions.builder()
              .realtimeSync(RealtimeSyncOptions.builder()
                  .notion(true)
                  .googleCalendar(true)
                  .microsoftTodo(true)
                  .build())
              .build())
          .build();

      // then
      assertThat(actual.isAllowingFollowsAfterApproval()).isTrue();
      assertThat(actual.isTemplateNotification()).isFalse();
      assertThat(actual.isDduduNotification()).isFalse();
      assertThat(actual.getDisplay().getWeekStartDay()).isEqualTo(WeekStartDay.MON);
      assertThat(actual.getDisplay().isDarkMode()).isTrue();
      assertThat(actual.getMenuActivation().getCalendar().isActive()).isFalse();
      assertThat(actual.getMenuActivation().getCalendar().getPriority()).isEqualTo(4);
      assertThat(actual.getAppConnection().getRealtimeSync().isNotion()).isTrue();
      assertThat(actual.getAppConnection().getRealtimeSync().isGoogleCalendar()).isTrue();
      assertThat(actual.getAppConnection().getRealtimeSync().isMicrosoftTodo()).isTrue();
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
      assertThat(actual.getDisplay().getWeekStartDay()).isEqualTo(WeekStartDay.SUN);
      assertThat(actual.getDisplay().isDarkMode()).isFalse();
      assertThat(actual.getMenuActivation().getCalendar().isActive()).isTrue();
      assertThat(actual.getMenuActivation().getCalendar().getPriority()).isEqualTo(1);
      assertThat(actual.getMenuActivation().getDashboard().isActive()).isTrue();
      assertThat(actual.getMenuActivation().getDashboard().getPriority()).isEqualTo(2);
      assertThat(actual.getMenuActivation().getStats().isActive()).isTrue();
      assertThat(actual.getMenuActivation().getStats().getPriority()).isEqualTo(3);
      assertThat(actual.getAppConnection().getRealtimeSync().isNotion()).isFalse();
      assertThat(actual.getAppConnection().getRealtimeSync().isGoogleCalendar()).isFalse();
      assertThat(actual.getAppConnection().getRealtimeSync().isMicrosoftTodo()).isFalse();
    }

  }

}
