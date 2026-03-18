package com.ddudu.application.planning.repeattodo.service;

import com.ddudu.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeattodo.in.CreateRepeatTodoUseCase;
import com.ddudu.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.RepeatTodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.service.RepeatTodoDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateRepeatTodoService implements CreateRepeatTodoUseCase {

  private final RepeatTodoDomainService repeatTodoDomainService;
  private final SaveRepeatTodoPort saveRepeatTodoPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveTodoPort saveTodoPort;

  @Override
  public Long create(Long loginId, CreateRepeatTodoRequest request) {
    // 1. 목표 조회 및 검증
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(),
        RepeatTodoErrorCode.INVALID_GOAL.getCodeName()
    );

    // 2. 목표 소유자의 요청인지 확인
    goal.validateGoalCreator(loginId);

    // 3. 종료되지 않은 목표인지 확인
    validateGoalNotDone(goal);

    // 4. 반복 뚜두 생성 후 저장
    RepeatTodo repeatTodo = saveRepeatTodoPort.save(repeatTodoDomainService.create(
        goal.getId(),
        request.toCommand()
    ));

    // 5. (반복되는) 뚜두 생성 후 저장
    saveTodoPort.saveAll(repeatTodoDomainService.createRepeatedTodos(loginId, repeatTodo));

    return repeatTodo.getId();
  }

  private void validateGoalNotDone(Goal goal) {
    if (goal.isDone()) {
      throw new IllegalArgumentException(RepeatTodoErrorCode.GOAL_ALREADY_DONE.getCodeName());
    }
  }

}
