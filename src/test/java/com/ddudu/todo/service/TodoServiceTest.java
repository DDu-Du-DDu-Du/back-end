package com.ddudu.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoServiceTest {

  @Autowired
  TodoService todoService;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  GoalRepository goalRepository;

  @Nested
  class 할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() {
      // given
      Goal goal = createGoal("dev course");
      Todo todo = createTodo("할 일 1개 조회 기능 구현", goal);

      // when
      TodoResponse response = todoService.findById(todo.getId());

      // then
      assertThat(response).extracting("id", "goalId", "goalName", "name", "status")
          .containsExactly(todo.getId(), goal.getId(), goal.getName(), todo.getName(),
              todo.getStatus()
                  .name()
          );
    }

    @Test
    void 아이디가_존재하지_않아_할_일_조회를_실패한다() {
      // given
      Long invalidId = 999L;

      // when then
      assertThatThrownBy(() -> todoService.findById(invalidId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("할 일 아이디가 존재하지 않습니다.");
    }

  }

  private Goal createGoal(String name) {
    Goal goal = Goal.builder()
        .name(name)
        .build();

    return goalRepository.save(goal);
  }

  private Todo createTodo(String name, Goal goal) {
    Todo todo = Todo.builder()
        .name(name)
        .goal(goal)
        .build();

    return todoRepository.save(todo);
  }

}