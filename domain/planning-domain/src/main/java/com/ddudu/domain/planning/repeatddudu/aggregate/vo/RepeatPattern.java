package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import java.time.LocalDate;
import java.util.List;

public interface RepeatPattern {

  RepeatInfo getInfo();

  List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate);

}
