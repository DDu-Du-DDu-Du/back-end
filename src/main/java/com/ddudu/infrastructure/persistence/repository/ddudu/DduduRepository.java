package com.ddudu.infrastructure.persistence.repository.ddudu;

import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DduduRepository extends JpaRepository<DduduEntity, Long>, DduduQueryRepository {

  List<DduduEntity> findAllByGoal(GoalEntity goalEntity);

}
