package com.ddudu.aggregate;

import com.ddudu.aggregate.enums.DduduStatus;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BaseStats {

  private final Long dduduId;
  private final Long goalId;
  private final DduduStatus status;
  private final boolean isPostponed;
  private final LocalDate scheduledOn;

  public boolean isCompleted() {
    return status.isCompleted();
  }

}
