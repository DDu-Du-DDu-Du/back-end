package com.ddudu.persistence.entity;

import com.ddudu.common.BaseEntity;
import com.ddudu.like.domain.Like;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "todo_id", nullable = false)
  private TodoEntity todo;

  @Builder
  public LikeEntity(
      Long id, UserEntity user, TodoEntity todo, LocalDateTime createdAt, LocalDateTime updatedAt,
      Boolean isDeleted
  ) {
    super(createdAt, updatedAt, isDeleted);

    this.id = id;
    this.user = user;
    this.todo = todo;
  }

  public static LikeEntity from(Like like) {
    return LikeEntity.builder()
        .id(like.getId())
        .user(UserEntity.from(like.getUser()))
        .todo(TodoEntity.from(like.getTodo()))
        .createdAt(like.getCreatedAt())
        .updatedAt(like.getUpdatedAt())
        .isDeleted(like.isDeleted())
        .build();
  }

  public Like toDomain() {
    return Like.builder()
        .id(id)
        .user(user.toDomain())
        .todo(todo.toDomain())
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .isDeleted(isDeleted())
        .build();
  }

}
