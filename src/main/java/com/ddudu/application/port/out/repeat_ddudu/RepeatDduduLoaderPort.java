package com.ddudu.application.port.out.repeat_ddudu;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import java.util.List;
import java.util.Optional;

public interface RepeatDduduLoaderPort {

  Optional<RepeatDdudu> getOptionalRepeatDdudu(Long id);

  List<RepeatDdudu> getAllByGoal(Goal goal);

  RepeatDdudu getOrElseThrow(Long id, String message);

}
