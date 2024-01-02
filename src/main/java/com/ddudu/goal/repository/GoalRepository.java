package com.ddudu.goal.repository;

import com.ddudu.goal.domain.Goal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {

  @Query(
      "SELECT g FROM Goal g WHERE g.id=:id AND g.isDeleted=false"
  )
  Optional<Goal> findById(Long id);

}
