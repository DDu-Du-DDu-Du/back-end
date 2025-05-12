package com.ddudu.infra.mysql.planning.ddudu.adapter;

import com.ddudu.aggregate.BaseStats;
import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DduduSearchPort;
import com.ddudu.application.common.port.ddudu.out.DduduUpdatePort;
import com.ddudu.application.common.port.ddudu.out.DeleteDduduPort;
import com.ddudu.application.common.port.ddudu.out.RepeatDduduPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.stats.out.DduduStatsPort;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.infra.mysql.planning.ddudu.dto.DduduCursorDto;
import com.ddudu.infra.mysql.planning.ddudu.entity.DduduEntity;
import com.ddudu.infra.mysql.planning.ddudu.repository.DduduRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class DduduPersistenceAdapter implements DduduLoaderPort, DduduUpdatePort, SaveDduduPort,
    RepeatDduduPort, DduduSearchPort, DeleteDduduPort, DduduStatsPort, MonthlyStatsPort {

  private final DduduRepository dduduRepository;

  @Override
  public Ddudu getDduduOrElseThrow(Long id, String message) {
    return dduduRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            Ddudu.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public Optional<Ddudu> getOptionalDdudu(Long id) {
    return dduduRepository.findById(id)
        .map(DduduEntity::toDomain);
  }

  @Override
  public List<Ddudu> getRepeatedDdudus(RepeatDdudu repeatDdudu) {
    return dduduRepository.findAllByRepeatDduduId(repeatDdudu.getId())
        .stream()
        .map(DduduEntity::toDomain)
        .toList();
  }

  @Override
  public List<Ddudu> getDailyDdudus(
      LocalDate date, Long userId, List<PrivacyType> accessiblePrivacyTypes
  ) {
    return dduduRepository.findAllByDateAndUserAndPrivacyTypes(date, userId, accessiblePrivacyTypes)
        .stream()
        .map(DduduEntity::toDomain)
        .toList();
  }

  @Override
  public Ddudu update(Ddudu ddudu) {
    DduduEntity dduduEntity = dduduRepository.findById(ddudu.getId())
        .orElseThrow(EntityNotFoundException::new);

    dduduEntity.update(ddudu);

    return dduduEntity.toDomain();
  }

  @Override
  public Ddudu save(Ddudu ddudu) {
    return dduduRepository.save(DduduEntity.from(ddudu))
        .toDomain();
  }

  @Override
  public List<Ddudu> saveAll(List<Ddudu> ddudus) {
    List<DduduEntity> dduduEntities = ddudus.stream()
        .map(DduduEntity::from)
        .toList();

    return dduduRepository.saveAll(dduduEntities)
        .stream()
        .map(DduduEntity::toDomain)
        .toList();
  }

  @Override
  public ScrollResponse<SimpleDduduSearchDto> search(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine
  ) {
    List<DduduCursorDto> ddudusWithCursor = dduduRepository.findScrollDdudus(
        userId,
        request,
        query,
        isMine,
        false
    );

    return getScrollResponse(ddudusWithCursor, request.getSize());
  }

  @Override
  public void delete(Ddudu ddudu) {
    dduduRepository.delete(DduduEntity.from(ddudu));
  }

  @Override
  public void deleteAllByRepeatDdudu(RepeatDdudu repeatDdudu) {
    dduduRepository.deleteAllByRepeatDduduId(repeatDdudu.getId());
  }

  private ScrollResponse<SimpleDduduSearchDto> getScrollResponse(
      List<DduduCursorDto> ddudusWithCursor,
      int size
  ) {
    List<SimpleDduduSearchDto> simpleDdudus = ddudusWithCursor.stream()
        .limit(size)
        .map(DduduCursorDto::ddudu)
        .toList();
    String nextCursor = getNextCursor(ddudusWithCursor, size);

    return ScrollResponse.from(simpleDdudus, nextCursor);
  }

  private String getNextCursor(List<DduduCursorDto> ddudusWithCursor, int size) {
    if (ddudusWithCursor.size() > size) {
      return ddudusWithCursor.get(size - 1)
          .cursor();
    }

    return null;
  }

  @Override
  public List<DduduCompletionResponse> calculateDdudusCompletion(
      LocalDate startDate, LocalDate endDate, Long userId, List<PrivacyType> privacyTypes
  ) {
    return dduduRepository.findDdudusCompletion(
        startDate, endDate, userId, privacyTypes);
  }

  @Override
  public Map<YearMonth, MonthlyStats> collectMonthlyStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  ) {
    Long goalId = Objects.nonNull(goal) ? goal.getId() : null;
    List<BaseStats> stats = dduduRepository.findStatsBaseOfUser(userId, goalId, from, to);

    return stats.stream()
        .collect(Collectors.groupingBy(stat -> YearMonth.from(stat.getScheduledOn())))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
                Map.Entry::getKey,
                monthlyStatsEntry -> MonthlyStats.builder()
                    .userId(userId)
                    .yearMonth(monthlyStatsEntry.getKey())
                    .stats(monthlyStatsEntry.getValue())
                    .build()
            )
        );
  }

}
