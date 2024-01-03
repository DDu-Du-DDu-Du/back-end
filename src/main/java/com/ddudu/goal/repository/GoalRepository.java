package com.ddudu.goal.repository;

import com.ddudu.goal.domain.Goal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {

  Optional<Goal> findById(Long id);

}
