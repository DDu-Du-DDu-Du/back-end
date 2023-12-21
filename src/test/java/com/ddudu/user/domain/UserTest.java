package com.ddudu.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.user.domain.User.UserBuilder;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

  static Faker faker = new Faker();
  String email;
  String password;
  String nickname;

  @Autowired
  TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    email = faker.internet()
        .emailAddress();
    password = faker.internet()
        .password();
    nickname = faker.oscarMovie()
        .character();
  }

  @Test
  void User_인스턴스를_생성한다() {
    // given
    User expected = User.builder()
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();

    // when
    User actual = entityManager.persist(expected);

    // then
    assertThat(actual).usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  void User_엔티티에_Id에_Auto_Increment가_적용된다() {
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

  @ParameterizedTest(name = "유효하지 않은 이메일 : {0}")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "email", "email@example", "email@example.", "email@example.com."})
  void 유효하지_않은_이메일의_유저_생성을_실패한다(String email) {
    // given
    UserBuilder userBuilder = User.builder()
        .email(email)
        .password("password")
        .nickname("jayjay");

    // when
    ThrowingCallable construct = userBuilder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(construct);
  }

}
