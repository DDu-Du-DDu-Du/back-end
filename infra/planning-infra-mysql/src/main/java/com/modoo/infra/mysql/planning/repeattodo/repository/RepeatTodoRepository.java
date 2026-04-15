package com.modoo.infra.mysql.planning.repeattodo.repository;

import com.modoo.infra.mysql.planning.repeattodo.entity.RepeatTodoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatTodoRepository extends JpaRepository<RepeatTodoEntity, Long>,
    RepeatTodoQueryRepository {

  List<RepeatTodoEntity> findAllByGoalId(Long goalId);

}
