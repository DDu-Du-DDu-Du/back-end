package com.ddudu.infrastructure.persistence.repository.repeat_ddudu;

import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.RepeatDduduEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatDduduRepository extends JpaRepository<RepeatDduduEntity, Long> {

  List<RepeatDduduEntity> findAllByGoal(GoalEntity goal);

}
