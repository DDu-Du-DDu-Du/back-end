package com.modoo.domain.notification.announcement.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.modoo.common.exception.AnnouncementErrorCode;
import com.modoo.fixture.AnnouncementFixture;
import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class AnnouncementTest {

  @Nested
  class CreationTest {

    @Test
    void valid_announcement_creation_succeeds() {
      // given

      // when
      ThrowingCallable create = AnnouncementFixture::createValidAnnouncement;

      // then
      assertThatNoException().isThrownBy(create);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void creation_fails_when_title_is_null_or_blank(String invalidTitle) {
      // given

      // when
      ThrowingCallable create = () -> AnnouncementFixture.createAnnouncementWithTitle(invalidTitle);

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AnnouncementErrorCode.NULL_TITLE.getCodeName());
    }

    @Test
    void creation_fails_when_title_exceeds_50_chars() {
      // given
      String longTitle = AnnouncementFixture.getRandomFixedSentence(51);

      // when
      ThrowingCallable create = () -> AnnouncementFixture.createAnnouncementWithTitle(longTitle);

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AnnouncementErrorCode.EXCESSIVE_TITLE_LENGTH.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void creation_fails_when_contents_is_null_or_blank(String invalidContents) {
      // given

      // when
      ThrowingCallable create = () -> AnnouncementFixture.createAnnouncementWithContents(
          invalidContents
      );

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AnnouncementErrorCode.NULL_CONTENTS.getCodeName());
    }

    @Test
    void creation_fails_when_contents_exceeds_2000_chars() {
      // given
      String longContents = AnnouncementFixture.getRandomFixedSentence(2001);

      // when
      ThrowingCallable create = () -> AnnouncementFixture.createAnnouncementWithContents(
          longContents
      );

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AnnouncementErrorCode.EXCESSIVE_CONTENTS_LENGTH.getCodeName());
    }

    @Test
    void creation_fails_when_user_id_is_null() {
      // given

      // when
      ThrowingCallable create = () -> AnnouncementFixture.createAnnouncementWithUserId(null);

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(AnnouncementErrorCode.NULL_USER_ID.getCodeName());
    }

    @Test
    void creation_succeeds_when_id_is_null() {
      // given
      Announcement valid = AnnouncementFixture.createValidAnnouncement();

      // when
      Announcement announcement = AnnouncementFixture.createAnnouncement(
          null,
          valid.getTitle(),
          valid.getContents(),
          valid.getUserId(),
          valid.getCreatedAt()
      );

      // then
      assertThat(announcement.getId()).isNull();
    }

    @Test
    void creation_succeeds_when_created_at_is_null() {
      // given

      // when
      Announcement announcement = AnnouncementFixture.createAnnouncementWithCreatedAt(null);

      // then
      assertThat(announcement.getCreatedAt()).isNull();
    }

  }

  @Nested
  class UpdateTest {

    @Test
    void update_성공시_id_user_id_created_at은_유지되고_title_contents가_변경된다() {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();
      String newTitle = AnnouncementFixture.getRandomAnnouncementTitle();
      String newContents = AnnouncementFixture.getRandomAnnouncementContents();

      // when
      Announcement updated = announcement.update(newTitle, newContents);

      // then
      assertThat(updated.getId()).isEqualTo(announcement.getId());
      assertThat(updated.getUserId()).isEqualTo(announcement.getUserId());
      assertThat(updated.getCreatedAt()).isEqualTo(announcement.getCreatedAt());
      assertThat(updated.getTitle()).isEqualTo(newTitle);
      assertThat(updated.getContents()).isEqualTo(newContents);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void update_실패_title이_null_or_blank면_예외(String invalidTitle) {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();

      // when
      ThrowingCallable update = () -> announcement.update(
          invalidTitle,
          AnnouncementFixture.getRandomAnnouncementContents()
      );

      // then
      assertThatIllegalArgumentException().isThrownBy(update)
          .withMessage(AnnouncementErrorCode.NULL_TITLE.getCodeName());
    }

    @Test
    void update_실패_title이_50자를_초과하면_예외() {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();
      String longTitle = AnnouncementFixture.getRandomFixedSentence(51);

      // when
      ThrowingCallable update = () -> announcement.update(
          longTitle,
          AnnouncementFixture.getRandomAnnouncementContents()
      );

      // then
      assertThatIllegalArgumentException().isThrownBy(update)
          .withMessage(AnnouncementErrorCode.EXCESSIVE_TITLE_LENGTH.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void update_실패_contents가_null_or_blank면_예외(String invalidContents) {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();

      // when
      ThrowingCallable update = () -> announcement.update(
          AnnouncementFixture.getRandomAnnouncementTitle(),
          invalidContents
      );

      // then
      assertThatIllegalArgumentException().isThrownBy(update)
          .withMessage(AnnouncementErrorCode.NULL_CONTENTS.getCodeName());
    }

    @Test
    void update_실패_contents가_2000자를_초과하면_예외() {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();
      String longContents = AnnouncementFixture.getRandomFixedSentence(2001);

      // when
      ThrowingCallable update = () -> announcement.update(
          AnnouncementFixture.getRandomAnnouncementTitle(),
          longContents
      );

      // then
      assertThatIllegalArgumentException().isThrownBy(update)
          .withMessage(AnnouncementErrorCode.EXCESSIVE_CONTENTS_LENGTH.getCodeName());
    }

  }

  @Nested
  class AuthorTest {

    @Test
    void 같은_user_id면_작성자다() {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();

      // when
      boolean actual = announcement.isAuthor(announcement.getUserId());

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 다른_user_id면_작성자가_아니다() {
      // given
      Announcement announcement = AnnouncementFixture.createValidAnnouncement();
      Long anotherUserId = announcement.getUserId() + 1L;

      // when
      boolean actual = announcement.isAuthor(anotherUserId);

      // then
      assertThat(actual).isFalse();
    }

    @Test
    void user_id가_null이면_작성자가_아니다() {
      // given
      Announcement announcement = AnnouncementFixture.createAnnouncement(
          1L,
          AnnouncementFixture.getRandomAnnouncementTitle(),
          AnnouncementFixture.getRandomAnnouncementContents(),
          10L,
          LocalDateTime.now()
      );

      // when
      boolean actual = announcement.isAuthor(null);

      // then
      assertThat(actual).isFalse();
    }

  }

}
