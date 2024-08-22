package com.ddudu.application.domain.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.StatsBaseDto;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(DduduDomainService.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduDomainServiceTest {

  @Autowired
  DduduDomainService dduduDomainService;

  @Nested
  class 뚜두_생성_테스트 {

    User user;
    Long goalId;
    String name;
    LocalDate scheduledOn;
    CreateDduduRequest request;

    @BeforeEach
    void setUp() {
      user = UserFixture.createRandomUserWithId();
      goalId = GoalFixture.getRandomId();
      name = DduduFixture.getRandomSentenceWithMax(50);
      scheduledOn = LocalDate.now();
      request = new CreateDduduRequest(goalId, name, scheduledOn);
    }

    @Test
    void 뚜두를_생성한다() {
      // when
      Ddudu actual = dduduDomainService.create(user, request);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "scheduledOn")
          .containsExactly(user.getId(), goalId, name, scheduledOn);
    }

    @Test
    void 날짜가_설정되지_않으면_뚜두의_날짜가_생성_날짜가_된다() {
      // when
      CreateDduduRequest request = new CreateDduduRequest(goalId, name, null);
      Ddudu actual = dduduDomainService.create(user, request);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "scheduledOn")
          .containsExactly(user.getId(), goalId, name, LocalDate.now());
    }

  }

  @Nested
  class 뚜두_스탯_테스트 {

    long id;
    long goalId;
    int totalSize;
    int completedSize;
    int uncompletedSize;
    List<StatsBaseDto> stats;

    @BeforeEach
    void setUp() {
      id = DduduFixture.getRandomId();
      goalId = DduduFixture.getRandomId();
      stats = new ArrayList<>();
    }

    @Nested
    class 뚜두_달성도_테스트 {

      @BeforeEach
      void setUp() {
        totalSize = DduduFixture.getRandomInt(100, 1000);
        completedSize = DduduFixture.getRandomInt(1, totalSize);
        uncompletedSize = totalSize - completedSize;

        for (int i = 0; i < completedSize; i++) {
          stats.add(new StatsBaseDto(id, goalId, DduduStatus.COMPLETE, false, LocalDate.now(),
              LocalDateTime.now()
          ));
        }

        for (int i = 0; i < uncompletedSize; i++) {
          stats.add(new StatsBaseDto(id, goalId, DduduStatus.UNCOMPLETED, false, LocalDate.now(),
              LocalDateTime.now()
          ));
        }
      }

      @Test
      void 달성도를_계산한다() {
        // given
        int expected = Math.round((float) completedSize / totalSize * 100);

        // when
        int actual = dduduDomainService.calculateAchievementPercentage(stats);

        // then
        assertThat(actual).isEqualTo(expected);
      }

      @Test
      void 대상_스탯이_비었으면_달성도로_0을_반환한다() {
        // given
        stats = new ArrayList<>();

        // when
        int actual = dduduDomainService.calculateAchievementPercentage(stats);

        // then
        assertThat(actual).isZero();
      }

    }

    @Nested
    class 뚜두_지속도_테스트 {

      @BeforeEach
      void setUp() {
        totalSize = 14;
        completedSize = 10;
        uncompletedSize = totalSize - completedSize;

        for (int i = 0; i < completedSize; i++) {
          LocalDate scheduledOn = LocalDate.now()
              .plusDays(i);

          stats.add(new StatsBaseDto(id, goalId, DduduStatus.COMPLETE, false, scheduledOn,
              LocalDateTime.now()
          ));
        }

        for (int i = 0; i < uncompletedSize; i++) {
          LocalDate scheduledOn = LocalDate.now()
              .plusDays(i);

          stats.add(new StatsBaseDto(id, goalId, DduduStatus.UNCOMPLETED, false, scheduledOn,
              LocalDateTime.now()
          ));
        }
      }

      @Test
      void 지속도를_계산한다() {
        // given
        LocalDate newStreakStart = LocalDate.now()
            .plusDays(completedSize + 1);

        stats.add(new StatsBaseDto(id, goalId, DduduStatus.COMPLETE, false, newStreakStart,
            LocalDateTime.now()
        ));

        // when
        int actual = dduduDomainService.calculateSustenanceCount(stats);

        // then
        assertThat(actual).isEqualTo(completedSize);
      }

      @Test
      void 대상_스탯이_비었으면_0을_반환한다() {
        // given
        stats = new ArrayList<>();

        // when
        int actual = dduduDomainService.calculateSustenanceCount(stats);

        // then
        assertThat(actual).isZero();
      }

    }

    @Nested
    class 미룬_뚜두_테스트 {

      @BeforeEach
      void setUp() {
        totalSize = DduduFixture.getRandomInt(100, 1000);
        completedSize = DduduFixture.getRandomInt(1, totalSize);
        uncompletedSize = totalSize - completedSize;

        for (int i = 0; i < completedSize; i++) {
          stats.add(new StatsBaseDto(id, goalId, DduduStatus.COMPLETE, false, LocalDate.now(),
              LocalDateTime.now()
          ));
        }

        for (int i = 0; i < uncompletedSize; i++) {
          stats.add(new StatsBaseDto(id, goalId, DduduStatus.UNCOMPLETED, true, LocalDate.now(),
              LocalDateTime.now()
          ));
        }
      }

      @Test
      void 미루기_통계를_계산한다() {
        // given

        // when
        int actual = dduduDomainService.calculatePostponementCount(stats);

        // then
        assertThat(actual).isEqualTo(uncompletedSize);
      }

      @Test
      void 대상_스탯이_비었으면_0을_반환한다() {
        // given
        stats = new ArrayList<>();

        // when
        int actual = dduduDomainService.calculatePostponementCount(stats);

        // then
        assertThat(actual).isZero();
      }

    }

    @Nested
    class 뚜두_재달성률_테스트 {

      @BeforeEach
      void setUp() {
        totalSize = DduduFixture.getRandomInt(100, 1000);
        completedSize = DduduFixture.getRandomInt(1, totalSize);
        uncompletedSize = totalSize - completedSize;

        for (int i = 0; i < completedSize; i++) {
          stats.add(new StatsBaseDto(id, goalId, DduduStatus.COMPLETE, true, LocalDate.now(),
              LocalDateTime.now()
          ));
        }

        for (int i = 0; i < uncompletedSize; i++) {
          stats.add(new StatsBaseDto(id, goalId, DduduStatus.UNCOMPLETED, true, LocalDate.now(),
              LocalDateTime.now()
          ));
        }
      }

      @Test
      void 재달성률을_계산한다() {
        // given
        int expected = Math.round((float) completedSize / totalSize * 100);

        // when
        int actual = dduduDomainService.calculateReattainmentCount(stats);

        // then
        assertThat(actual).isEqualTo(expected);
      }

      @Test
      void 대상_스탯이_비었으면_0을_반환한다() {
        // given
        stats = new ArrayList<>();

        // when
        int actual = dduduDomainService.calculateReattainmentCount(stats);

        // then
        assertThat(actual).isZero();
      }

    }

  }

}
