package com.modoo.application.common.dto.todo.response;

import java.util.List;

public record TodoDashboardResponse(
    boolean isEmpty,
    List<TodoDashboardContent> contents,
    int todayIndex
) {

  public static TodoDashboardResponse of(boolean isEmpty, List<TodoDashboardContent> contents, int todayIndex) {
    return new TodoDashboardResponse(isEmpty, contents, todayIndex);
  }

}
