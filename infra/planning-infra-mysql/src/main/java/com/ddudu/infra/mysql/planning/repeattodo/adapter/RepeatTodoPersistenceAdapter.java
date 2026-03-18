package com.ddudu.infra.mysql.planning.repeattodo.adapter;

import com.ddudu.application.common.port.repeattodo.out.DeleteRepeatTodoPort;
import com.ddudu.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.ddudu.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.ddudu.application.common.port.repeattodo.out.UpdateRepeatTodoPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.infra.mysql.planning.repeattodo.entity.RepeatTodoEntity;
import com.ddudu.infra.mysql.planning.repeattodo.repository.RepeatTodoRepository;
import com.ddudu.infra.mysql.planning.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class RepeatTodoPersistenceAdapter implements SaveRepeatTodoPort, RepeatTodoLoaderPort,
    UpdateRepeatTodoPort, DeleteRepeatTodoPort {

  private final RepeatTodoRepository repeatTodoRepository;
  private final TodoRepository dduduRepository;

  @Override
  public RepeatTodo save(RepeatTodo repeatTodo) {
    return repeatTodoRepository.save(RepeatTodoEntity.from(repeatTodo))
        .toDomain();
  }


  @Override
  public Optional<RepeatTodo> getOptionalRepeatTodo(Long id) {
    return repeatTodoRepository.findById(id)
        .map(RepeatTodoEntity::toDomain);
  }

  @Override
  public List<RepeatTodo> getAllByGoal(Goal goal) {
    return repeatTodoRepository.findAllByGoalId(goal.getId())
        .stream()
        .map(RepeatTodoEntity::toDomain)
        .toList();
  }

  @Override
  public RepeatTodo getOrElseThrow(Long id, String message) {
    return repeatTodoRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            RepeatTodo.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public RepeatTodo update(RepeatTodo repeatTodo) {
    RepeatTodoEntity repeatTodoEntity = repeatTodoRepository.findById(repeatTodo.getId())
        .orElseThrow(EntityNotFoundException::new);

    repeatTodoEntity.update(repeatTodo);

    return repeatTodoEntity.toDomain();
  }

  @Override
  public void deleteWithTodos(RepeatTodo repeatTodo) {
    dduduRepository.deleteAllByRepeatTodoId(repeatTodo.getId());
    repeatTodoRepository.delete(RepeatTodoEntity.from(repeatTodo));
  }

  @Override
  public void deleteAllWithTodosByGoal(Goal goal) {
    dduduRepository.deleteAllByGoalId(goal.getId());
    repeatTodoRepository.deleteAllByGoal(goal.getId());
  }

}
