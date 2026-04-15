package com.modoo.application.notification.announcement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.notification.request.UpdateAnnouncementRequest;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.notification.in.UpdateAnnouncementUseCase;
import com.modoo.application.common.port.notification.out.AnnouncementCommandPort;
import com.modoo.application.common.port.notification.out.AnnouncementLoaderPort;
import com.modoo.common.exception.AnnouncementErrorCode;
import com.modoo.domain.notification.announcement.aggregate.Announcement;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.AnnouncementFixture;
import com.modoo.fixture.UserFixture;
import java.util.MissingResourceException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class UpdateAnnouncementServiceTest {

  @Autowired
  UpdateAnnouncementUseCase updateAnnouncementUseCase;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  AnnouncementCommandPort announcementCommandPort;

  @Autowired
  AnnouncementLoaderPort announcementLoaderPort;

  User adminUser;
  Announcement savedAnnouncement;
  String newTitle;
  String newBody;

  @BeforeEach
  void setUp() {
    adminUser = signUpPort.save(UserFixture.createRandomAdminUserWithId());
    Announcement announcement = Announcement.builder()
        .userId(adminUser.getId())
        .title(AnnouncementFixture.getRandomAnnouncementTitle())
        .contents(AnnouncementFixture.getRandomAnnouncementContents())
        .build();
    savedAnnouncement = announcementCommandPort.save(announcement);
    newTitle = AnnouncementFixture.getRandomAnnouncementTitle();
    newBody = AnnouncementFixture.getRandomAnnouncementContents();
  }

  @Test
  void 공지사항_수정에_성공한다() {
    // given
    UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(newTitle, newBody);

    // when
    IdResponse response = updateAnnouncementUseCase.update(
        adminUser.getId(),
        savedAnnouncement.getId(),
        request
    );

    // then
    Announcement updated = announcementLoaderPort.getAnnouncementOrElseThrow(
        response.id(),
        AnnouncementErrorCode.ANNOUNCEMENT_NOT_EXISTING.getCodeName()
    );

    assertThat(response.id()).isEqualTo(savedAnnouncement.getId());
    assertThat(updated.getId()).isEqualTo(savedAnnouncement.getId());
    assertThat(updated.getUserId()).isEqualTo(adminUser.getId());
    assertThat(updated.getTitle()).isEqualTo(newTitle);
    assertThat(updated.getContents()).isEqualTo(newBody);
  }

  @Test
  void 로그인_사용자가_없으면_수정에_실패한다() {
    // given
    Long invalidLoginId = AnnouncementFixture.getRandomId();
    UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(newTitle, newBody);

    // when
    ThrowingCallable update = () -> updateAnnouncementUseCase.update(
        invalidLoginId,
        savedAnnouncement.getId(),
        request
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(update)
        .withMessage(AnnouncementErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자_권한이_ADMIN이_아니면_수정에_실패한다() {
    // given
    User normalUser = signUpPort.save(UserFixture.createRandomUserWithId());
    UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(newTitle, newBody);

    // when
    ThrowingCallable update = () -> updateAnnouncementUseCase.update(
        normalUser.getId(),
        savedAnnouncement.getId(),
        request
    );

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(update)
        .withMessage(AnnouncementErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  @Test
  void 공지사항이_없으면_수정에_실패한다() {
    // given
    Long invalidAnnouncementId = AnnouncementFixture.getRandomId();
    UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(newTitle, newBody);

    // when
    ThrowingCallable update = () -> updateAnnouncementUseCase.update(
        adminUser.getId(),
        invalidAnnouncementId,
        request
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(update)
        .withMessage(AnnouncementErrorCode.ANNOUNCEMENT_NOT_EXISTING.getCodeName());
  }

  @Test
  void 공지사항_작성자와_로그인_사용자가_다르면_수정에_실패한다() {
    // given
    User anotherAdmin = signUpPort.save(UserFixture.createRandomAdminUserWithId());
    UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(newTitle, newBody);

    // when
    ThrowingCallable update = () -> updateAnnouncementUseCase.update(
        anotherAdmin.getId(),
        savedAnnouncement.getId(),
        request
    );

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(update)
        .withMessage(AnnouncementErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
