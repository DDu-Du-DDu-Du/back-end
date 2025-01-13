package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Timetable;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.enums.Relationship;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.TimeGroupedDdudus;
import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.port.in.ddudu.GetTimetableUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
    Timetable timetable = new Timetable(
        dduduLoaderPort.getDailyDdudus(date, user, accessiblePrivacyTypes));

    List<TimeGroupedDdudus> assignedDdudus = timetable.getTimeGroupedDdudus();
    List<GoalGroupedDdudus> unassignedDdudus = timetable.getUnassignedDdudusWithGoal(
        goalLoaderPort.findAllByUserAndPrivacyTypes(user, accessiblePrivacyTypes)
    );

    return TimetableResponse.of(assignedDdudus, unassignedDdudus);
  }

}
