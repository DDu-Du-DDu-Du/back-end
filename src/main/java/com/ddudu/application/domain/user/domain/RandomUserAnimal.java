package com.ddudu.application.domain.user.domain;

import java.util.Random;
import lombok.Getter;

@Getter
public enum RandomUserAnimal {
  LION("사자"),
  OTTER("수달"),
  PIG("돼지"),
  PUPPY("강아지"),
  KITTEN("고양이"),
  TURTLE("거북이"),
  TIGER("호랑이"),
  BADGER("오소리"),
  MOLE("두더지"),
  CALF("송아지"),
  EMU("에뮤"),
  DINGO("딩고"),
  KOALA("코알라"),
  FERRET("페럿"),
  PAROT("앵무새");

  private static final Random RANDOM = new Random();

  private final String username;
  private final String nickname;

  RandomUserAnimal(String nickname) {
    this.username = this.name()
        .toLowerCase();
    this.nickname = nickname;
  }

  public static RandomUserAnimal getRandom() {
    RandomUserAnimal[] animals = RandomUserAnimal.values();

    return animals[RANDOM.nextInt(animals.length)];
  }

}
