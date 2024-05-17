package com.ddudu.old.todo.domain;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface OldTodoRepository {

  Ddudu save(Ddudu ddudu);

  Optional<Ddudu> findById(Long id);

  List<Ddudu> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, User user);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user, List<PrivacyType> privacyTypes
  );

  void update(Ddudu ddudu);

  void delete(Ddudu ddudu);

}
