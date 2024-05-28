package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.dto.ddudu.BasicDduduWithGoalId;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.PeriodSetupPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class DduduPersistenceAdapter implements DduduLoaderPort, PeriodSetupPort, SaveDduduPort {

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
  public List<Ddudu> getDailyDdudusOfUserUnderGoals(LocalDate date, User user, List<Goal> goals) {
    return dduduRepository.findDdudusByDateAndUserAndGoals(
            date,
            UserEntity.from(user),
            goals.stream()
                .map(GoalEntity::from)
                .toList()
        )
        .stream()
        .map(DduduEntity::toDomain)
        .toList();
  }

  @Override
  public List<GoalGroupedDdudus> getDailyDdudusOfUserGroupingByGoal(
      LocalDate date, User loginUser, List<Goal> goals
  ) {
    List<GoalEntity> goalEntities = goals.stream()
        .map(GoalEntity::from)
        .toList();

    return dduduRepository.findDailyDdudusByUserGroupByGoal(
        date, UserEntity.from(loginUser), goalEntities);
  }

  @Override
  public List<GoalGroupedDdudus> getUnassignedDdudusOfUserGroupingByGoal(
      LocalDate date, User user, List<Goal> goals
  ) {
    return dduduRepository.findUnassignedDdudusByUserGroupByGoal(
        date, UserEntity.from(user), goals.stream()
            .map(GoalEntity::from)
            .toList()
    );
  }

  @Override
  public Map<LocalTime, List<BasicDduduWithGoalId>> getDailyDdudusOfUserGroupingByTime(
      LocalDate date, User user, List<Goal> goals
  ) {
    return dduduRepository.findDailyDdudusByUserGroupByTime(
        date, UserEntity.from(user), goals.stream()
            .map(GoalEntity::from)
            .toList()
    );
  }

  @Override
  public Ddudu updatePeriod(Ddudu ddudu) {
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

}
