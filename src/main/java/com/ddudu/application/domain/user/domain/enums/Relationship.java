package com.ddudu.application.domain.user.domain.enums;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum Relationship {
  ME(List.of(PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC)) {
    boolean isSatisfied(User user, User targetUser) {
      return user.equals(targetUser);
    }
  },
  FOLLOWER(List.of(PrivacyType.FOLLOWER, PrivacyType.PUBLIC)) {
    boolean isSatisfied(User user, User targetUser) {
      // TODO : 팔로우 기능 구현 후 수정
      return false;
    }
  },
  NONE(List.of(PrivacyType.PUBLIC)) {
    boolean isSatisfied(User user, User targetUser) {
      return true;
    }
  };

  private final List<PrivacyType> accessiblePrivacyTypes;

  Relationship(List<PrivacyType> accessiblePrivacyTypes) {
    this.accessiblePrivacyTypes = accessiblePrivacyTypes;
  }

  public static Relationship getRelationship(User user, User targetUser) {
    return Arrays.stream(Relationship.values())
        .filter(relationship -> relationship.isSatisfied(user, targetUser))
        .findFirst()
        .orElse(NONE);
  }

  abstract boolean isSatisfied(User user, User targetUser);

}
