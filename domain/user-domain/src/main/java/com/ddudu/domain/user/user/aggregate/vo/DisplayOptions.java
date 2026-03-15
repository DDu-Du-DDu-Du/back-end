package com.ddudu.domain.user.user.aggregate.vo;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DisplayOptions {

  private final WeekStartDay weekStartDay;
  private final boolean darkMode;

  @Builder
  private DisplayOptions(WeekStartDay weekStartDay, Boolean darkMode) {
    this.weekStartDay = Objects.requireNonNullElse(weekStartDay, WeekStartDay.SUN);
    this.darkMode = Objects.nonNull(darkMode) && darkMode;
  }

}
