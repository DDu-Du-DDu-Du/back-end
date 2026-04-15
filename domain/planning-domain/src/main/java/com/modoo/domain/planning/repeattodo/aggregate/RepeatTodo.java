package com.modoo.domain.planning.repeattodo.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.modoo.common.exception.RepeatTodoErrorCode;
import com.modoo.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatInfo;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class RepeatTodo {

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
  private RepeatTodo(
      Long id,
      Long goalId,
      String name,
      RepeatType repeatType,
      RepeatPattern repeatPattern,
      LocalDate startDate,
      LocalDate endDate,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    validate(goalId, repeatType, name, startDate, endDate, beginAt, endAt);

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

  public RepeatInfo getRepeatInfo() {
    return repeatPattern.getInfo();
  }

  private void validate(
      Long goalId,
      RepeatType repeatType,
      String name,
      LocalDate startDate,
      LocalDate endDate,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    checkArgument(nonNull(repeatType), RepeatTodoErrorCode.NULL_REPEAT_TYPE.getCodeName());
    checkArgument(nonNull(goalId), RepeatTodoErrorCode.NULL_GOAL_VALUE.getCodeName());
    validateName(name);
    validatePeriodOfRepeat(startDate, endDate);
    validatePeriodOfTime(beginAt, endAt);
  }

  private void validateName(String name) {
    checkArgument(StringUtils.isNotBlank(name), RepeatTodoErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH,
        RepeatTodoErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName()
    );
  }

  private void validatePeriodOfRepeat(LocalDate startDate, LocalDate endDate) {
    checkArgument(nonNull(startDate), RepeatTodoErrorCode.NULL_START_DATE.getCodeName());
    checkArgument(nonNull(endDate), RepeatTodoErrorCode.NULL_END_DATE.getCodeName());
    checkArgument(
        endDate.isAfter(startDate),
        RepeatTodoErrorCode.UNABLE_TO_END_BEFORE_START.getCodeName()
    );
  }

  private void validatePeriodOfTime(LocalTime beginAt, LocalTime endAt) {
    if (isNull(beginAt) && isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt),
        RepeatTodoErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName()
    );
  }

  public RepeatTodo update(
      String name,
      RepeatType repeatType,
      RepeatPattern repeatPattern,
      LocalDate startDate,
      LocalDate endDate,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    return RepeatTodo.builder()
        .id(id)
        .goalId(goalId)
        .name(name)
        .repeatType(repeatType)
        .repeatPattern(repeatPattern)
        .startDate(startDate)
        .endDate(endDate)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
