package com.ddudu.old.persistence.repository;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.persistence.dao.like.LikeDao;
import com.ddudu.old.persistence.entity.DduduEntity;
import com.ddudu.old.persistence.entity.LikeEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

  private final LikeDao likeDao;

  @Override
  public Like save(Like like) {
    return likeDao.save(LikeEntity.from(like))
        .toDomain();
  }

  @Override
  public Optional<Like> findById(Long id) {
    return likeDao.findById(id)
        .map(LikeEntity::toDomain);
  }

  @Override
  public Like findByUserAndTodo(User user, Ddudu ddudu) {
    LikeEntity found = likeDao.findByUserAndTodo(
        UserEntity.from(user), DduduEntity.from(ddudu));

    if (isNull(found)) {
      return null;
    }

    return found.toDomain();
  }

  @Override
  public List<Like> findByTodos(List<Ddudu> ddudus) {
    List<DduduEntity> todoEntities = ddudus.stream()
        .map(DduduEntity::from)
        .toList();

    return likeDao.findByTodos(todoEntities)
        .stream()
        .map(LikeEntity::toDomain)
        .toList();
  }

  @Override
  public void update(Like like) {
    likeDao.save(LikeEntity.from(like));
  }

  @Override
  public void delete(Like like) {
    likeDao.delete(LikeEntity.from(like));
  }

}
