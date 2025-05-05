package com.ddudu.domain.planning.repeatddudu.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeatddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.planning.repeatddudu.dto.RepeatPatternDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class RepeatDdudu {

  private static final int MAX_NAME_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long goalId;
  private final String name;
  private final RepeatType repeatType;
  private final RepeatPattern repeatPattern;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalTime beginAt;
  private final LocalTime endAt;

  @Builder
  private RepeatDdudu(
      Long id, Long goalId, String name, RepeatType repeatType, RepeatPattern repeatPattern,
      LocalDate startDate, LocalDate endDate, LocalTime beginAt, LocalTime endAt
  ) {
    validate(goalId, name, repeatType, startDate, endDate, beginAt, endAt);

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
      Long goalId, String name, RepeatType repeatType, LocalDate startDate, LocalDate endDate,
      LocalTime beginAt, LocalTime endAt
  ) {
    checkArgument(nonNull(goalId), RepeatDduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    checkArgument(nonNull(repeatType), RepeatDduduErrorCode.NULL_REPEAT_TYPE.getCodeName());
    validateName(name);
    validatePeriodOfRepeat(startDate, endDate);
    validatePeriodOfTime(beginAt, endAt);
  }

  private void validateName(String name) {
    checkArgument(StringUtils.isNotBlank(name), RepeatDduduErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH,
        RepeatDduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName()
    );
  }

  private void validatePeriodOfRepeat(LocalDate startDate, LocalDate endDate) {
    checkArgument(nonNull(startDate), RepeatDduduErrorCode.NULL_START_DATE.getCodeName());
    checkArgument(nonNull(endDate), RepeatDduduErrorCode.NULL_END_DATE.getCodeName());
    checkArgument(
        endDate.isAfter(startDate), RepeatDduduErrorCode.UNABLE_TO_END_BEFORE_START.getCodeName());
  }

  private void validatePeriodOfTime(LocalTime beginAt, LocalTime endAt) {
    if (isNull(beginAt) && isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt),
        RepeatDduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName()
    );
  }

  public RepeatDdudu update(
      String name, RepeatType repeatType, RepeatPatternDto repeatPatternDto, LocalDate startDate,
      LocalDate endDate, LocalTime beginAt, LocalTime endAt
  ) {
    return RepeatDdudu.builder()
        .id(id)
        .goalId(goalId)
        .name(name)
        .repeatType(repeatType)
        .repeatPatternDto(repeatPatternDto)
        .startDate(startDate)
        .endDate(endDate)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
