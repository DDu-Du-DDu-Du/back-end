package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.user.domain.User;
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
