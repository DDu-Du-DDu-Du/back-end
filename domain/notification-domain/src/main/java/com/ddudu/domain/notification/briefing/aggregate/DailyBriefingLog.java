package com.ddudu.domain.notification.briefing.aggregate;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.common.exception.DailyBriefingLogErrorCode;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class DailyBriefingLog {

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long userId;
  private final LocalDate briefingDate;

  @Builder
  private DailyBriefingLog(Long id, Long userId, LocalDate briefingDate) {
    validate(userId);

    this.id = id;
    this.userId = userId;
    this.briefingDate = Objects.requireNonNullElse(briefingDate, LocalDate.now());
  }

  private void validate(Long userId) {
    checkArgument(
        Objects.nonNull(userId),
        DailyBriefingLogErrorCode.NULL_USER_ID.getCodeName()
    );
  }

}
