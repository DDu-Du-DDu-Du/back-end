package com.ddudu.fixture;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.common.dto.Authority;
import com.ddudu.domain.user.user.aggregate.enums.ProviderType;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAdjective;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAnimal;
import com.ddudu.domain.user.user.aggregate.enums.UserStatus;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.aggregate.vo.Options;
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

  public static User createRandomSocialUser(AuthProvider authProvider) {
    return createRandomUser(getRandomId(), null, authProvider, null, null, null, null);
  }

  public static User createRandomUser(
      long id, String introduction, AuthProvider authProvider, Options options,
      Boolean allowingFollowsAfterApproval, Boolean templateNotification, Boolean dduduNotification
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
