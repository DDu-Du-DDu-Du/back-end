package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.GoalGroupedTodos;
import com.ddudu.application.common.port.ddudu.in.GetDailyTodosByGoalUseCase;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.application.planning.ddudu.model.TodoList;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.Relationship;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDailyTodosByGoalService implements GetDailyTodosByGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final TodoLoaderPort dduduLoaderPort;
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

    // 3. 뚜두 조회
    TodoList ddudus = new TodoList(dduduLoaderPort.getDailyTodos(
        date,
        user.getId(),
        accessiblePrivacyTypes
    ));

    return ddudus.getTodosWithGoal(goalLoaderPort.findAllByUserAndPrivacyTypes(
        user.getId(),
        accessiblePrivacyTypes
    ));
  }

}
