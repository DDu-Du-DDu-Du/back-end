package com.ddudu.application.domain.user.domain;

import java.util.Random;
import lombok.Getter;

@Getter
public enum RandomUserAdjective {
  ANGRY("화난"),
  HAPPY("행복한"),
  SAD("슬픈"),
  ASTONISHED("깜짝 놀란"),
  WORRIED("걱정하는"),
  PROUD("자랑스러운"),
  NERVOUS("긴장한"),
  AFRAID("무서워하는"),
  LONELY("외로운"),
  PAINFUL("괴로운"),
  IRRITATED("짜증난"),
  RESOLUTE("불굴의"),
  DETERMINED("결단력있는"),
  AMIABLE("쾌활한"),
  AMUSED("즐거운"),
  DELIGHTED("기쁜");

  private static final Random RANDOM = new Random();

  private final String username;
  private final String nickname;

  RandomUserAdjective(String nickname) {
    this.username = this.name()
        .toLowerCase();
    this.nickname = nickname;
  }

  public static RandomUserAdjective getRandom() {
    RandomUserAdjective[] adjectives = RandomUserAdjective.values();

    return adjectives[RANDOM.nextInt(adjectives.length)];
  }

}
