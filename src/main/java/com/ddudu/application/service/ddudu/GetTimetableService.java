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
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    List<PrivacyType> accessiblePrivacyTypes = Relationship.getRelationship(loginUser, user)
        .getAccessiblePrivacyTypes();
    Timetable timetable = new Timetable(
        dduduLoaderPort.getDailyDdudus(date, user, accessiblePrivacyTypes));

    List<TimeGroupedDdudus> assignedDdudus = timetable.getTimeGroupedDdudus();
    List<GoalGroupedDdudus> unassignedDdudus = timetable.getUnassignedDdudusWithGoal(
        goalLoaderPort.findAllByUserAndPrivacyTypes(user, accessiblePrivacyTypes)
    );

    return TimetableResponse.of(assignedDdudus, unassignedDdudus);
  }

}
