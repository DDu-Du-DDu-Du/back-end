package com.ddudu.application.port.out;

import com.ddudu.application.domain.goal.domain.Goal;
import java.util.Optional;

public interface GoalLoaderPort {

  Optional<Goal> findById(Long id);

}
