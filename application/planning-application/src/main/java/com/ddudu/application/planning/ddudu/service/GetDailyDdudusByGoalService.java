package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.application.planning.ddudu.model.DduduList;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.Relationship;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.port.ddudu.in.GetDailyDdudusByGoalUseCase;
import com.ddudu.application.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.user.out.UserLoaderPort;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDailyDdudusByGoalService implements GetDailyDdudusByGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public List<GoalGroupedDdudus> get(Long loginId, Long userId, LocalDate date) {
    // 1. 요청 사용자와 조회 대상 사용자 조회
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    // 2. 사용자 간 관계 확인
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    // 3. 뚜두 조회
    DduduList ddudus = new DduduList(
        dduduLoaderPort.getDailyDdudus(date, user.getId(), accessiblePrivacyTypes));

    return ddudus.getDdudusWithGoal(
        goalLoaderPort.findAllByUserAndPrivacyTypes(user.getId(), accessiblePrivacyTypes)
    );
  }

}
