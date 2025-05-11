package com.ddudu.domain.user.user.aggregate.enums;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.UserFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RelationshipTest {

  @Nested
  class 관계_판별_테스트 {

    User me;
    User anotherUser;

    @BeforeEach
    void setUp() {
      me = UserFixture.createRandomUserWithId();
      anotherUser = UserFixture.createRandomUserWithId();
    }

    @Test
    void 동일한_사용자인_경우_ME_관계를_반환한다() {
      // when
      Relationship relationship = Relationship.getRelationship(me, me);

      // then
      Assertions.assertThat(relationship)
          .isEqualTo(Relationship.ME);
      Assertions.assertThat(relationship.isSatisfied(me, me))
          .isTrue();
    }

    @Test
    void 다른_사용자인_경우_NONE_관계를_반환한다() {
      // when
      Relationship relationship = Relationship.getRelationship(me, anotherUser);

      // then
      Assertions.assertThat(relationship)
          .isEqualTo(Relationship.NONE);
      Assertions.assertThat(relationship.isSatisfied(me, anotherUser))
          .isTrue();
    }

    @Test
    void 관계_우선순위가_올바르게_적용된다() {
      // when
      Relationship relationship = Relationship.getRelationship(me, me);

      // then
      Assertions.assertThat(relationship)
          .isEqualTo(Relationship.ME);
    }

    @Test
    void 서로_다른_ID를_가진_사용자는_ME_관계가_아니다() {
      // when
      boolean isMeRelationship = Relationship.ME.isSatisfied(me, anotherUser);

      // then
      Assertions.assertThat(isMeRelationship)
          .isFalse();
    }

  }

}