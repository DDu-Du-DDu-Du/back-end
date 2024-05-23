package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import java.time.LocalDate;
import java.util.List;

public interface DduduLoaderPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  List<Ddudu> findAllByDateAndUserAndGoals(LocalDate date, Long userId, List<Goal> goals);

}
