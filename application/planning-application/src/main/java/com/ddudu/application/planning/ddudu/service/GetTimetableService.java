package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.application.planning.ddudu.model.Timetable;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.Relationship;
import com.ddudu.application.common.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.common.dto.ddudu.TimeGroupedDdudus;
import com.ddudu.application.common.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.common.port.ddudu.in.GetTimetableUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTimetableService implements
    GetTimetableUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;

  @Override
  public TimetableResponse get(Long loginId, Long userId, LocalDate date) {
    // 1. 요청 유저, 검색 대상 유저 조회 및 검증
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    // 2. 두 유저 사이의 관계 확인 및 접근 가능한 PrivacyType 조회
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    // 3. 타임 테이블 조회
    List<Ddudu> ddudus = dduduLoaderPort.getDailyDdudus(date, user.getId(), accessiblePrivacyTypes);
    Timetable timetable = new Timetable(ddudus);

    // 4. 응답 생성 (데이터 변환)
    List<Goal> goals = goalLoaderPort.findAllByUserAndPrivacyTypes(user.getId(), accessiblePrivacyTypes);
    List<TimeGroupedDdudus> assignedDdudus = timetable.getTimeGroupedDdudus(goals);
    List<GoalGroupedDdudus> unassignedDdudus = timetable.getUnassignedDdudusWithGoal(goals);

    return TimetableResponse.of(assignedDdudus, unassignedDdudus);
  }

}
