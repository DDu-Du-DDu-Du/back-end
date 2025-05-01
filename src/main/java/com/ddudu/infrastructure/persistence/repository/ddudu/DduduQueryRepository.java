package com.ddudu.infrastructure.persistence.repository.ddudu;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.application.dto.stats.StatsBaseDto;
import com.ddudu.infrastructure.persistence.dto.DduduCursorDto;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
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
