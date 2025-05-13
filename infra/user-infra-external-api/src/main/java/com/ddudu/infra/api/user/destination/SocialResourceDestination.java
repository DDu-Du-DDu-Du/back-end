package com.ddudu.infra.api.user.destination;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SocialResourceDestination {
  KAKAO("https://kapi.kakao.com/v2/user/me");

  private final String url;

  public static String getDestinationBy(String type) {
    return Arrays.stream(SocialResourceDestination.values())
        .filter(destination -> type.equals(destination.name()))
        .findFirst()
        .orElseThrow()
        .url;
  }

}
