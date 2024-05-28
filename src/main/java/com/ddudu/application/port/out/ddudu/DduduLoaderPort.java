package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.dto.ddudu.BasicDduduWithGoalId;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface DduduLoaderPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  List<Ddudu> getDailyDdudusOfUserUnderGoals(LocalDate date, User user, List<Goal> goals);

  List<GoalGroupedDdudus> getDailyDdudusOfUserGroupingByGoal(
      LocalDate date, User loginUser, List<Goal> goals
  );

  List<GoalGroupedDdudus> getUnassignedDdudusOfUserGroupingByGoal(
      LocalDate date, User user, List<Goal> goals
  );

  Map<LocalTime, List<BasicDduduWithGoalId>> getDailyDdudusOfUserGroupingByTime(
      LocalDate date, User user, List<Goal> goals
  );

}
