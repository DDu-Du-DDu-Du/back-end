package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.ddudu.application.common.dto.todo.request.UpdateTodoReminderRequest;
import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.exception.TodoErrorCode;
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
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
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

  User user;
  Goal goal;
  Todo todo;
  UpdateTodoRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
    request = new UpdateTodoRequest(
        goal.getId(),
        TodoFixture.getRandomSentenceWithMax(50),
        TodoFixture.createValidMemo(),
        LocalDate.now().plusDays(1),
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

  @Nested
  @ExtendWith(MockitoExtension.class)
  class 신규_리마인더_포트_예외_테스트 {

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

    UpdateTodoService isolatedUpdateTodoService;

    User isolatedUser;
    Goal isolatedGoal;
    Long isolatedGoalId;
    Todo isolatedTodo;

    @BeforeEach
    void setUp() {
      isolatedUpdateTodoService = new UpdateTodoService(
          userLoaderPort,
          goalLoaderPort,
          todoLoaderPort,
          todoUpdatePort,
          reminderLoaderPort,
          reminderCommandPort,
          todoDomainService,
          applicationEventPublisher
      );

      isolatedUser = UserFixture.createRandomUserWithId();
      isolatedGoal = GoalFixture.createRandomGoalWithUser(isolatedUser.getId());
      isolatedGoalId = GoalFixture.getRandomId();
      isolatedTodo = Todo.builder()
          .id(TodoFixture.getRandomId())
          .goalId(isolatedGoalId)
          .userId(isolatedUser.getId())
          .name("테스트 투두")
          .status(TodoStatus.UNCOMPLETED)
          .scheduledOn(LocalDate.now().plusDays(1))
          .beginAt(LocalTime.of(10, 0))
          .endAt(LocalTime.of(11, 0))
          .build();

      when(userLoaderPort.getUserOrElseThrow(eq(isolatedUser.getId()), any()))
          .thenReturn(isolatedUser);
      when(todoLoaderPort.getTodoOrElseThrow(eq(isolatedTodo.getId()), any()))
          .thenReturn(isolatedTodo);
      when(goalLoaderPort.getGoalOrElseThrow(eq(isolatedGoalId), any())).thenReturn(isolatedGoal);
      when(todoDomainService.update(eq(isolatedTodo), any())).thenReturn(isolatedTodo);
      when(todoUpdatePort.update(isolatedTodo)).thenReturn(isolatedTodo);
    }

    @Test
    void 신규_리마인더_저장_중_포트_예외가_발생하면_수정에_실패한다() {
      // given
      UpdateTodoRequest request = createRequest(List.of(
          new UpdateTodoReminderRequest(null, LocalDateTime.now().plusHours(1))
      ));
      when(reminderLoaderPort.getRemindersByTodoId(isolatedTodo.getId())).thenReturn(List.of());
      when(reminderCommandPort.save(any()))
          .thenThrow(new IllegalStateException("REMINDER_SAVE_FAILED"));

      // when
      ThrowingCallable update = () -> isolatedUpdateTodoService.update(
          isolatedUser.getId(),
          isolatedTodo.getId(),
          request
      );

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
          .userId(isolatedUser.getId())
          .todoId(isolatedTodo.getId())
          .remindsAt(LocalDateTime.now().plusMinutes(30))
          .build();
      UpdateTodoRequest request = createRequest(List.of(
          new UpdateTodoReminderRequest(existingReminder.getId(), LocalDateTime.now().plusHours(1))
      ));
      when(reminderLoaderPort.getRemindersByTodoId(isolatedTodo.getId()))
          .thenReturn(List.of(existingReminder));
      when(reminderCommandPort.update(any()))
          .thenThrow(new IllegalStateException("REMINDER_UPDATE_FAILED"));

      // when
      ThrowingCallable update = () -> isolatedUpdateTodoService.update(
          isolatedUser.getId(),
          isolatedTodo.getId(),
          request
      );

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
          .userId(isolatedUser.getId())
          .todoId(isolatedTodo.getId())
          .remindsAt(LocalDateTime.now().plusMinutes(30))
          .build();
      UpdateTodoRequest request = createRequest(List.of());
      when(reminderLoaderPort.getRemindersByTodoId(isolatedTodo.getId()))
          .thenReturn(List.of(existingReminder));
      doThrow(new IllegalStateException("REMINDER_DELETE_FAILED"))
          .when(reminderCommandPort)
          .deleteById(existingReminder.getId());

      // when
      ThrowingCallable update = () -> isolatedUpdateTodoService.update(
          isolatedUser.getId(),
          isolatedTodo.getId(),
          request
      );

      // then
      assertThatThrownBy(update)
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("REMINDER_DELETE_FAILED");
    }

    @Test
    void 리마인더_조회_중_포트_예외가_발생하면_수정에_실패한다() {
      // given
      UpdateTodoRequest request = createRequest(List.of());
      when(reminderLoaderPort.getRemindersByTodoId(isolatedTodo.getId()))
          .thenThrow(new IllegalStateException("REMINDER_LOAD_FAILED"));

      // when
      ThrowingCallable update = () -> isolatedUpdateTodoService.update(
          isolatedUser.getId(),
          isolatedTodo.getId(),
          request
      );

      // then
      assertThatThrownBy(update)
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("REMINDER_LOAD_FAILED");
    }

    private UpdateTodoRequest createRequest(List<UpdateTodoReminderRequest> reminders) {
      return new UpdateTodoRequest(
          isolatedGoalId,
          TodoFixture.getRandomSentenceWithMax(50),
          TodoFixture.createValidMemo(),
          LocalDate.now().plusDays(1),
          LocalTime.of(10, 0),
          LocalTime.of(11, 0),
          reminders
      );
    }

  }

}
