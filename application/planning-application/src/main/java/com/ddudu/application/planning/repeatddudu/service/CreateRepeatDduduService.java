package com.ddudu.application.planning.repeatddudu.service;

import com.ddudu.application.common.dto.repeatddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeatddudu.in.CreateRepeatDduduUseCase;
import com.ddudu.application.common.port.repeatddudu.out.SaveRepeatDduduPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.RepeatDduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.service.RepeatDduduDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateRepeatDduduService implements CreateRepeatDduduUseCase {

  private final RepeatDduduDomainService repeatDduduDomainService;
  private final SaveRepeatDduduPort saveRepeatDduduPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveDduduPort saveDduduPort;

  @Override
  public Long create(Long loginId, CreateRepeatDduduRequest request) {
    // 1. 목표 조회 및 검증
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(),
        RepeatDduduErrorCode.INVALID_GOAL.getCodeName()
    );

    // 2. 목표 소유자의 요청인지 확인
    goal.validateGoalCreator(loginId);

    // 3. 종료되지 않은 목표인지 확인
    validateGoalNotDone(goal);

    // 4. 반복 뚜두 생성 후 저장
    RepeatDdudu repeatDdudu = saveRepeatDduduPort.save(repeatDduduDomainService.create(
        goal.getId(),
        request.toCommand()
    ));

    // 5. (반복되는) 뚜두 생성 후 저장
    saveDduduPort.saveAll(repeatDduduDomainService.createRepeatedDdudus(loginId, repeatDdudu));

    return repeatDdudu.getId();
  }

  private void validateGoalNotDone(Goal goal) {
    if (goal.isDone()) {
      throw new IllegalArgumentException(RepeatDduduErrorCode.GOAL_ALREADY_DONE.getCodeName());
    }
  }

}
