package com.ddudu.infrastructure.planningmysql.ddudu.repository;

import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.application.stats.dto.response.DduduCompletionResponse;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.stats.dto.StatsBaseDto;
import com.ddudu.infrastructure.planningmysql.ddudu.dto.DduduCursorDto;
import com.ddudu.infrastructure.planningmysql.ddudu.entity.DduduEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DduduQueryRepository {

  List<DduduEntity> findDdudusByDate(
      LocalDateTime startDate, LocalDateTime endDate, Long userId
  );

  List<DduduCompletionResponse> findDdudusCompletion(
      LocalDate startDate, LocalDate endDate, Long userId,
      List<PrivacyType> privacyTypes
  );

  void deleteAllByGoalId(Long goalId);

  List<DduduCursorDto> findScrollDdudus(
      Long userId, ScrollRequest request, String query, Boolean isMine, Boolean isFollower
  );

  List<DduduEntity> findAllByDateAndUserAndPrivacyTypes(
      LocalDate date, Long userId, List<PrivacyType> accessiblePrivacyTypes
  );

  void deleteAllByRepeatDduduId(Long repeatDduduId);

  List<StatsBaseDto> findStatsBaseOfUser(
      Long userId, Long goalId, LocalDate from, LocalDate to
  );

}
