package com.modoo.application.planning.reminder.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.reminder.response.RetrieveReminderResponse;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.reminder.out.ReminderCommandPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.common.exception.ReminderErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class RetrieveRemindersServiceTest {

  @Autowired
  RetrieveRemindersService retrieveRemindersService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  ReminderCommandPort reminderCommandPort;

  User user;
  Goal goal;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));

    Todo temp = TodoFixture.createRandomTodoWithGoalAndTime(goal, LocalTime.of(10, 0), null);
    todo = saveTodoPort.save(temp.moveDate(LocalDate.now()
        .plusDays(1)));
  }

  @Test
  void 미리알림_조회에_성공한다() {
    // given
    LocalDateTime scheduledAt = todo.getScheduledOn()
        .atTime(todo.getBeginAt());
    Reminder first = reminderCommandPort.save(
        Reminder.from(user.getId(), todo.getId(), scheduledAt.minusMinutes(10), scheduledAt)
    );
    Reminder second = reminderCommandPort.save(
        Reminder.from(
            user.getId(),
            todo.getId(),
            scheduledAt.minusMinutes(5),
            scheduledAt.plusMinutes(1)
        )
    );

    // when
    List<RetrieveReminderResponse> responses =
        retrieveRemindersService.retrieve(user.getId(), todo.getId());

    // then
    assertThat(responses).hasSize(2);
    assertThat(responses)
        .extracting(RetrieveReminderResponse::id)
        .containsExactly(first.getId(), second.getId());
  }

  @Test
  void 로그인_사용자가_없으면_실패한다() {
    // given
    Long invalidLoginId = TodoFixture.getRandomId();

    // when
    ThrowingCallable retrieve =
        () -> retrieveRemindersService.retrieve(invalidLoginId, todo.getId());

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(retrieve)
        .withMessage(ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 투두가_없으면_실패한다() {
    // given
    Long invalidTodoId = TodoFixture.getRandomId();

    // when
    ThrowingCallable retrieve =
        () -> retrieveRemindersService.retrieve(user.getId(), invalidTodoId);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(retrieve)
        .withMessage(ReminderErrorCode.TODO_NOT_EXISTING.getCodeName());
  }

  @Test
  void 다른_사용자의_투두를_조회하면_실패한다() {
    // given
    User otherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable retrieve =
        () -> retrieveRemindersService.retrieve(otherUser.getId(), todo.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(retrieve)
        .withMessage(ReminderErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
