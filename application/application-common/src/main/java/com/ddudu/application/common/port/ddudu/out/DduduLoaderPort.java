package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DduduLoaderPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  Optional<Ddudu> getOptionalDdudu(Long id);

  List<Ddudu> getRepeatedDdudus(RepeatDdudu repeatDdudu);

  List<Ddudu> getDailyDdudus(LocalDate date, Long userId, List<PrivacyType> accessiblePrivacyTypes);

  int countTodayDdudu(Long userId);

}
