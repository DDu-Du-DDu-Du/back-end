package com.modoo.infra.mysql.planning.todo.repository;

import com.modoo.infra.mysql.planning.todo.entity.TodoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoEntity, Long>, TodoQueryRepository {

  List<TodoEntity> findAllByRepeatTodoId(Long repeatTodoEntityId);

}
