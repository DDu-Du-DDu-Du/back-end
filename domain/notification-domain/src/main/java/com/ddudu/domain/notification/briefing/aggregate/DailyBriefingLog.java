package com.ddudu.domain.notification.briefing.aggregate;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyBriefingLog {

  private Long id;
  private Long userId;
  private LocalDate briefingDate;

}
