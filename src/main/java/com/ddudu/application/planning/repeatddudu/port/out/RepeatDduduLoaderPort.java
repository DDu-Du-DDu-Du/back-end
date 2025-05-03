package com.ddudu.application.planning.repeatddudu.port.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import java.util.List;
import java.util.Optional;

public interface RepeatDduduLoaderPort {

  Optional<RepeatDdudu> getOptionalRepeatDdudu(Long id);

  List<RepeatDdudu> getAllByGoal(Goal goal);

  RepeatDdudu getOrElseThrow(Long id, String message);

}
