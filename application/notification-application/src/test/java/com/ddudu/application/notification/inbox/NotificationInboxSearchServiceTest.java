package com.ddudu.application.notification.inbox;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.notification.NotificationInboxCursorDto;
import com.ddudu.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.ddudu.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.NotificationInboxFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import org.assertj.core.api.Assertions;
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
class NotificationInboxSearchServiceTest {

  @Autowired
  NotificationInboxSearchService notificationInboxSearchService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  NotificationInboxCommandPort notificationInboxCommandPort;

  @Autowired
  NotificationInboxLoaderPort notificationInboxLoaderPort;

  User user;
  int size;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    size = BaseFixture.getRandomInt(10, 30);
    LocalDateTime remindAt = LocalDateTime.now()
        .plusSeconds(5);
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));

    for (int i = 0; i < size + 1; i++) {
      Ddudu ddudu = saveDduduPort.save(DduduFixture.createDduduWithReminderFor(
          user.getId(),
          goal.getId(),
          remindAt
      ));
      NotificationEvent notificationEvent = notificationEventCommandPort.save(
          NotificationEventFixture.createFiredDduduEventNowWithUserAndContext(
              user.getId(),
              ddudu.getId()
          )
      );

      notificationInboxCommandPort.save(
          NotificationInboxFixture.createNotReadInboxSelfWithContextAndContent(
              notificationEvent.getId(),
              user.getId(),
              NotificationEventTypeCode.DDUDU_REMINDER,
              BaseFixture.getRandomId(),
              BaseFixture.getRandomSentenceWithMax(50),
              BaseFixture.getRandomSentenceWithMax(200)
          )
      );
    }
  }

  @Test
  void 최신_목록_조회에_성공한다() {
    // given
    NotificationInboxSearchRequest request = new NotificationInboxSearchRequest("0", size);

    // when
    ScrollResponse<NotificationInboxSearchResponse> response = notificationInboxSearchService.search(
        user.getId(),
        request
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(size - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(size);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));

    NotificationInboxSearchResponse firstInbox = response.contents()
        .get(0);

    assertThat(firstInbox.isFromSystem()).isEqualTo(Objects.equals(
        firstInbox.senderId(),
        user.getId()
    ));
    assertThat(firstInbox.isRead()).isFalse();
  }

  @Test
  void 기본_10개의_목록_조회에_성공한다() {
    // given
    int defaultSize = 10;
    NotificationInboxSearchRequest request = new NotificationInboxSearchRequest("0", null);

    // when
    ScrollResponse<NotificationInboxSearchResponse> response = notificationInboxSearchService.search(
        user.getId(),
        request
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(defaultSize - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(defaultSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
  }

  @Test
  void 다음_커서_기반으로_목록_조회에_성공한다() {
    // given
    int expectedSize = size / 2;
    ScrollRequest firstRequest = new ScrollRequest(null, "0", expectedSize);
    List<NotificationInboxCursorDto> firstResult = notificationInboxLoaderPort.search(
        user.getId(),
        firstRequest
    );
    String nextCursor = firstResult.get(expectedSize - 1)
        .cursor();
    NotificationInboxSearchRequest secondRequest = new NotificationInboxSearchRequest(
        nextCursor,
        expectedSize
    );

    // when
    ScrollResponse<NotificationInboxSearchResponse> response = notificationInboxSearchService.search(
        user.getId(),
        secondRequest
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(expectedSize - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(expectedSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
  }

  @Test
  void 커서가_제공되지_않으면_가장_최신_인박스부터_검색한다() {
    // given
    NotificationInboxSearchRequest request = new NotificationInboxSearchRequest(null, size);

    // when
    ScrollResponse<NotificationInboxSearchResponse> response = notificationInboxSearchService.search(
        user.getId(),
        request
    );

    // then
    Long expectedNextCursor = response.contents()
        .get(size - 1)
        .id();

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(size);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
  }

  @Test
  void 유효하지_않은_사용자_조회에_실패한다() {
    // given
    long invalidId = BaseFixture.getRandomId();
    NotificationInboxSearchRequest request = new NotificationInboxSearchRequest("0", size);

    // when
    ThrowingCallable search = () -> notificationInboxSearchService.search(invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(search)
        .withMessage(NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
