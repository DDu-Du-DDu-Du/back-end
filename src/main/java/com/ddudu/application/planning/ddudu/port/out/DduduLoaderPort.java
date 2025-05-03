package com.ddudu.application.planning.ddudu.port.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DduduLoaderPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  Optional<Ddudu> getOptionalDdudu(Long id);

  List<Ddudu> getRepeatedDdudus(RepeatDdudu repeatDdudu);

  List<Ddudu> getDailyDdudus(
      LocalDate date, User user, List<PrivacyType> accessiblePrivacyTypes
  );

}
