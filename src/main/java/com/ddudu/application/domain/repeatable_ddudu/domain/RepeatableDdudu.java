package com.ddudu.application.domain.repeatable_ddudu.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.ddudu.application.domain.repeatable_ddudu.domain.enums.RepeatType;
import com.ddudu.application.domain.repeatable_ddudu.exception.RepeatableDduduErrorCode;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RepeatableDdudu {

  private static final int MAX_NAME_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long goalId;
  private final String name;
  private final RepeatType repeatType;
  @Getter(AccessLevel.NONE)
  private final RepeatPattern repeatPattern;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalTime beginAt;
  private final LocalTime endAt;

  @Builder
  public RepeatableDdudu(
      Long id, Long goalId, String name, RepeatType repeatType, RepeatPattern repeatPattern,
      LocalDate startDate, LocalDate endDate, LocalTime beginAt, LocalTime endAt
  ) {
    validate(goalId, name, startDate, endDate, beginAt, endAt);

    this.id = id;
    this.goalId = goalId;
    this.name = name;
    this.repeatType = repeatType;
    this.repeatPattern = repeatPattern;
    this.startDate = startDate;
    this.endDate = endDate;
    this.beginAt = beginAt;
    this.endAt = endAt;
  }

  public List<LocalDate> getRepeatDates() {
    return repeatPattern.calculateRepeatDates(startDate, endDate);
  }

  private void validate(
      Long goalId, String name, LocalDate startDate, LocalDate endDate, LocalTime beginAt,
      LocalTime endAt
  ) {
    checkArgument(nonNull(goalId), RepeatableDduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    validateName(name);
    validatePeriodOfRepeat(startDate, endDate);
    validatePeriodOfTime(beginAt, endAt);
  }

  private void validateName(String name) {
    checkArgument(StringUtils.isNotBlank(name), RepeatableDduduErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH,
        RepeatableDduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName()
    );
  }

  private void validatePeriodOfRepeat(LocalDate startDate, LocalDate endDate) {
    checkArgument(nonNull(startDate), RepeatableDduduErrorCode.NULL_START_DATE.getCodeName());
    checkArgument(nonNull(endDate), RepeatableDduduErrorCode.NULL_END_DATE.getCodeName());
    checkArgument(
        startDate.isAfter(endDate),
        RepeatableDduduErrorCode.UNABLE_TO_END_BEFORE_START.getCodeName()
    );
  }

  private void validatePeriodOfTime(LocalTime beginAt, LocalTime endAt) {
    if (Objects.isNull(beginAt) || Objects.isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt),
        RepeatableDduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName()
    );
  }

}
