package com.modoo.application.notification.dailybriefing;

import com.modoo.application.common.dto.notification.DailyBriefingDto;
import com.modoo.application.common.dto.notification.response.DailyBriefingResponse;
import com.modoo.application.common.port.notification.in.BriefTodayPlanningUseCase;
import com.modoo.application.common.port.notification.out.DailyBriefingCommandPort;
import com.modoo.application.common.port.notification.out.DailyBriefingLoaderPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.DailyBriefingLogErrorCode;
import com.modoo.domain.notification.briefing.aggregate.DailyBriefingLog;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class BriefTodayPlanningService implements BriefTodayPlanningUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DailyBriefingLoaderPort dailyBriefingLoaderPort;
  private final DailyBriefingCommandPort dailyBriefingCommandPort;
  private final TodoLoaderPort todoLoaderPort;

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

    int count = todoLoaderPort.countTodayTodo(user.getId());
    DailyBriefingLog dailyBriefingLog = DailyBriefingLog.builder()
        .userId(user.getId())
        .build();

    dailyBriefingCommandPort.save(dailyBriefingLog);

    return DailyBriefingResponse.from(new DailyBriefingDto(count));
  }

}
