package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.ddudu.application.common.dto.todo.request.UpdateTodoReminderRequest;
import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.ddudu.domain.planning.todo.service.TodoDomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UpdateTodoServicePortExceptionTest {

  @Mock
  UserLoaderPort userLoaderPort;

  @Mock
  GoalLoaderPort goalLoaderPort;

  @Mock
  TodoLoaderPort todoLoaderPort;

  @Mock
  TodoUpdatePort todoUpdatePort;

  @Mock
  ReminderLoaderPort reminderLoaderPort;

  @Mock
  ReminderCommandPort reminderCommandPort;

  @Mock
  TodoDomainService todoDomainService;

  @Mock
  ApplicationEventPublisher applicationEventPublisher;

  UpdateTodoService updateTodoService;

  User user;
  Goal goal;
  Long goalId;
  Todo todo;

  @BeforeEach
  void setUp() {
    updateTodoService = new UpdateTodoService(
        userLoaderPort,
        goalLoaderPort,
        todoLoaderPort,
        todoUpdatePort,
        reminderLoaderPort,
        reminderCommandPort,
        todoDomainService,
        applicationEventPublisher
    );

    user = UserFixture.createRandomUserWithId();
    goal = GoalFixture.createRandomGoalWithUser(user.getId());
    goalId = GoalFixture.getRandomId();
    todo = Todo.builder()
        .id(TodoFixture.getRandomId())
        .goalId(goalId)
        .userId(user.getId())
        .name("테스트 투두")
        .status(TodoStatus.UNCOMPLETED)
        .scheduledOn(LocalDate.now().plusDays(1))
        .beginAt(LocalTime.of(10, 0))
        .endAt(LocalTime.of(11, 0))
        .build();

    when(userLoaderPort.getUserOrElseThrow(eq(user.getId()), any())).thenReturn(user);
    when(todoLoaderPort.getTodoOrElseThrow(eq(todo.getId()), any())).thenReturn(todo);
    when(goalLoaderPort.getGoalOrElseThrow(eq(goalId), any())).thenReturn(goal);
    when(todoDomainService.update(eq(todo), any())).thenReturn(todo);
    when(todoUpdatePort.update(todo)).thenReturn(todo);
  }

  @Test
  void 신규_리마인더_저장_중_포트_예외가_발생하면_수정에_실패한다() {
    // given
    UpdateTodoRequest request = createRequest(List.of(
        new UpdateTodoReminderRequest(null, LocalDateTime.now().plusHours(1))
    ));
    when(reminderLoaderPort.getRemindersByTodoId(todo.getId())).thenReturn(List.of());
    when(reminderCommandPort.save(any()))
        .thenThrow(new IllegalStateException("REMINDER_SAVE_FAILED"));

    // when
    ThrowingCallable update = () -> updateTodoService.update(user.getId(), todo.getId(), request);

    // then
    assertThatThrownBy(update)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("REMINDER_SAVE_FAILED");
  }

  @Test
  void 기존_리마인더_수정_중_포트_예외가_발생하면_수정에_실패한다() {
    // given
    Reminder existingReminder = Reminder.builder()
        .id(1L)
        .userId(user.getId())
        .todoId(todo.getId())
        .remindsAt(LocalDateTime.now().plusMinutes(30))
        .build();
    UpdateTodoRequest request = createRequest(List.of(
        new UpdateTodoReminderRequest(existingReminder.getId(), LocalDateTime.now().plusHours(1))
    ));
    when(reminderLoaderPort.getRemindersByTodoId(todo.getId()))
        .thenReturn(List.of(existingReminder));
    when(reminderCommandPort.update(any()))
        .thenThrow(new IllegalStateException("REMINDER_UPDATE_FAILED"));

    // when
    ThrowingCallable update = () -> updateTodoService.update(user.getId(), todo.getId(), request);

    // then
    assertThatThrownBy(update)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("REMINDER_UPDATE_FAILED");
  }

  @Test
  void 리마인더_삭제_중_포트_예외가_발생하면_수정에_실패한다() {
    // given
    Reminder existingReminder = Reminder.builder()
        .id(2L)
        .userId(user.getId())
        .todoId(todo.getId())
        .remindsAt(LocalDateTime.now().plusMinutes(30))
        .build();
    UpdateTodoRequest request = createRequest(List.of());
    when(reminderLoaderPort.getRemindersByTodoId(todo.getId()))
        .thenReturn(List.of(existingReminder));
    doThrow(new IllegalStateException("REMINDER_DELETE_FAILED"))
        .when(reminderCommandPort)
        .deleteById(existingReminder.getId());

    // when
    ThrowingCallable update = () -> updateTodoService.update(user.getId(), todo.getId(), request);

    // then
    assertThatThrownBy(update)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("REMINDER_DELETE_FAILED");
  }

  @Test
  void 리마인더_조회_중_포트_예외가_발생하면_수정에_실패한다() {
    // given
    UpdateTodoRequest request = createRequest(List.of());
    when(reminderLoaderPort.getRemindersByTodoId(todo.getId()))
        .thenThrow(new IllegalStateException("REMINDER_LOAD_FAILED"));

    // when
    ThrowingCallable update = () -> updateTodoService.update(user.getId(), todo.getId(), request);

    // then
    assertThatThrownBy(update)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("REMINDER_LOAD_FAILED");
  }

  private UpdateTodoRequest createRequest(List<UpdateTodoReminderRequest> reminders) {
    return new UpdateTodoRequest(
        goalId,
        TodoFixture.getRandomSentenceWithMax(50),
        TodoFixture.createValidMemo(),
        LocalDate.now().plusDays(1),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        reminders
    );
  }

}
