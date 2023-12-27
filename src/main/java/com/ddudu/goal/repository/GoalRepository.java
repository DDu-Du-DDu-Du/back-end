package com.ddudu.goal.repository;

import com.ddudu.goal.domain.Goal;
import com.ddudu.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalRepository extends JpaRepository<Goal, Long> {

  @Query("SELECT g FROM Goal g ORDER BY g.status DESC, g.createdAt ASC")
  List<Goal> findAllByUser(User user);

}
