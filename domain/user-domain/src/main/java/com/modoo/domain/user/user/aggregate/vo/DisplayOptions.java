package com.modoo.domain.user.user.aggregate.vo;

import com.modoo.domain.user.user.aggregate.enums.Language;
import com.modoo.domain.user.user.aggregate.enums.TimeFormat;
import com.modoo.domain.user.user.aggregate.enums.WeekStartDay;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DisplayOptions {

  private final WeekStartDay weekStartDay;
  private final boolean darkMode;
  private final Language language;
  private final TimeFormat timeFormat;

  @Builder
  private DisplayOptions(
      WeekStartDay weekStartDay,
      Boolean darkMode,
      Language language,
      TimeFormat timeFormat
  ) {
    this.weekStartDay = Objects.isNull(weekStartDay) ? WeekStartDay.SUN : weekStartDay;
    this.darkMode = Objects.nonNull(darkMode) && darkMode;
    this.language = Objects.isNull(language) ? Language.EN : language;
    this.timeFormat = Objects.isNull(timeFormat) ? TimeFormat.TWELVE_HOUR : timeFormat;
  }

}
