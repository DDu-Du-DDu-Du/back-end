package com.ddudu.todo.service;

import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

  private final TodoRepository todoRepository;

  @Transactional(readOnly = true)
  public TodoResponse findById(Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("할 일 아이디가 존재하지 않습니다."));

    return TodoResponse.from(todo);
  }

}
