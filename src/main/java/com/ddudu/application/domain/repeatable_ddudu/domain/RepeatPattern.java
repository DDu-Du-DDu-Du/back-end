package com.ddudu.application.domain.repeatable_ddudu.domain;

import java.time.LocalDate;
import java.util.List;

public interface RepeatPattern {

  List<LocalDate> calculateRepetitionDates(LocalDate startDate, LocalDate endDate);

}
