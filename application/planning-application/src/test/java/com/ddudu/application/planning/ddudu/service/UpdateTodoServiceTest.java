package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.todo.request.UpdateTodoReminderRequest;
import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
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
class UpdateTodoServiceTest {

  @Autowired
  UpdateTodoService updateTodoService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  ReminderCommandPort reminderCommandPort;

  @Autowired
  ReminderLoaderPort reminderLoaderPort;

  User user;
  Goal goal;
  Todo todo;
  UpdateTodoRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    LocalDate scheduledOn = LocalDate.now().plusDays(1);
    todo = saveTodoPort.save(
        TodoFixture.getTodoBuilder()
            .goalId(goal.getId())
            .userId(user.getId())
            .scheduledOn(scheduledOn)
            .beginAt(LocalTime.of(10, 0))
            .endAt(LocalTime.of(11, 0))
            .build()
    );
    request = new UpdateTodoRequest(
        goal.getId(),
        TodoFixture.getRandomSentenceWithMax(50),
        TodoFixture.createValidMemo(),
        scheduledOn,
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        null
    );
  }

  @Test
  void 투두를_수정한다() {
    // given

    // when
    BasicTodoResponse actual = updateTodoService.update(
        user.getId(),
        todo.getId(),
        request
    );

    // then
    assertThat(actual.id()).isEqualTo(todo.getId());
    assertThat(actual.name()).isEqualTo(request.name());
  }

  @Test
  void 로그인_사용자가_없으면_수정에_실패한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        invalidUserId,
        todo.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_투두면_수정에_실패한다() {
    // given
    Long invalidTodoId = TodoFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        user.getId(),
        invalidTodoId,
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 작성자가_아니면_수정에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        anotherUser.getId(),
        todo.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(SecurityException.class)
        .hasMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  @Test
  void 존재하지_않는_목표면_수정에_실패한다() {
    // given
    UpdateTodoRequest invalidRequest = new UpdateTodoRequest(
        GoalFixture.getRandomId(),
        request.name(),
        request.memo(),
        request.scheduledOn(),
        request.beginAt(),
        request.endAt(),
        request.reminders()
    );

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        user.getId(),
        todo.getId(),
        invalidRequest
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(TodoErrorCode.GOAL_NOT_EXISTING.getCodeName());
  }

  @Test
  void 리마인더를_추가하면_저장된다() {
    // given
    LocalDateTime remindAt = LocalDateTime.of(
        request.scheduledOn(),
        request.beginAt()
    ).minusHours(1);
    UpdateTodoRequest requestWithReminder = new UpdateTodoRequest(
        request.goalId(),
        request.name(),
        request.memo(),
        request.scheduledOn(),
        request.beginAt(),
        request.endAt(),
        List.of(new UpdateTodoReminderRequest(null, remindAt))
    );

    // when
    updateTodoService.update(user.getId(), todo.getId(), requestWithReminder);

    // then
    List<Reminder> reminders = reminderLoaderPort.getRemindersByTodoId(todo.getId());
    assertThat(reminders).hasSize(1);
    assertThat(reminders.get(0).getRemindsAt()).isEqualTo(remindAt);
  }

  @Test
  void 목표_작성자가_아니면_수정에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal anotherUserGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUser(anotherUser.getId())
    );
    UpdateTodoRequest requestWithAnotherUsersGoal = new UpdateTodoRequest(
        anotherUserGoal.getId(),
        request.name(),
        request.memo(),
        request.scheduledOn(),
        request.beginAt(),
        request.endAt(),
        request.reminders()
    );

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        user.getId(),
        todo.getId(),
        requestWithAnotherUsersGoal
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(SecurityException.class)
        .hasMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  @Test
  void 기존_리마인더가_있으면_리마인더를_수정한다() {
    // given
    LocalDateTime existingRemindAt = LocalDateTime.of(
        request.scheduledOn(),
        request.beginAt()
    ).minusHours(2);
    Reminder savedReminder = reminderCommandPort.save(Reminder.from(
        user.getId(),
        todo.getId(),
        existingRemindAt,
        todo.getScheduleDatetime()
    ));
    LocalDateTime updatedRemindAt = LocalDateTime.of(
        request.scheduledOn(),
        request.beginAt()
    ).minusHours(1);
    UpdateTodoRequest requestWithExistingReminder = new UpdateTodoRequest(
        request.goalId(),
        request.name(),
        request.memo(),
        request.scheduledOn(),
        request.beginAt(),
        request.endAt(),
        List.of(new UpdateTodoReminderRequest(savedReminder.getId(), updatedRemindAt))
    );

    // when
    updateTodoService.update(user.getId(), todo.getId(), requestWithExistingReminder);

    // then
    List<Reminder> reminders = reminderLoaderPort.getRemindersByTodoId(todo.getId());
    assertThat(reminders).hasSize(1);
    assertThat(reminders.get(0).getId()).isEqualTo(savedReminder.getId());
    assertThat(reminders.get(0).getRemindsAt()).isEqualTo(updatedRemindAt);
  }

  @Test
  void 요청에서_제외된_기존_리마인더는_삭제한다() {
    // given
    LocalDateTime firstRemindAt = LocalDateTime.of(
        request.scheduledOn(),
        request.beginAt()
    ).minusHours(2);
    reminderCommandPort.save(Reminder.from(
        user.getId(),
        todo.getId(),
        firstRemindAt,
        todo.getScheduleDatetime()
    ));
    LocalDateTime secondRemindAt = LocalDateTime.of(
        request.scheduledOn(),
        request.beginAt()
    ).minusMinutes(30);
    Reminder secondReminder = reminderCommandPort.save(Reminder.from(
        user.getId(),
        todo.getId(),
        secondRemindAt,
        todo.getScheduleDatetime()
    ));
    UpdateTodoRequest requestKeepingOnlySecondReminder = new UpdateTodoRequest(
        request.goalId(),
        request.name(),
        request.memo(),
        request.scheduledOn(),
        request.beginAt(),
        request.endAt(),
        List.of(new UpdateTodoReminderRequest(secondReminder.getId(), secondRemindAt))
    );

    // when
    updateTodoService.update(user.getId(), todo.getId(), requestKeepingOnlySecondReminder);

    // then
    List<Reminder> reminders = reminderLoaderPort.getRemindersByTodoId(todo.getId());
    assertThat(reminders).hasSize(1);
    assertThat(reminders.get(0).getId()).isEqualTo(secondReminder.getId());
  }

}
