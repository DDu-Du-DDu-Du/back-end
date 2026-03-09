package com.ddudu.application.notification.announcement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.notification.in.CreateAnnouncementUseCase;
import com.ddudu.application.common.port.notification.out.AnnouncementLoaderPort;
import com.ddudu.common.exception.AnnouncementErrorCode;
import com.ddudu.domain.notification.announcement.aggregate.Announcement;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.AnnouncementFixture;
import com.ddudu.fixture.UserFixture;
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
class CreateAnnouncementServiceTest {

  @Autowired
  CreateAnnouncementUseCase createAnnouncementUseCase;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  AnnouncementLoaderPort announcementLoaderPort;

  User user;
  String title;
  String contents;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    title = AnnouncementFixture.getRandomAnnouncementTitle();
    contents = AnnouncementFixture.getRandomAnnouncementContents();
  }

  @Test
  void 공지사항_생성에_성공한다() {
    // given
    CreateAnnouncementRequest request = new CreateAnnouncementRequest(title, contents);

    // when
    IdResponse response = createAnnouncementUseCase.create(user.getId(), request);

    // then
    Announcement saved = announcementLoaderPort.getAnnouncementOrElseThrow(response.id(), "not found");

    assertThat(saved.getId()).isEqualTo(response.id());
    assertThat(saved.getUserId()).isEqualTo(user.getId());
    assertThat(saved.getTitle()).isEqualTo(title);
    assertThat(saved.getContents()).isEqualTo(contents);
  }

  @Test
  void 로그인_사용자가_존재하지_않으면_생성에_실패한다() {
    // given
    long invalidId = AnnouncementFixture.getRandomId();
    CreateAnnouncementRequest request = new CreateAnnouncementRequest(title, contents);

    // when
    ThrowingCallable create = () -> createAnnouncementUseCase.create(invalidId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(create)
        .withMessage(AnnouncementErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
