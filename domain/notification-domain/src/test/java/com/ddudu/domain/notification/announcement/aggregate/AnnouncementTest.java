package com.ddudu.domain.notification.announcement.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.common.exception.AnnouncementErrorCode;
import com.ddudu.fixture.AnnouncementFixture;
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

}
