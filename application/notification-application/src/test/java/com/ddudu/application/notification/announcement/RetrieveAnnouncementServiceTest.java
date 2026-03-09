package com.ddudu.application.notification.announcement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.response.AnnouncementDetailResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.notification.in.RetrieveAnnouncementUseCase;
import com.ddudu.application.common.port.notification.out.AnnouncementCommandPort;
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
class RetrieveAnnouncementServiceTest {

  @Autowired
  RetrieveAnnouncementUseCase retrieveAnnouncementUseCase;

  @Autowired
  AnnouncementCommandPort announcementCommandPort;

  @Autowired
  SignUpPort signUpPort;

  User author;
  Announcement savedAnnouncement;

  @BeforeEach
  void setUp() {
    author = signUpPort.save(UserFixture.createRandomUserWithId());

    Announcement announcement = Announcement.builder()
        .userId(author.getId())
        .title(AnnouncementFixture.getRandomAnnouncementTitle())
        .contents(AnnouncementFixture.getRandomAnnouncementContents())
        .build();
    savedAnnouncement = announcementCommandPort.save(announcement);
  }

  @Test
  void 공지사항_ID로_상세조회에_성공한다() {
    // when
    AnnouncementDetailResponse response = retrieveAnnouncementUseCase.findById(
        savedAnnouncement.getId()
    );

    // then
    assertThat(response.id()).isEqualTo(savedAnnouncement.getId());
    assertThat(response.title()).isEqualTo(savedAnnouncement.getTitle());
    assertThat(response.body()).isEqualTo(savedAnnouncement.getContents());
    assertThat(response.author()).isEqualTo(author.getNickname());
    assertThat(response.createdAt()).isEqualTo(savedAnnouncement.getCreatedAt());
  }

  @Test
  void 존재하지_않는_공지사항_ID면_조회에_실패한다() {
    // given
    Long invalidId = AnnouncementFixture.getRandomId();

    // when
    ThrowingCallable findById = () -> retrieveAnnouncementUseCase.findById(invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(findById)
        .withMessage(AnnouncementErrorCode.ANNOUNCEMENT_NOT_EXISTING.getCodeName());
  }

}
