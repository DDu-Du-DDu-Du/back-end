package com.ddudu.aggregate;

import com.ddudu.aggregate.enums.DduduStatus;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
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
  private final LocalTime beginAt;
  private final LocalTime endAt;

  public boolean isCompleted() {
    return status.isCompleted();
  }

  public boolean isUnderSameGoal(Long goalId) {
    return this.goalId.equals(goalId);
  }

  public long getTimePart() {
    if (Objects.isNull(beginAt) || Objects.isNull(endAt)) {
      return 0L;
    }

    long fromBeginToNoon = Duration.between(beginAt, LocalTime.NOON)
        .toSeconds();
    long fromNoonToEnd = Duration.between(LocalTime.NOON, endAt)
        .toSeconds();

    return fromNoonToEnd - fromBeginToNoon;
  }

  public DayOfWeek getDayOfWeek() {
    return scheduledOn.getDayOfWeek();
  }

}
