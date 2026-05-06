package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.interim.InterimSetReminderEvent;
import com.modoo.application.common.dto.todo.request.CreateTodoReminderRequest;
import com.modoo.application.common.dto.todo.request.CreateTodoRequest;
import com.modoo.application.common.dto.todo.response.BasicTodoResponse;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.reminder.out.ReminderCommandPort;
import com.modoo.application.common.port.todo.in.CreateTodoUseCase;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.service.TodoDomainService;
import com.modoo.domain.user.user.aggregate.User;
import java.util.List;
import java.util.Objects;
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
  private final ReminderCommandPort reminderCommandPort;
  private final TodoDomainService todoDomainService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public BasicTodoResponse create(Long loginId, CreateTodoRequest request) {
    // 1. 요청 유저 조회
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());

    // 2. 목표가 있는 요청이면 목표 검증
    validateGoalIfExists(loginId, request.goalId());

    // 3. 투두 생성 후 저장
    Todo todo = todoDomainService.create(user.getId(), request.toCommand());
    Todo saved = saveTodoPort.save(todo);

    createReminders(user.getId(), saved, request.reminders());

    return BasicTodoResponse.from(saved);
  }

  private void createReminders(Long userId, Todo todo, List<CreateTodoReminderRequest> requests) {
    if (!Objects.nonNull(requests)) {
      return;
    }

    requests.stream()
        .map(request -> Reminder.from(
            userId,
            todo.getId(),
            request.remindsAt(),
            todo.getScheduleDatetime()
        ))
        .map(reminderCommandPort::save)
        .forEach(savedReminder ->
            applicationEventPublisher.publishEvent(
                InterimSetReminderEvent.from(userId, savedReminder)
            )
        );
  }

  private void validateGoalIfExists(Long loginId, Long goalId) {
    if (Objects.isNull(goalId)) {
      return;
    }

    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        goalId, TodoErrorCode.GOAL_NOT_EXISTING.getCodeName());
    goal.validateGoalCreator(loginId);
    validateGoalNotDone(goal);
  }

  private void validateGoalNotDone(Goal goal) {
    if (goal.isDone()) {
      throw new IllegalArgumentException(TodoErrorCode.GOAL_ALREADY_DONE.getCodeName());
    }
  }

}
