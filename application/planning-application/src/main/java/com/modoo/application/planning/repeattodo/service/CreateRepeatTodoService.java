package com.modoo.application.planning.repeattodo.service;

import com.modoo.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.repeattodo.in.CreateRepeatTodoUseCase;
import com.modoo.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.RepeatTodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.repeattodo.service.RepeatTodoDomainService;
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

    // 4. 반복 투두 생성 후 저장
    RepeatTodo repeatTodo = saveRepeatTodoPort.save(repeatTodoDomainService.create(
        goal.getId(),
        request.toCommand()
    ));

    // 5. (반복되는) 투두 생성 후 저장
    saveTodoPort.saveAll(repeatTodoDomainService.createRepeatedTodos(loginId, repeatTodo));

    return repeatTodo.getId();
  }

  private void validateGoalNotDone(Goal goal) {
    if (goal.isDone()) {
      throw new IllegalArgumentException(RepeatTodoErrorCode.GOAL_ALREADY_DONE.getCodeName());
    }
  }

}
