package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.util.List;
import java.util.MissingResourceException;
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
class DduduSearchServiceTest {

  @Autowired
  DduduSearchService dduduSearchService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  int size;
  List<Ddudu> ddudus;
  Long latestId;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    size = DduduFixture.getRandomInt(10, 100);
    ddudus = saveDduduPort.saveAll(DduduFixture.createMultipleDdudusWithGoal(goal, size + 1));
    latestId = ddudus.get(size)
        .getId();
  }

  @Test
  void 뚜두_최신순_목록_조회를_성공한다() {
    // given
    DduduSearchRequest request = new DduduSearchRequest(null, null, size, null);

    // when
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchService.search(
        user.getId(),
        request
    );

    // then
    long expectedNextCursor = latestId - size + 1;

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(size);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
  }

  @Test
  void 기본_10개의_뚜두_목록_조회를_성공한다() {
    // given
    DduduSearchRequest request = new DduduSearchRequest(null, null, null, null);
    int defaultSize = 10;

    // when
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchService.search(
        user.getId(),
        request
    );

    // then
    long expectedNextCursor = latestId - defaultSize + 1;

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(defaultSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
  }

  @Test
  void 다음_커서_기반으로_뚜두_최신순_목록_조회를_성공한다() {
    // given
    int expectedSize = 5;
    String nextCursor = String.valueOf(latestId - expectedSize + 1);
    DduduSearchRequest request = new DduduSearchRequest(null, nextCursor, expectedSize, null);

    // when
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchService.search(
        user.getId(),
        request
    );

    // then
    int expectedNextCursor = Integer.parseInt(nextCursor) - expectedSize;

    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(expectedSize);
    assertThat(response.nextCursor()).isEqualTo(String.valueOf(expectedNextCursor));
  }

  @Test
  void 찾는_대상이_없으면_빈_조회_결과를_반환한다() {
    // given
    String cursor = String.valueOf(latestId - size - 1);
    DduduSearchRequest request = new DduduSearchRequest(null, cursor, size, null);

    // when
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchService.search(
        user.getId(),
        request
    );

    // then
    assertThat(response.isEmpty()).isTrue();
    assertThat(response.contents()).isEmpty();
    assertThat(response.nextCursor()).isNull();
  }

  @Test
  void 검색어_조회를_성공한다() {
    // given
    Ddudu firstDdudu = ddudus.get(0);
    DduduSearchRequest request = new DduduSearchRequest(null, null, size, firstDdudu.getName());

    // when
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchService.search(
        user.getId(),
        request
    );

    // then
    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents()).hasSize(1);
    assertThat(response.nextCursor()).isNull();
  }

  @Test
  void 사용자가_없으면_조회를_실패한다() {
    // given
    long invalidId = DduduFixture.getRandomId();
    DduduSearchRequest request = new DduduSearchRequest(null, null, size, null);

    // when
    ThrowingCallable search = () -> dduduSearchService.search(invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(search)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}