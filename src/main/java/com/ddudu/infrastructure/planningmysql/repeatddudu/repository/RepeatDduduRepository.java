package com.ddudu.infrastructure.planningmysql.repeatddudu.repository;

import com.ddudu.infrastructure.planningmysql.repeatddudu.entity.RepeatDduduEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatDduduRepository extends JpaRepository<RepeatDduduEntity, Long>,
    RepeatDduduQueryRepository {

  List<RepeatDduduEntity> findAllByGoalId(Long goalId);

}
