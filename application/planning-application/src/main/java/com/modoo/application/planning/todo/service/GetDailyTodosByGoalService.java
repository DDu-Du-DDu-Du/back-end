package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.GoalGroupedTodos;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.todo.in.GetDailyTodosByGoalUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.application.planning.todo.model.TodoList;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.domain.user.user.aggregate.enums.Relationship;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDailyTodosByGoalService implements GetDailyTodosByGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public List<GoalGroupedTodos> get(Long loginId, Long userId, LocalDate date) {
    // 1. 요청 사용자와 조회 대상 사용자 조회
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    User user = userLoaderPort.getUserOrElseThrow(
        userId,
        TodoErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    // 2. 사용자 간 관계 확인
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    // 3. 투두 조회
    TodoList todos = new TodoList(todoLoaderPort.getDailyTodos(
        date,
        user.getId(),
        accessiblePrivacyTypes
    ));

    return todos.getTodosWithGoal(goalLoaderPort.findAllByUserAndPrivacyTypes(
        user.getId(),
        accessiblePrivacyTypes
    ));
  }

}
