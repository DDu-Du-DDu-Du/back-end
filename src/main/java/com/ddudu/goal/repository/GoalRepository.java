package com.ddudu.goal.repository;

import com.ddudu.goal.domain.Goal;
import com.ddudu.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalRepository extends JpaRepository<Goal, Long> {

  @Query(
      "SELECT g FROM Goal g WHERE g.id=:id AND g.isDeleted=false"
  )
  Optional<Goal> findById(Long id);

  @Query(
      "SELECT g FROM Goal g WHERE g.user=:user AND g.isDeleted=false ORDER BY g.status DESC, g.createdAt ASC"
  )
  List<Goal> findAllByUser(User user);

}
