package com.ddudu.application.service.goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.service.ddudu.CollectMonthlyStatsSummaryService;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectNumberStatsServiceTest {

  @MockBean
  DateTimeProvider dateTimeProvider;

  @SpyBean
  AuditingHandler auditingHandler;

  @Autowired
  CollectMonthlyStatsSummaryService collectMonthlyStatsService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  List<Goal> goals;
  int uncompletedCountPerGoal;
  int completedCountPerGoal;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    auditingHandler.setDateTimeProvider(dateTimeProvider);
    doReturn(Optional.of(LocalDateTime.now())).when(dateTimeProvider)
        .getNow();

    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user, 5));
    uncompletedCountPerGoal = 5;
    completedCountPerGoal = 5;

    goals.forEach(goal -> saveDduduPort.saveAll(
        DduduFixture.createDifferentDdudusWithGoal(goal, completedCountPerGoal,
            uncompletedCountPerGoal
        )));
  }

  @Test
  void 이번달_뚜두_통합_통계를_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();
    LocalDateTime lastMonth = LocalDateTime.now()
        .minusMonths(1);

    doReturn(Optional.of(lastMonth)).when(dateTimeProvider)
        .getNow();
    goals.forEach(goal -> saveDduduPort.saveAll(
        DduduFixture.createDifferentDdudusWithGoal(goal, completedCountPerGoal,
            uncompletedCountPerGoal
        )));

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(), thisMonth);

    // then
    int totalPerGoal = uncompletedCountPerGoal + completedCountPerGoal;
    System.out.println(response);

    assertThat(response.lastMonth())
        .hasFieldOrPropertyWithValue("yearMonth", YearMonth.from(lastMonth))
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
    assertThat(response.thisMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth)
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
  }

  @Test
  void 요청의_날짜가_없으면_이번달의_뚜두_통계를_낸다() {
    // given
    LocalDateTime lastMonth = LocalDateTime.now()
        .minusMonths(1);

    doReturn(Optional.of(lastMonth)).when(dateTimeProvider)
        .getNow();
    goals.forEach(goal -> saveDduduPort.saveAll(
        DduduFixture.createDifferentDdudusWithGoal(goal, completedCountPerGoal,
            uncompletedCountPerGoal
        )));

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(), null);

    // then
    int totalPerGoal = uncompletedCountPerGoal + completedCountPerGoal;

    assertThat(response.lastMonth())
        .hasFieldOrPropertyWithValue("yearMonth", YearMonth.from(lastMonth))
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
    assertThat(response.thisMonth())
        .hasFieldOrPropertyWithValue("yearMonth", YearMonth.now())
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
  }

  @Test
  void 통계_대상_월에_뚜두를_생성하지_않은_목표는_통계에서_제외된다() {
    // given
    YearMonth nextMonth = YearMonth.now()
        .plusMonths(1);
    Ddudu ddudu = DduduFixture.createRandomDduduWithStatusAndSchedule(
        goals.get(0), DduduStatus.COMPLETE, nextMonth.atDay(1));

    doReturn(Optional.of(LocalDate.now())).when(dateTimeProvider)
        .getNow();
    saveDduduPort.save(ddudu);

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(), nextMonth);

    // then
    assertThat(response.thisMonth()
        .totalCount()).isZero();
  }

  @Test
  void 지난달_뚜두가_없어도_통계는_0으로_계산된다() {
    // given
    YearMonth lastMonth = YearMonth.now()
        .minusMonths(1);

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(), lastMonth);

    // then
    assertThat(response.lastMonth()
        .totalCount()).isZero();
  }

  @Test
  void 로그인_사용자가_없으면_월_달성_뚜두_수_통계를_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable collect = () -> collectMonthlyStatsService.collectMonthlyTotalStats(
        invalidId, thisMonth);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(collect)
        .withMessage(GoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}