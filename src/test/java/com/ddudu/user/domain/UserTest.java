package com.ddudu.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DisplayNameGeneration(ReplaceUnderscores.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EnableJpaAuditing
class UserTest {

  @Autowired
  TestEntityManager entityManager;

  @Test
  void User_생성_성공() {
    // given
    User expected = User.builder()
        .email("email@example.com")
        .password("password")
        .nickname("jayjay")
        .build();

    // when
    User actual = entityManager.persist(expected);

    // then
    assertThat(actual).usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  void User_Id_Auto_Increment_적용() {
    // given
    User first = User.builder()
        .email("first@example.com")
        .password("password")
        .nickname("jayjay")
        .build();
    User second = User.builder()
        .email("second@example.com")
        .password("password")
        .nickname("jayjay")
        .build();

    // when
    User persistedFirst = entityManager.persist(first);
    User persistedSecond = entityManager.persist(second);

    // then
    assertThat(persistedFirst.getId()).isNotEqualTo(persistedSecond.getId());
  }

}
