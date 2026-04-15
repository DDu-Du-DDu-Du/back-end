package com.modoo.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.scroll.response.ScrollResponse;
import com.modoo.application.common.dto.todo.SimpleTodoSearchDto;
import com.modoo.application.common.dto.todo.request.TodoSearchRequest;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
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
class TodoSearchServiceTest {

  @Autowired
  TodoSearchService todoSearchService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  Goal goal;
  int size;
  List<Todo> todos;
  Long latestId;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    size = TodoFixture.getRandomInt(10, 100);
    todos = saveTodoPort.saveAll(TodoFixture.createMultipleTodosWithGoal(goal, size + 1));
    latestId = todos.get(size)
        .getId();
  }

  @Test
  void 투두_최신순_목록_조회를_성공한다() {
    // given
    TodoSearchRequest request = new TodoSearchRequest(null, null, size, null);

    // when
    ScrollResponse<SimpleTodoSearchDto> response = todoSearchService.search(
        user.getId(),
        request
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(size - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(size);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
  }

  @Test
  void 기본_10개의_투두_목록_조회를_성공한다() {
    // given
    TodoSearchRequest request = new TodoSearchRequest(null, null, null, null);
    int defaultSize = 10;

    // when
    ScrollResponse<SimpleTodoSearchDto> response = todoSearchService.search(
        user.getId(),
        request
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(defaultSize - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(defaultSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
  }

  @Test
  void 다음_커서_기반으로_투두_최신순_목록_조회를_성공한다() {
    // given
    int expectedSize = 5;
    String nextCursor = String.valueOf(latestId - expectedSize + 1);
    TodoSearchRequest request = new TodoSearchRequest(null, nextCursor, expectedSize, null);

    // when
    ScrollResponse<SimpleTodoSearchDto> response = todoSearchService.search(
        user.getId(),
        request
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(expectedSize - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(expectedSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
  }

  @Test
  void 검색어_조회를_성공한다() {
    // given
    Todo postponedTodo = saveTodoPort.save(
        TodoFixture.createTodoWithScheduleAndPostponedFlag(
            goal, true, todos.get(0)
                .getScheduledOn()
        )
    );
    TodoSearchRequest request = new TodoSearchRequest(
        null,
        null,
        size,
        postponedTodo.getName()
    );

    // when
    ScrollResponse<SimpleTodoSearchDto> response = todoSearchService.search(
        user.getId(),
        request
    );

    // then
    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(1);
    assertThat(response.nextCursor()).isNull();
    assertThat(response.hasNext()).isFalse();
    assertThat(response.contents()
        .get(0)
        .postponedAt()).isNotNull();
  }

  @Test
  void 사용자가_없으면_조회를_실패한다() {
    // given
    long invalidId = TodoFixture.getRandomId();
    TodoSearchRequest request = new TodoSearchRequest(null, null, size, null);

    // when
    ThrowingCallable search = () -> todoSearchService.search(invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(search)
        .withMessage(TodoErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
