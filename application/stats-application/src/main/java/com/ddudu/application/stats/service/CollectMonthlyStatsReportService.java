package com.ddudu.application.stats.service;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.MonthlyStatsReportDto;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsReportResponse;
import com.ddudu.application.common.port.stats.in.CollectMonthlyStatsReportUseCase;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectMonthlyStatsReportService implements CollectMonthlyStatsReportUseCase {

  private static final int FIRST_DATE = 1;

  private final UserLoaderPort userLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;

  @Override
  public MonthlyStatsReportResponse collectReport(Long loginId, YearMonth yearMonth) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        StatsErrorCode.USER_NOT_EXISTING.getCodeName()
    );
    LocalDate from = getFirstDateOfLastMonth(yearMonth);
    LocalDate to = getLastDateOfMonth(yearMonth);
    YearMonth lastMonth = YearMonth.from(from);
    YearMonth thisMonth = YearMonth.from(to);
    Map<YearMonth, MonthlyStats> monthlyStats = monthlyStatsPort.collectMonthlyStats(
        user.getId(),
        null,
        from,
        to
    );
    MonthlyStatsReportDto lastMonthStats = MonthlyStatsReportDto.from(monthlyStats.getOrDefault(
        lastMonth,
        MonthlyStats.empty(user.getId(), lastMonth)
    ));
    MonthlyStatsReportDto thisMonthStats = MonthlyStatsReportDto.from(monthlyStats.getOrDefault(
        thisMonth,
        MonthlyStats.empty(user.getId(), thisMonth)
    ));

    return MonthlyStatsReportResponse.from(lastMonthStats, thisMonthStats);
  }

  private LocalDate getFirstDateOfLastMonth(YearMonth yearMonth) {
    if (Objects.isNull(yearMonth)) {
      yearMonth = YearMonth.now();
    }

    return yearMonth.minusMonths(1)
        .atDay(FIRST_DATE);
  }

  private LocalDate getLastDateOfMonth(YearMonth yearMonth) {
    if (Objects.nonNull(yearMonth)) {
      return yearMonth.atEndOfMonth();
    }

    return YearMonth.now()
        .atEndOfMonth();
  }

}
