package com.ddudu.infra.mysql.planning.ddudu.repository;

import com.ddudu.infra.mysql.planning.ddudu.entity.DduduEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DduduRepository extends JpaRepository<DduduEntity, Long>, DduduQueryRepository {

  List<DduduEntity> findAllByRepeatDduduId(Long repeatDduduEntityId);

}
