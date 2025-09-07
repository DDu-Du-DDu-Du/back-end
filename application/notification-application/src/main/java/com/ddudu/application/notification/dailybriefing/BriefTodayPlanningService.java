package com.ddudu.application.notification.dailybriefing;

import com.ddudu.application.common.dto.notification.DailyBriefingDto;
import com.ddudu.application.common.dto.notification.response.DailyBriefingResponse;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.notification.in.BriefTodayPlanningUseCase;
import com.ddudu.application.common.port.notification.out.DailyBriefingCommandPort;
import com.ddudu.application.common.port.notification.out.DailyBriefingLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DailyBriefingLogErrorCode;
import com.ddudu.domain.notification.briefing.aggregate.DailyBriefingLog;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class BriefTodayPlanningService implements BriefTodayPlanningUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DailyBriefingLoaderPort dailyBriefingLoaderPort;
  private final DailyBriefingCommandPort dailyBriefingCommandPort;
  private final DduduLoaderPort dduduLoaderPort;

  @Override
  public DailyBriefingResponse getDailyBriefing(Long loginId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        DailyBriefingLogErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );

    boolean alreadyBriefed = dailyBriefingLoaderPort.existsByUser(user.getId());

    if (alreadyBriefed) {
      return DailyBriefingResponse.notFirst();
    }

    int count = dduduLoaderPort.countTodayDdudu(user.getId());
    DailyBriefingLog dailyBriefingLog = DailyBriefingLog.builder()
        .userId(user.getId())
        .build();

    dailyBriefingCommandPort.save(dailyBriefingLog);

    return DailyBriefingResponse.from(new DailyBriefingDto(count));
  }

}
