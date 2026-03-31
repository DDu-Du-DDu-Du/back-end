package com.ddudu.application.planning.reminder.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.response.CreateReminderResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@RecordApplicationEvents
@DisplayNameGeneration(ReplaceUnderscores.class)
class CreateReminderServiceTest {

  @Autowired
  CreateReminderService createReminderService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  ReminderLoaderPort reminderLoaderPort;

  User user;
  Goal goal;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));

    Todo temp = TodoFixture.createRandomTodoWithGoalAndTime(goal, LocalTime.of(10, 0), null);
    todo = saveTodoPort.save(temp.moveDate(LocalDate.now().plusDays(1)));
  }

  @Test
  void Reminder_생성_유즈케이스에_성공한다() {
    // given
    LocalDateTime remindsAt = todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(10);
    CreateReminderRequest request = new CreateReminderRequest(todo.getId(), remindsAt);

    // when
    CreateReminderResponse response = createReminderService.create(user.getId(), request);

    // then
    assertThat(response.id()).isNotNull();
    assertThat(reminderLoaderPort.getOptionalReminder(response.id())).isPresent();
    assertThat(reminderLoaderPort.getOptionalReminder(response.id()).orElseThrow().getTodoId())
        .isEqualTo(todo.getId());
  }

  @Test
  void 로그인_사용자가_없으면_실패한다() {
    // given
    Long invalidLoginId = TodoFixture.getRandomId();
    LocalDateTime remindsAt = todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(10);
    CreateReminderRequest request = new CreateReminderRequest(todo.getId(), remindsAt);

    // when
    ThrowingCallable create = () -> createReminderService.create(invalidLoginId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void Reminder_생성_후_이벤트를_발행한다(ApplicationEvents events) {
    // given
    LocalDateTime remindsAt = todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(10);
    CreateReminderRequest request = new CreateReminderRequest(todo.getId(), remindsAt);

    // when
    createReminderService.create(user.getId(), request);

    // then
    assertThat(events.stream(InterimSetReminderEvent.class))
        .hasSize(1);
  }

  @Test
  void 투두가_없으면_실패한다() {
    // given
    Long invalidTodoId = TodoFixture.getRandomId();
    CreateReminderRequest request = new CreateReminderRequest(
        invalidTodoId,
        LocalDateTime.now().plusDays(1)
    );

    // when
    ThrowingCallable create = () -> createReminderService.create(user.getId(), request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.TODO_NOT_EXISTING.getCodeName());
  }

}
