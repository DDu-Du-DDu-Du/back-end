package com.ddudu.aggregate;

import com.ddudu.aggregate.enums.DduduStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseStats {

  @EqualsAndHashCode.Include
  private final Long dduduId;
  private final Long goalId;
  private final String goalName;
  private final DduduStatus status;
  private final boolean isPostponed;
  private final LocalDate scheduledOn;

  public boolean isCompleted() {
    return status.isCompleted();
  }

  public boolean isUnderSameGoal(Long goalId) {
    return this.goalId.equals(goalId);
  }

}
