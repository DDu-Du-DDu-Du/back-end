package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.application.common.dto.stats.AchievedDetailOverviewDto;
import com.ddudu.application.common.dto.stats.DayOfWeekStatsDto;
import com.ddudu.application.common.dto.stats.GenericCalendarStats;
import com.ddudu.application.common.dto.stats.PostponedDetailOverviewDto;
import com.ddudu.application.common.dto.stats.RepeatTodoStatsDto;
import com.ddudu.application.common.dto.stats.response.AchievedStatsDetailResponse;
import com.ddudu.application.common.dto.stats.response.PostponedStatsDetailResponse;
import com.ddudu.application.common.dto.stats.response.TodoCompletionResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.service.RepeatTodoDomainService;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatTodoFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import com.ddudu.fixtures.MonthlyStatsFixture;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectMonthlyStatsDetailServiceTest {

  @Autowired
  CollectMonthlyStatsDetailService collectMonthlyAchievedDetailService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  SaveRepeatTodoPort saveRepeatTodoPort;

  @Autowired
  RepeatTodoDomainService repeatTodoDomainService;

  User user;
  Goal goal;
  YearMonth thisMonth;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    thisMonth = YearMonth.now();
  }

  @Nested
  class 달성_상세_통계_테스트 {

    @Test
    void 달성_상세_응답에_목표_색상이_포함된다() {
      // given

      // when
      AchievedStatsDetailResponse response =
          collectMonthlyAchievedDetailService.collectAchievedDetail(
          user.getId(),
          goal.getId(),
          thisMonth,
          thisMonth
      );

      // then
      assertThat(response.goalColor()).isEqualTo(goal.getColor());
    }

    @Nested
    class 오버뷰_테스트 {

      int size;

      @BeforeEach
      void setUp() {
        size = TodoFixture.getRandomInt(1, 100);

        saveTodoPort.saveAll(TodoFixture.createMultipleTodosWithGoal(goal, size));
      }

      @Test
      void 이번_달에_데이터가_있으면_총계_달성수_달성률이_유효하다() {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            thisMonth
        );

        // then
        AchievedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isEqualTo(size);
        assertThat(actual.achievementCount()).isBetween(0, actual.totalCount());
        assertThat(actual.achievementRate()).isBetween(0, 100);
      }

      @Test
      void 조회_대상_월에_데이터가_없으면_총계_달성수_달성률은_0이다() {
        // given
        YearMonth nextMonth = thisMonth.plusMonths(1);

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            nextMonth,
            nextMonth
        );

        // then
        AchievedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isZero();
        assertThat(actual.achievementCount()).isZero();
        assertThat(actual.achievementRate()).isZero();
      }

      @ParameterizedTest
      @NullSource
      void 시작_월이_null이면_이번달로_대체되어_정상_반환된다(YearMonth nullMonth) {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            nullMonth,
            thisMonth
        );

        // then
        AchievedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isEqualTo(size);
      }

      @ParameterizedTest
      @NullSource
      void 종료_월이_null이면_이번달로_대체되어_정상_반환된다(YearMonth nullMonth) {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            nullMonth
        );

        // then
        AchievedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isEqualTo(size);
      }

    }

    @Nested
    class 요일통계_테스트 {

      int size;

      @BeforeEach
      void setUp() {
        size = TodoFixture.getRandomInt(1, 100);

        saveTodoPort.saveAll(TodoFixture.createMultipleTodosWithGoal(goal, size));
      }

      @Test
      void 이번_달에_데이터가_있으면_요일통계를_정상_반환한다() {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            thisMonth
        );

        // then
        DayOfWeekStatsDto actual = response.dayOfWeekStats();

        assertThat(actual.stats()).hasSize(DayOfWeek.values().length);
        assertThat(actual.mostActiveDays()).isNotNull();
      }

      @Test
      void 조회_대상_월에_데이터가_없으면_요일맵은_모두_0이고_mostActiveDays는_빈_목록이다() {
        // given
        YearMonth nextMonth = thisMonth.plusMonths(1);

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            nextMonth,
            nextMonth
        );

        // then
        DayOfWeekStatsDto actual = response.dayOfWeekStats();

        assertThat(actual.stats()
            .values()).allMatch(count -> Objects.equals(count, 0));
        assertThat(actual.mostActiveDays()).isEmpty();
      }

    }

    @Nested
    class 반복투두통계_테스트 {

      int repeatTodoSize;
      Map<Long, Integer> repeatTodos;

      @BeforeEach
      void setUpRepeat() {
        repeatTodoSize = MonthlyStatsFixture.getRandomInt(1, 10);
        repeatTodos = new HashMap<>();
        LocalDate from = thisMonth.atDay(1);
        LocalDate to = thisMonth.atEndOfMonth();

        for (int i = 0; i < repeatTodoSize; i++) {
          RepeatTodo repeatTodo = saveRepeatTodoPort.save(
              RepeatTodoFixture.createRepeatTodoWithGoal(goal, from, to)
          );
          List<Todo> ddudus = saveTodoPort.saveAll(repeatTodoDomainService.createRepeatedTodos(
              user.getId(),
              repeatTodo
          ));

          repeatTodos.put(repeatTodo.getId(), ddudus.size());
        }
      }

      @Test
      void 이번_달_반복투두_통계를_반환한다() {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            thisMonth
        );

        // then
        List<RepeatTodoStatsDto> actual = response.repeatTodoStats();

        assertThat(actual).hasSize(repeatTodoSize);
        assertThat(actual).allMatch(stats -> repeatTodos.containsKey(stats.repeatTodoId())
            && stats.totalCount() == repeatTodos.get(stats.repeatTodoId()));
      }

      @Test
      void 이번_달에_데이터가_없으면_빈_리스트를_반환한다() {
        // given
        YearMonth nextMonth = thisMonth.plusMonths(1);

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            nextMonth,
            nextMonth
        );

        // then
        assertThat(response.repeatTodoStats()).isEmpty();
      }

    }

    @Nested
    class 달력통계_테스트 {

      YearMonth nextMonth;
      int size;

      @BeforeEach
      void setUpCalendar() {
        nextMonth = thisMonth.plusMonths(1);
        size = TodoFixture.getRandomInt(1, 100);

        saveTodoPort.saveAll(TodoFixture.createMultipleTodosWithGoal(goal, size));
      }

      @Test
      void 단일월_요청이면_달력통계조회가_가능하다() {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            thisMonth
        );

        // then
        GenericCalendarStats<TodoCompletionResponse> actual = response.calendarStats();

        assertThat(actual.isAvailable()).isTrue();
        assertThat(actual.stats()).isNotNull();
      }

      @Test
      void 단일월_요청이_아니면_달력통계가_불가능하다() {
        // given

        // when
        AchievedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectAchievedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            nextMonth
        );

        // then
        GenericCalendarStats<TodoCompletionResponse> actual = response.calendarStats();

        assertThat(actual.isAvailable()).isFalse();
        assertThat(actual.stats()).isEmpty();
      }

    }

  }

  @Nested
  class 미루기_상세_통계_테스트 {

    @Test
    void 미루기_상세_응답에_목표_색상이_포함된다() {
      // given

      // when
      PostponedStatsDetailResponse response =
          collectMonthlyAchievedDetailService.collectPostponedDetail(
          user.getId(),
          goal.getId(),
          thisMonth,
          thisMonth
      );

      // then
      assertThat(response.goalColor()).isEqualTo(goal.getColor());
    }

    @Nested
    class 오버뷰_테스트 {

      int totalCount;
      int totalPostponed;
      int totalNotPostponed;
      int reattained;

      @BeforeEach
      void setUp() {
        totalCount = MonthlyStatsFixture.getRandomInt(1, 50);
        totalPostponed = MonthlyStatsFixture.getRandomInt(1, totalCount);
        totalNotPostponed = totalCount - totalPostponed;
        reattained = MonthlyStatsFixture.getRandomInt(0, totalPostponed);
        List<Todo> postponed = TodoFixture.createReattainedTodos(
            goal,
            reattained,
            totalPostponed
        );
        List<Todo> notPostponed = TodoFixture.createTodosWithPostponedFlag(
            goal,
            0,
            totalNotPostponed
        );

        saveTodoPort.saveAll(postponed);
        saveTodoPort.saveAll(notPostponed);
      }

      @Test
      void 이번_달에_데이터가_있으면_미루기_오버뷰가_정상_반환된다() {
        // given

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
                user.getId(),
                goal.getId(),
                thisMonth,
                thisMonth
            );

        // then
        PostponedDetailOverviewDto actual = response.overview();
        assertThat(actual.totalCount()).isEqualTo(totalPostponed);
        assertThat(actual.postponedCount()).isEqualTo(totalPostponed);
        assertThat(actual.reattainedCount()).isEqualTo(reattained);
        assertThat(actual.postponementRate()).isBetween(0, 100);
        assertThat(actual.reattainmentRate()).isBetween(0, 100);
      }

      @Test
      void 조회_대상_월에_데이터가_없으면_모든_수치는_0이다() {
        // given
        YearMonth nextMonth = thisMonth.plusMonths(1);

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            nextMonth,
            nextMonth
        );

        // then
        PostponedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isZero();
        assertThat(actual.postponedCount()).isZero();
        assertThat(actual.reattainedCount()).isZero();
        assertThat(actual.postponementRate()).isZero();
        assertThat(actual.reattainmentRate()).isZero();
      }

      @ParameterizedTest
      @NullSource
      void 시작_월이_null이면_이번달로_대체되어_정상_반환된다(YearMonth nullMonth) {
        // given

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            nullMonth,
            thisMonth
        );

        // then
        PostponedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isEqualTo(totalPostponed);
        assertThat(actual.postponedCount()).isEqualTo(totalPostponed);
        assertThat(actual.reattainedCount()).isEqualTo(reattained);
      }

      @ParameterizedTest
      @NullSource
      void 종료_월이_null이면_이번달로_대체되어_정상_반환된다(YearMonth nullMonth) {
        // given

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            nullMonth
        );

        // then
        PostponedDetailOverviewDto actual = response.overview();

        assertThat(actual.totalCount()).isEqualTo(totalPostponed);
        assertThat(actual.postponedCount()).isEqualTo(totalPostponed);
        assertThat(actual.reattainedCount()).isEqualTo(reattained);
      }

      @Test
      void 미루기_상세_집계_대상은_postponedAt_날짜_기준이다() {
        // given
        YearMonth nextMonth = thisMonth.plusMonths(1);
        LocalDate inRangeDate = nextMonth.atDay(10);
        LocalDate outRangeDate = thisMonth.atDay(10);

        Todo included = TodoFixture.getTodoBuilder()
            .goalId(goal.getId())
            .userId(user.getId())
            .scheduledOn(outRangeDate)
            .postponedAt(inRangeDate.atStartOfDay())
            .build();
        Todo excluded = TodoFixture.getTodoBuilder()
            .goalId(goal.getId())
            .userId(user.getId())
            .scheduledOn(inRangeDate)
            .postponedAt(LocalDateTime.of(outRangeDate, LocalDateTime.now().toLocalTime()))
            .build();

        saveTodoPort.save(included);
        saveTodoPort.save(excluded);

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
                user.getId(),
                goal.getId(),
                nextMonth,
                nextMonth
            );

        // then
        PostponedDetailOverviewDto actual = response.overview();
        assertThat(actual.totalCount()).isEqualTo(1);
        assertThat(actual.postponedCount()).isEqualTo(1);
      }

    }

    @Nested
    class 요일통계_테스트 {

      int size;

      @BeforeEach
      void setUp() {
        size = TodoFixture.getRandomInt(1, 100);
        int reattained = TodoFixture.getRandomInt(0, size);

        saveTodoPort.saveAll(TodoFixture.createReattainedTodos(goal, reattained, size));
      }

      @Test
      void 이번_달에_데이터가_있으면_요일통계를_정상_반환한다() {
        // given

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            thisMonth
        );

        // then
        DayOfWeekStatsDto actual = response.dayOfWeekStats();

        assertThat(actual.stats()).hasSize(DayOfWeek.values().length);
        assertThat(actual.mostActiveDays()).isNotNull();
      }

      @Test
      void 조회_대상_월에_데이터가_없으면_요일맵은_모두_0이고_mostActiveDays는_빈_목록이다() {
        // given
        YearMonth nextMonth = thisMonth.plusMonths(1);

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            nextMonth,
            nextMonth
        );

        // then
        DayOfWeekStatsDto actual = response.dayOfWeekStats();

        assertThat(actual.stats()
            .values()).allMatch(v -> Objects.equals(v, 0));
        assertThat(actual.mostActiveDays()).isEmpty();
      }

    }

    @Nested
    class 달력통계_테스트 {

      YearMonth nextMonth;
      int size;

      @BeforeEach
      void setUp() {
        nextMonth = thisMonth.plusMonths(1);
        size = TodoFixture.getRandomInt(1, 50);
        int reattained = TodoFixture.getRandomInt(0, size);

        saveTodoPort.saveAll(TodoFixture.createReattainedTodos(goal, reattained, size));
      }

      @Test
      void 단일월_요청이면_달력통계조회가_가능하다() {
        // given

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            thisMonth
        );

        // then
        GenericCalendarStats<TodoCompletionResponse> actual = response.calendarStats();

        assertThat(actual.isAvailable()).isTrue();
        assertThat(actual.stats()).isNotNull();
      }

      @Test
      void 단일월_요청이_아니면_달력통계가_불가능하다() {
        // given

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            thisMonth,
            nextMonth
        );

        // then
        GenericCalendarStats<TodoCompletionResponse> actual = response.calendarStats();

        assertThat(actual.isAvailable()).isFalse();
        assertThat(actual.stats()).isEmpty();
      }

      @Test
      void 단일월이지만_데이터가_없으면_Available_이면서_items는_빈_리스트다() {
        // given
        YearMonth another = thisMonth.plusMonths(2);

        // when
        PostponedStatsDetailResponse response =
            collectMonthlyAchievedDetailService.collectPostponedDetail(
            user.getId(),
            goal.getId(),
            another,
            another
        );

        // then
        GenericCalendarStats<TodoCompletionResponse> actual = response.calendarStats();

        assertThat(actual.isAvailable()).isTrue();
        assertThat(actual.stats()).isEmpty();
      }

    }

  }

  @Test
  void 시작_월은_종료_월보다_클_수_없다() {
    // given
    YearMonth nextMonth = thisMonth.minusMonths(1);

    // when
    ThrowingCallable collectDetail =
        () -> collectMonthlyAchievedDetailService.collectAchievedDetail(
        user.getId(),
        goal.getId(),
        thisMonth,
        nextMonth
    );

    // then
    assertThatIllegalArgumentException().isThrownBy(collectDetail)
        .withMessage(StatsErrorCode.INVALID_TO_MONTH.getCodeName());
  }

  @ParameterizedTest
  @NullSource
  void 목표_아이디는_null이_될_수_없다(Long goalId) {
    // given

    // when
    ThrowingCallable collectDetail =
        () -> collectMonthlyAchievedDetailService.collectAchievedDetail(
        user.getId(),
        goalId,
        thisMonth,
        thisMonth
    );

    // then
    assertThatIllegalArgumentException().isThrownBy(collectDetail)
        .withMessage(StatsErrorCode.NULL_GOAL_ID.getCodeName());
  }

  @Test
  void 로그인_사용자가_없으면_예외를_반환한다() {
    // given
    long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable collectDetail =
        () -> collectMonthlyAchievedDetailService.collectAchievedDetail(
        invalidUserId,
        goal.getId(),
        thisMonth,
        thisMonth
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(collectDetail)
        .withMessage(StatsErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 목표가_존재하지_않으면_예외를_반환한다() {
    // given
    long goalId = GoalFixture.getRandomId();

    // when
    ThrowingCallable collectDetail =
        () -> collectMonthlyAchievedDetailService.collectAchievedDetail(
        user.getId(),
        goalId,
        thisMonth,
        thisMonth
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(collectDetail)
        .withMessage(StatsErrorCode.GOAL_NOT_EXISTING.getCodeName());
  }

}
