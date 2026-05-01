package com.modoo.application.common.dto.todo.response;

import java.time.LocalDate;
import java.util.List;

public record TodoDashboardContent(
    LocalDate date,
    List<TodoDashboardItem> todos
) {

  public static TodoDashboardContent of(LocalDate date, List<TodoDashboardItem> todos) {
    return new TodoDashboardContent(date, todos);
  }

}
