package com.ddudu.fixture;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.Todo.TodoBuilder;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoFixture extends BaseFixture {

  public static String createValidMemo() {
    return getRandomSentenceWithMax(2000);
  }

  public static String createOverLengthMemo() {
    return getRandomSentence(2001, 2200);
  }

  public static List<Todo> createReattainedTodos(
      Goal goal,
      int reattainedCount,
      int totalPostponedCount
  ) {
    List<Todo> ddudus = new ArrayList<>();

    for (int i = 0; i < reattainedCount; i++) {
      ddudus.add(createRandomTodoWithReference(
          goal.getId(),
          goal.getUserId(),
          true,
          TodoStatus.COMPLETE
      ));
    }

    for (int i = reattainedCount; i < totalPostponedCount; i++) {
      ddudus.add(createRandomTodoWithReference(
          goal.getId(),
          goal.getUserId(),
          true,
          TodoStatus.UNCOMPLETED
      ));
    }

    return ddudus;
  }

  public static List<Todo> createTodosWithPostponedFlag(
      Goal goal,
      int postponedCount,
      int notPostponedCount
  ) {
    List<Todo> ddudus = new ArrayList<>();

    for (int i = 0; i < postponedCount; i++) {
      LocalDate scheduledOn = YearMonth.now()
          .atDay(getRandomInt(
              1,
              YearMonth.now()
                  .lengthOfMonth()
          ));

      ddudus.add(createTodoWithScheduleAndPostponedFlag(goal, true, scheduledOn));
    }

    for (int i = 0; i < notPostponedCount; i++) {
      LocalDate scheduledOn = YearMonth.now()
          .atDay(getRandomInt(
              1,
              YearMonth.now()
                  .lengthOfMonth()
          ));

      ddudus.add(createTodoWithScheduleAndPostponedFlag(goal, false, scheduledOn));
    }

    return ddudus;
  }

  public static List<Todo> createConsecutiveCompletedTodos(Goal goal, int count) {
    LocalDate firstDate = YearMonth.now()
        .atDay(1);
    List<Todo> ddudus = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      LocalDate scheduled = firstDate.plusDays(i);
      ddudus.add(createRandomTodoWithStatusAndSchedule(goal, TodoStatus.COMPLETE, scheduled));
    }

    return ddudus;
  }

  public static List<Todo> createDifferentTodosWithGoal(
      Goal goal,
      int completedCount,
      int uncompletedCount
  ) {
    List<Todo> ddudus = new ArrayList<>();

    ddudus.addAll(createMultipleTodosWithGoal(goal, completedCount));
    ddudus.addAll(createMultipleTodosWithGoal(goal, uncompletedCount, TodoStatus.UNCOMPLETED));

    return ddudus;
  }

  public static List<Todo> createMultipleTodosWithGoal(Goal goal, int size) {
    return createMultipleTodosWithGoal(goal, size, TodoStatus.COMPLETE);
  }

  private static List<Todo> createMultipleTodosWithGoal(Goal goal, int size, TodoStatus status) {
    List<Todo> ddudus = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      ddudus.add(createRandomTodoWithStatus(goal, status));
    }

    return ddudus;
  }

  public static Todo createTodoWithScheduleAndPostponedFlag(
      Goal goal,
      boolean isPostponed,
      LocalDate scheduledOn
  ) {
    return getTodoBuilder()
        .userId(goal.getUserId())
        .goalId(goal.getId())
        .isPostponed(isPostponed)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Todo createRandomTodoWithStatus(Goal goal, TodoStatus status) {
    return getTodoBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .status(status)
        .build();
  }

  public static Todo createRandomTodoWithGoal(Goal goal) {
    return getTodoBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .build();
  }

  public static Todo createRandomTodoWithReference(
      Long goalId,
      Long userId,
      Boolean isPostponed,
      TodoStatus status
  ) {
    return getTodoBuilder()
        .goalId(goalId)
        .userId(userId)
        .isPostponed(isPostponed)
        .status(status)
        .build();
  }

  public static Todo createRandomTodoWithStatusAndSchedule(
      Goal goal,
      TodoStatus status,
      LocalDate scheduledOn
  ) {
    return getTodoBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .status(status)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Todo createRandomTodoWithSchedule(
      Long userId,
      Long goalId,
      LocalDate scheduledOn
  ) {
    return getTodoBuilder()
        .goalId(goalId)
        .userId(userId)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Todo createTodoWithReminder(
      Long userId,
      Long goalId,
      LocalDate scheduleOn,
      LocalTime beginAt,
      LocalDateTime remindAt
  ) {
    return getTodoBuilder()
        .userId(userId)
        .goalId(goalId)
        .scheduledOn(scheduleOn)
        .beginAt(beginAt)
        .build();
  }

  public static Todo createTodoWithReminderFor(Long userId, Long goalId, LocalDateTime remindAt) {
    LocalDateTime scheduledAt = remindAt.plusSeconds(1);

    return createTodoWithReminder(
        userId,
        goalId,
        scheduledAt.toLocalDate(),
        scheduledAt.toLocalTime(),
        remindAt
    );
  }

  public static Todo createRandomTodoWithGoalAndTime(
      Goal goal,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    return getTodoBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public static Todo createRandomTodoWithRepeatTodo(Long userId, RepeatTodo repeatTodo) {
    return getTodoBuilder()
        .goalId(repeatTodo.getGoalId())
        .userId(userId)
        .repeatTodoId(repeatTodo.getId())
        .build();
  }

  public static TodoBuilder getTodoBuilder() {
    return Todo.builder()
        .id(getRandomId())
        .name(getRandomSentenceWithMax(50))
        .scheduledOn(LocalDate.now());
  }

}
