package com.ddudu.fixture;

import com.ddudu.common.dto.Authority;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.ProviderType;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAdjective;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAnimal;
import com.ddudu.domain.user.user.aggregate.enums.UserStatus;
import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.aggregate.vo.DisplayOptions;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationItem;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFixture extends BaseFixture {

  public static User createRandomUserWithId() {
    return createRandomUser(getRandomId(), null, null, null, null, null, null);
  }

  public static User createRandomAdminUserWithId() {
    return createRandomUserWithAuthority(getRandomId(), Authority.ADMIN);
  }

  public static User createRandomUserWithAuthority(long id, Authority authority) {
    User user = createRandomUser(id, null, null, null, null, null, null);
    return User.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .username(user.getUsername())
        .introduction(user.getIntroduction())
        .authority(authority)
        .authProviders(user.getAuthProviders())
        .status(user.getStatus())
        .options(Options.builder()
            .allowingFollowsAfterApproval(user.isAllowingFollowsAfterApproval())
            .templateNotification(user.isNotifyingTemplate())
            .dduduNotification(user.isNotifyingDdudu())
            .build())
        .build();
  }

  public static User createRandomUserWithNullOptions(long id) {
    String lowTime = UUID.randomUUID()
        .toString()
        .substring(0, 8);
    RandomUserAdjective adjective = RandomUserAdjective.getRandom();
    RandomUserAnimal animal = RandomUserAnimal.getRandom();
    String username = adjective.getUsername() + animal.getUsername() + lowTime;
    String nickname = adjective.getNickname() + " " + animal.getNickname();

    return User.builder()
        .id(id)
        .nickname(nickname)
        .username(username)
        .authority(Authority.NORMAL)
        .authProviders(Collections.singletonList(createRandomAuthProvider()))
        .status(UserStatus.ACTIVE)
        .options(null)
        .allowingFollowsAfterApproval(null)
        .templateNotification(null)
        .dduduNotification(null)
        .build();
  }

  public static User createRandomSocialUser(AuthProvider authProvider) {
    return createRandomUser(getRandomId(), null, authProvider, null, null, null, null);
  }

  public static User createRandomUserWithWeekStartDay(long id, String weekStartDay) {
    Options options = Options.builder()
        .display(DisplayOptions.builder()
            .weekStartDay(WeekStartDay.get(weekStartDay))
            .darkMode(faker.bool().bool())
            .build())
        .menuActivation(MenuActivationOptions.builder()
            .calendar(MenuActivationItem.builder()
                .active(faker.bool().bool())
                .priority(faker.number().numberBetween(1, 10))
                .build())
            .dashboard(MenuActivationItem.builder()
                .active(faker.bool().bool())
                .priority(faker.number().numberBetween(1, 10))
                .build())
            .stats(MenuActivationItem.builder()
                .active(faker.bool().bool())
                .priority(faker.number().numberBetween(1, 10))
                .build())
            .build())
        .appConnection(AppConnectionOptions.builder()
            .realtimeSync(RealtimeSyncOptions.builder()
                .notion(faker.bool().bool())
                .googleCalendar(faker.bool().bool())
                .microsoftTodo(faker.bool().bool())
                .build())
            .build())
        .build();

    return createRandomUser(id, null, null, options, null, null, null);
  }

  public static User createRandomUser(
      long id,
      String introduction,
      AuthProvider authProvider,
      Options options,
      Boolean allowingFollowsAfterApproval,
      Boolean templateNotification,
      Boolean dduduNotification
  ) {
    String lowTime = UUID.randomUUID()
        .toString()
        .substring(0, 8);
    RandomUserAdjective adjective = RandomUserAdjective.getRandom();
    RandomUserAnimal animal = RandomUserAnimal.getRandom();
    String username = adjective.getUsername() + animal.getUsername() + lowTime;
    String nickname = adjective.getNickname() + " " + animal.getNickname();

    return User.builder()
        .id(id)
        .nickname(nickname)
        .username(username)
        .introduction(introduction)
        .authority(Authority.NORMAL)
        .authProviders(Collections.singletonList(
            Objects.nonNull(authProvider) ? authProvider : createRandomAuthProvider()))
        .status(UserStatus.ACTIVE)
        .options(options)
        .allowingFollowsAfterApproval(
            Objects.nonNull(allowingFollowsAfterApproval) ? allowingFollowsAfterApproval
                : faker.bool()
                    .bool())
        .templateNotification(Objects.nonNull(templateNotification) ? templateNotification
            : faker.bool()
                .bool())
        .dduduNotification(Objects.nonNull(dduduNotification) ? dduduNotification : faker.bool()
            .bool())
        .build();
  }

  public static AuthProvider createRandomAuthProvider() {
    ProviderType[] types = ProviderType.values();
    int index = getRandomInt(0, types.length - 1);
    ProviderType providerType = types[index];

    return AuthProvider.builder()
        .providerId(String.valueOf(getRandomId()))
        .providerType(providerType.name())
        .build();
  }

}
