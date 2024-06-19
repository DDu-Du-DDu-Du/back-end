package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.TimeGroupedDdudus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DduduLoaderPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  Optional<Ddudu> getOptionalDdudu(Long id);

  List<Ddudu> getDailyDdudusOfUserUnderGoals(LocalDate date, User user, List<Goal> goals);

  List<GoalGroupedDdudus> getDailyDdudusOfUserGroupingByGoal(
      LocalDate date, User loginUser, List<Goal> goals
  );

  List<GoalGroupedDdudus> getUnassignedDdudusOfUserGroupingByGoal(
      LocalDate date, User user, List<Goal> goals
  );

  List<TimeGroupedDdudus> getDailyDdudusOfUserGroupingByTime(
      LocalDate date, User user, List<Goal> goals
  );

  List<Ddudu> getRepeatedDdudus(RepeatDdudu repeatDdudu);

}
