package com.ddudu.infra.mysql.notification.briefing.adapter;

import com.ddudu.application.common.port.notification.out.DailyBriefingCommandPort;
import com.ddudu.application.common.port.notification.out.DailyBriefingLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.briefing.aggregate.DailyBriefingLog;
import com.ddudu.infra.mysql.notification.briefing.entity.DailyBriefingLogEntity;
import com.ddudu.infra.mysql.notification.briefing.repository.DailyBriefingRepository;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class DailyBriefingAdapter implements DailyBriefingLoaderPort, DailyBriefingCommandPort {

  private final DailyBriefingRepository dailyBriefingRepository;

  @Override
  public DailyBriefingLog save(DailyBriefingLog dailyBriefingLog) {
    return dailyBriefingRepository.save(DailyBriefingLogEntity.from(dailyBriefingLog))
        .toDomain();
  }

  @Override
  public boolean existsByUser(Long userId) {
    return dailyBriefingRepository.existsByUserId(userId);
  }

}
