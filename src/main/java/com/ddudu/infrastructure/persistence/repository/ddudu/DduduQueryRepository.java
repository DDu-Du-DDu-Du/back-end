package com.ddudu.infrastructure.persistence.repository.ddudu;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.infrastructure.persistence.dto.DduduCursorDto;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DduduQueryRepository {

  List<DduduEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  );

  List<DduduCompletionResponse> findDdudusCompletion(
      LocalDate startDate, LocalDate endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

  void deleteAllByGoal(GoalEntity goal);

  List<DduduCursorDto> findScrollDdudus(
      Long userId, ScrollRequest request, String query, Boolean isMine, Boolean isFollower
  );

  List<DduduEntity> findAllByDateAndUserAndPrivacyTypes(
      LocalDate date, UserEntity from, List<PrivacyType> accessiblePrivacyTypes
  );

}
