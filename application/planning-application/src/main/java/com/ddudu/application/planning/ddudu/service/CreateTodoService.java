package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.CreateTodoRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.port.ddudu.in.CreateTodoUseCase;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.service.TodoDomainService;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateTodoService implements CreateTodoUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveTodoPort saveTodoPort;
  private final TodoDomainService dduduDomainService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public BasicTodoResponse create(Long loginId, CreateTodoRequest request) {
    // 1. 요청 유저, 목표 조회
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(), TodoErrorCode.GOAL_NOT_EXISTING.getCodeName());

    // 2. 목표 소유자의 요청인지 확인
    goal.validateGoalCreator(loginId);

    // 3. 종료되지 않은 목표인지 확인
    validateGoalNotDone(goal);

    // 4. 뚜두 생성 후 저장
    Todo ddudu = dduduDomainService.create(user.getId(), request.toCommand());
    Todo saved = saveTodoPort.save(ddudu);

    if (saved.hasReminder()) {
      InterimSetReminderEvent event = InterimSetReminderEvent.from(user.getId(), saved);
      applicationEventPublisher.publishEvent(event);
    }

    return BasicTodoResponse.from(saved);
  }

  private void validateGoalNotDone(Goal goal) {
    if (goal.isDone()) {
      throw new IllegalArgumentException(TodoErrorCode.GOAL_ALREADY_DONE.getCodeName());
    }
  }

}
