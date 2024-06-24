package com.ddudu.application.domain.period_goal.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Objects.nonNull;

import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.period_goal.domain.vo.PeriodGoalDate;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class PeriodGoal {

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long userId;
  private final String contents;
  private final PeriodGoalType type;
  @Getter(AccessLevel.NONE)
  private final PeriodGoalDate planDate;

  @Builder
  public PeriodGoal(
      Long id, Long userId, String contents, String type, LocalDate planDate
  ) {
    validate(userId, contents, type);

    this.id = id;
    this.userId = userId;
    this.contents = contents;
    this.type = PeriodGoalType.from(type);
    this.planDate = PeriodGoalDate.of(this.type, planDate);
  }

  public LocalDate getPlanDate() {
    return planDate.getDate();
  }

  private void validate(Long userId, String contents, String type) {
    validateUser(userId);
    validateContents(contents);
    validateType(type);
  }

  private void validateUser(Long userId) {
    checkArgument(nonNull(userId), PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  private void validateContents(String contents) {
    checkArgument(isNotBlank(contents), PeriodGoalErrorCode.CONTENTS_NOT_EXISTING.getCodeName());
  }

  private void validateType(String type) {
    checkArgument(nonNull(type), PeriodGoalErrorCode.PERIOD_GOAL_TYPE_NOT_EXISTING.getCodeName());
  }

}
