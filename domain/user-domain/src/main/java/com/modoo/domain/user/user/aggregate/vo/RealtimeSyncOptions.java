package com.modoo.domain.user.user.aggregate.vo;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RealtimeSyncOptions {

  private final boolean notion;
  private final boolean googleCalendar;
  private final boolean microsoftTodo;

  @Builder
  private RealtimeSyncOptions(Boolean notion, Boolean googleCalendar, Boolean microsoftTodo) {
    this.notion = Objects.nonNull(notion) && notion;
    this.googleCalendar = Objects.nonNull(googleCalendar) && googleCalendar;
    this.microsoftTodo = Objects.nonNull(microsoftTodo) && microsoftTodo;
  }

}
