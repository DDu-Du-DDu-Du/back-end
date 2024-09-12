package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.application.dto.scroll.response.ScrollResponse;
import com.ddudu.application.dto.stats.StatsBaseDto;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.DduduSearchPort;
import com.ddudu.application.port.out.ddudu.DduduStatsPort;
import com.ddudu.application.port.out.ddudu.DduduUpdatePort;
import com.ddudu.application.port.out.ddudu.DeleteDduduPort;
import com.ddudu.application.port.out.ddudu.RepeatDduduPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.MonthlyStatsPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.dto.DduduCursorDto;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.RepeatDduduEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
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
    return dduduRepository.findAllByRepeatDdudu(RepeatDduduEntity.from(repeatDdudu))
        .stream()
        .map(DduduEntity::toDomain)
        .toList();
  }

  @Override
  public List<Ddudu> getDailyDdudus(
      LocalDate date, User user, List<PrivacyType> accessiblePrivacyTypes
  ) {
    return dduduRepository.findAllByDateAndUserAndPrivacyTypes(
            date, UserEntity.from(user), accessiblePrivacyTypes)
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
      Long userId, ScrollRequest request, String query, Boolean isMine
  ) {
    List<DduduCursorDto> ddudusWithCursor = dduduRepository.findScrollDdudus(
        userId, request, query, isMine, false);

    return getScrollResponse(ddudusWithCursor, request.getSize());
  }

  @Override
  public void delete(Ddudu ddudu) {
    dduduRepository.delete(DduduEntity.from(ddudu));
  }

  @Override
  public void deleteAllByRepeatDdudu(RepeatDdudu repeatDdudu) {
    dduduRepository.deleteAllByRepeatDdudu(RepeatDduduEntity.from(repeatDdudu));
  }

  private ScrollResponse<SimpleDduduSearchDto> getScrollResponse(
      List<DduduCursorDto> ddudusWithCursor, int size
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
      LocalDate startDate, LocalDate endDate, User user, List<PrivacyType> privacyTypes
  ) {
    return dduduRepository.findDdudusCompletion(
        startDate, endDate, UserEntity.from(user), privacyTypes);
  }

  @Override
  public List<StatsBaseDto> collectMonthlyStats(
      User user, Goal goal, LocalDate from, LocalDate to
  ) {
    GoalEntity goalEntity = Objects.nonNull(goal) ? GoalEntity.from(goal) : null;

    return dduduRepository.findStatsBaseOfUser(UserEntity.from(user), goalEntity, from, to);
  }

}
