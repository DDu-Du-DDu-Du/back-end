package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.ddudu.service.DduduDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.stats.MonthlyStatsSummaryDto;
import com.ddudu.application.dto.stats.StatsBaseDto;
import com.ddudu.application.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.port.in.ddudu.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.application.port.out.goal.MonthlyStatsPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CollectMonthlyStatsSummaryService implements CollectMonthlyStatsSummaryUseCase {

  private static final int FIRST_DATE = 1;

  private final UserLoaderPort userLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;
  private final DduduDomainService dduduDomainService;

  @Override
  public MonthlyStatsSummaryResponse collectMonthlyTotalStats(
      Long loginId, YearMonth yearMonth
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());
    LocalDate from = getFirstDateOfLastMonth(yearMonth);
    LocalDate to = getLastDateOfMonth(yearMonth);
    YearMonth lastMonth = YearMonth.from(from);
    YearMonth thisMonth = YearMonth.from(to);
    List<StatsBaseDto> stats = monthlyStatsPort.collectMonthlyStats(user, null, from, to);
    Map<YearMonth, List<StatsBaseDto>> statsMap = collectStatsMap(stats, lastMonth, thisMonth);
    MonthlyStatsSummaryDto lastMonthStats = dduduDomainService.calculateMonthlyStats(
        lastMonth, statsMap.get(lastMonth));
    MonthlyStatsSummaryDto thisMonthStats = dduduDomainService.calculateMonthlyStats(
        thisMonth, statsMap.get(thisMonth));

    return MonthlyStatsSummaryResponse.from(lastMonthStats, thisMonthStats);
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

  private Map<YearMonth, List<StatsBaseDto>> collectStatsMap(
      List<StatsBaseDto> stats,
      YearMonth lastMonth,
      YearMonth thisMonth
  ) {
    Map<YearMonth, List<StatsBaseDto>> statsMap = new HashMap<>();

    statsMap.put(lastMonth, List.of());
    statsMap.put(thisMonth, List.of());

    stats.forEach(stat -> {
      YearMonth key = YearMonth.from(stat.scheduledOn());
      List<StatsBaseDto> mutable = new ArrayList<>(statsMap.get(key));

      mutable.add(stat);
      statsMap.put(key, List.copyOf(mutable));
    });

    return Collections.unmodifiableMap(statsMap);
  }

}
