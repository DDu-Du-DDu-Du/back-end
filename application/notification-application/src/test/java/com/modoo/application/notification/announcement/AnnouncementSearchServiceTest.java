package com.modoo.application.notification.announcement;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.notification.SimpleAnnouncementDto;
import com.modoo.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.modoo.application.common.dto.scroll.response.ScrollResponse;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.notification.in.AnnouncementSearchUseCase;
import com.modoo.application.common.port.notification.out.AnnouncementCommandPort;
import com.modoo.domain.notification.announcement.aggregate.Announcement;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.AnnouncementFixture;
import com.modoo.fixture.BaseFixture;
import com.modoo.fixture.UserFixture;
import java.util.ArrayList;
import java.util.List;
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
class AnnouncementSearchServiceTest {

  @Autowired
  AnnouncementSearchUseCase announcementSearchUseCase;

  @Autowired
  AnnouncementCommandPort announcementCommandPort;

  @Autowired
  SignUpPort signUpPort;

  User firstAuthor;
  User secondAuthor;
  List<Announcement> savedAnnouncements;
  int size;

  @BeforeEach
  void setUp() {
    firstAuthor = signUpPort.save(UserFixture.createRandomUserWithId());
    secondAuthor = signUpPort.save(UserFixture.createRandomUserWithId());
    size = BaseFixture.getRandomInt(10, 20);
    savedAnnouncements = new ArrayList<>();

    for (int i = 0; i < size + 1; i++) {
      Long userId = i % 2 == 0 ? firstAuthor.getId() : secondAuthor.getId();
      Announcement announcement = Announcement.builder()
          .userId(userId)
          .title(AnnouncementFixture.getRandomAnnouncementTitle())
          .contents(AnnouncementFixture.getRandomAnnouncementContents())
          .build();

      savedAnnouncements.add(announcementCommandPort.save(announcement));
    }
  }

  @Test
  void 최신_공지사항_목록_조회에_성공한다() {
    // given
    AnnouncementSearchRequest request = new AnnouncementSearchRequest("latest", "0", size);

    // when
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(request);

    // then
    Long expectedNextCursor = response.contents()
        .get(size - 1)
        .id();
    Long latestId = savedAnnouncements.get(savedAnnouncements.size() - 1)
        .getId();
    String expectedAuthor = savedAnnouncements.get(savedAnnouncements.size() - 1)
        .getUserId()
        .equals(firstAuthor.getId()) ? firstAuthor.getNickname() : secondAuthor.getNickname();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(size);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
    assertThat(response.contents()
        .get(0)
        .id()).isEqualTo(latestId);
    assertThat(response.contents()
        .get(0)
        .author()).isEqualTo(expectedAuthor);
    assertThat(response.contents()).isSortedAccordingTo((a, b) -> Long.compare(b.id(), a.id()));
  }

  @Test
  void 기본_10개_목록_조회에_성공한다() {
    // given
    int defaultSize = 10;
    AnnouncementSearchRequest request = new AnnouncementSearchRequest(null, "0", null);

    // when
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(request);

    // then
    Long expectedNextCursor = response.contents()
        .get(defaultSize - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(defaultSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
  }

  @Test
  void 다음_커서_기반_목록_조회에_성공한다() {
    // given
    int expectedSize = size / 2;
    AnnouncementSearchRequest firstRequest = new AnnouncementSearchRequest(
        null,
        "0",
        expectedSize
    );
    ScrollResponse<SimpleAnnouncementDto> firstResponse = announcementSearchUseCase.search(
        firstRequest
    );
    String nextCursor = firstResponse.nextCursor();
    AnnouncementSearchRequest secondRequest = new AnnouncementSearchRequest(
        null,
        nextCursor,
        expectedSize
    );

    // when
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(
        secondRequest
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(expectedSize - 1)
        .id();
    Long firstPageLastId = firstResponse.contents()
        .get(expectedSize - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(expectedSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
    assertThat(response.contents())
        .allMatch(announcement -> announcement.id() < firstPageLastId);
  }

  @Test
  void 커서가_없으면_최신부터_조회한다() {
    // given
    AnnouncementSearchRequest request = new AnnouncementSearchRequest(null, null, size);

    // when
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(request);

    // then
    Long expectedNextCursor = response.contents()
        .get(size - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(size);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
    assertThat(response.hasNext()).isTrue();
  }

  @Test
  void 마지막_페이지면_nextCursor는_null이고_hasNext는_false다() {
    // given
    int totalSize = savedAnnouncements.size();
    AnnouncementSearchRequest request = new AnnouncementSearchRequest(null, "0", totalSize + 10);

    // when
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(request);

    // then
    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(totalSize);
    assertThat(response.nextCursor()).isNull();
    assertThat(response.hasNext()).isFalse();
  }

}
