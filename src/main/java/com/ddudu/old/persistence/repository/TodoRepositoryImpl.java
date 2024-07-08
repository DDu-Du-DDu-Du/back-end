package com.ddudu.old.persistence.repository;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.old.todo.domain.OldTodoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements OldTodoRepository {

  private final DduduRepository dduduRepository;

  @Override
  public Ddudu save(Ddudu ddudu) {
    return dduduRepository.save(DduduEntity.from(ddudu))
        .toDomain();
  }

  @Override
  public Optional<Ddudu> findById(Long id) {
    return dduduRepository.findById(id)
        .map(DduduEntity::toDomain);
  }

  @Override
  public List<Ddudu> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, User user
  ) {
    return dduduRepository.findTodosByDate(startDate, endDate, UserEntity.from(user))
        .stream()
        .map(DduduEntity::toDomain)
        .toList();
  }

  @Override
  public List<DduduCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user, List<PrivacyType> privacyTypes
  ) {
    return dduduRepository.findDdudusCompletion(
        startDate, endDate, UserEntity.from(user), privacyTypes);
  }

  @Override
  public void update(Ddudu ddudu) {
    dduduRepository.save(DduduEntity.from(ddudu));
  }

  @Override
  public void delete(Ddudu ddudu) {
    dduduRepository.delete(DduduEntity.from(ddudu));
  }

}
