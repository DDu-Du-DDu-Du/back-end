package com.ddudu.application.domain.user.domain.enums;

import com.ddudu.application.domain.user.domain.User;
import java.util.Arrays;
import java.util.function.BiPredicate;
import lombok.Getter;

@Getter
public enum Relationship {
  ME((user, targetUser) -> user.equals(targetUser)),
  FOLLOWER((user, targetUser) -> false),
  NONE((user, targetUser) -> true);

  private final BiPredicate<User, User> filter;

  Relationship(BiPredicate<User, User> filter) {
    this.filter = filter;
  }

  public static Relationship getRelationship(User user, User targetUser) {
    return Arrays.stream(Relationship.values())
        .filter(relationship -> relationship.isSatisfied(user, targetUser))
        .findFirst()
        .orElse(NONE);
  }

  public boolean isSatisfied(User user, User targetUser) {
    return filter.test(user, targetUser);
  }

}
