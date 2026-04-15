package com.modoo.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.notification.in.RemoveNotificationEventUseCase;
import com.modoo.application.common.port.notification.out.NotificationEventCommandPort;
import com.modoo.application.common.port.notification.out.NotificationEventLoaderPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.domain.notification.event.aggregate.NotificationEvent;
import com.modoo.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.NotificationEventFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
class RemoveNotificationEventServiceTest {

  @Autowired
  RemoveNotificationEventUseCase removeNotificationEventUseCase;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  User user;
  Goal goal;
  Todo todo;
  LocalDateTime willFireAt;
  NotificationEvent notificationEvent;

  @BeforeEach
  void setUp() {
    willFireAt = NotificationEventFixture.getFutureDateTime(10, TimeUnit.DAYS);
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
    notificationEvent = NotificationEventFixture.createValidTodoEventWithUserAndContext(
        user.getId(),
        todo.getId(),
        willFireAt
    );
  }

  @Test
  void 알림_이벤트_기록을_성공적으로_삭제한다() {
    // given
    notificationEventCommandPort.save(notificationEvent);

    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.builder()
        .userId(user.getId())
        .typeCode(NotificationEventTypeCode.TODO_REMINDER)
        .contextId(todo.getId())
        .build();
    boolean expected = !notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.TODO_REMINDER,
        todo.getId()
    );

    // when
    removeNotificationEventUseCase.remove(removeEvent);

    // then
    boolean actual = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.TODO_REMINDER,
        todo.getId()
    );

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 알림이_존재하지_않으면_삭제하지_않는다() {
    // given
    long invalidId = NotificationEventFixture.getRandomId();
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.builder()
        .userId(user.getId())
        .typeCode(NotificationEventTypeCode.TODO_REMINDER)
        .contextId(invalidId)
        .build();
    boolean expected = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.TODO_REMINDER,
        invalidId
    );

    // when
    removeNotificationEventUseCase.remove(removeEvent);

    // then
    boolean actual = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.TODO_REMINDER,
        invalidId
    );

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 알림이_이미_발송됐으면_삭제하지_않는다() {
    // given
    notificationEvent = notificationEventCommandPort.save(
        NotificationEventFixture.createFiredTodoEventNowWithUserAndContext(
            user.getId(),
            todo.getId()
        )
    );
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.builder()
        .userId(user.getId())
        .typeCode(NotificationEventTypeCode.TODO_REMINDER)
        .contextId(todo.getId())
        .build();
    boolean expected = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.TODO_REMINDER,
        todo.getId()
    );

    // when
    removeNotificationEventUseCase.remove(removeEvent);

    // then
    boolean actual = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.TODO_REMINDER,
        todo.getId()
    );

    assertThat(actual).isEqualTo(expected);
  }

}