package com.ddudu.old.like.domain;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.common.domain.BaseDomain;
import com.ddudu.old.like.exception.LikeErrorCode;
import com.ddudu.presentation.api.exception.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Like extends BaseDomain {

  private Long id;
  private User user;
  private Ddudu ddudu;

  @Builder
  public Like(
      Long id, User user, Ddudu ddudu, LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);
    validate(user, ddudu);

    this.id = id;
    this.user = user;
    this.ddudu = ddudu;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Like like = (Like) o;
    if (id != null) {
      return id.equals(like.id);
    } else {
      return super.equals(o);
    }
  }

  @Override
  public int hashCode() {
    return (id != null) ? id.hashCode() : super.hashCode();
  }

  private void validate(User user, Ddudu ddudu) {
    if (Objects.isNull(user)) {
      throw new InvalidParameterException(LikeErrorCode.NULL_USER);
    }

    if (Objects.isNull(ddudu)) {
      throw new InvalidParameterException(LikeErrorCode.NULL_TODO);
    }

    if (user.equals(ddudu.getUser())) {
      throw new InvalidParameterException(LikeErrorCode.SELF_LIKE_UNAVAILABLE);
    }

    if (ddudu.getStatus()
        .equals(DduduStatus.UNCOMPLETED)) {
      throw new InvalidParameterException(LikeErrorCode.UNAVAILABLE_UNCOMPLETED_TODO);
    }
  }

}
