package com.ddudu.infrastructure.planningmysql.ddudu.repository;

import com.ddudu.infrastructure.planningmysql.ddudu.entity.DduduEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DduduRepository extends JpaRepository<DduduEntity, Long>, DduduQueryRepository {

  List<DduduEntity> findAllByRepeatDduduId(Long repeatDduduEntityId);

}
