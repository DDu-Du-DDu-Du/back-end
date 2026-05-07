package com.modoo.application.common.dto.todo.request;

import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;

@Getter
public final class TodoSearchRequest {

  private final ScrollRequest scroll;

  @Parameter(
      name = "query",
      description = "검색어",
      example = "알고리즘"
  )
  private final String query;

  @Parameter(
      name = "isMine",
      description = "검색 대상이 본인인지. Not Yet Implemented.",
      example = "true"
  )
  private final Boolean isMine;

  @Parameter(
      name = "timeZone",
      description = "클라이언트 타임존",
      example = "Asia/Seoul"
  )
  private final String timeZone;

  // TODO: 일반 검색 대상 추가
  public TodoSearchRequest(
      String order, String cursor, Integer size, String query, String timeZone
  ) {
    this.scroll = new ScrollRequest(order, cursor, size);
    this.query = query;
    this.isMine = true;
    this.timeZone = timeZone;
  }

  public int getSize() {
    return scroll.getSize();
  }

}
