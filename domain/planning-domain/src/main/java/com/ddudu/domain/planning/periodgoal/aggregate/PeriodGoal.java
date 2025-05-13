package com.ddudu.domain.planning.periodgoal.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.planning.periodgoal.aggregate.vo.PeriodGoalDate;
import java.time.LocalDate;
import java.util.Objects;
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
  private PeriodGoal(
      Long id,
      Long userId,
      String contents,
      PeriodGoalType type,
      LocalDate planDate
  ) {
    validate(userId, contents);

    this.id = id;
    this.userId = userId;
    this.contents = contents;
    this.type = type;
    this.planDate = PeriodGoalDate.of(this.type, planDate);
  }

  public LocalDate getPlanDate() {
    return planDate.getDate();
  }

  public PeriodGoal update(String contents) {
    return getFullBuilder()
        .contents(contents)
        .build();
  }

  public void validateCreator(Long userId) {
    if (!isCreatedBy(userId)) {
      throw new SecurityException(PeriodGoalErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  private void validate(Long userId, String contents) {
    validateUser(userId);
    validateContents(contents);
  }

  private void validateUser(Long userId) {
    checkArgument(nonNull(userId), PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  private void validateContents(String contents) {
    checkArgument(isNotBlank(contents), PeriodGoalErrorCode.CONTENTS_NOT_EXISTING.getCodeName());
  }

  private boolean isCreatedBy(Long userId) {
    return Objects.equals(this.userId, userId);
  }

  private PeriodGoalBuilder getFullBuilder() {
    return PeriodGoal.builder()
        .id(this.id)
        .userId(this.userId)
        .contents(this.contents)
        .type(this.type)
        .planDate(this.planDate.getDate());
  }

}
