package com.ddudu.old.like.domain;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository {

  Like save(Like like);

  Optional<Like> findById(Long id);

  Like findByUserAndTodo(User user, Ddudu ddudu);

  List<Like> findByTodos(List<Ddudu> ddudus);

  void update(Like like);

  void delete(Like like);

}
