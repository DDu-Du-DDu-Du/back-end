package com.ddudu.application.domain.goal.domain.enums;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.enums.Relationship;
import java.util.Arrays;
import java.util.List;

public enum PrivacyType {
  PRIVATE(Relationship.ME),
  FOLLOWER(Relationship.ME, Relationship.FOLLOWER),
  PUBLIC(Relationship.ME, Relationship.FOLLOWER, Relationship.NONE);

  private final List<Relationship> accessibleRelationships;

  PrivacyType(Relationship... accessibleRelationships) {
    this.accessibleRelationships = Arrays.asList(accessibleRelationships);
  }

  public static PrivacyType from(String value) {
    if (isNull(value)) {
      return PrivacyType.PRIVATE;
    }

    return Arrays.stream(PrivacyType.values())
        .filter(providerType -> value.toUpperCase()
            .equals(providerType.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(GoalErrorCode.INVALID_PRIVACY_TYPE.getCodeName()));
  }

  public static List<PrivacyType> getAccessibleTypesIn(Relationship relationship) {
    return Arrays.stream(PrivacyType.values())
        .filter(type -> type.isAccessible(relationship))
        .toList();
  }

  private boolean isAccessible(Relationship relationship) {
    return accessibleRelationships.contains(relationship);
  }
}
