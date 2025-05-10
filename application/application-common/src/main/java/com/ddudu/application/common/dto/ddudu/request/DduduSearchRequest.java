package com.ddudu.application.common.dto.ddudu.request;

import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;

@Getter
public final class DduduSearchRequest {

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

  // TODO: 일반 검색 대상 추가
  public DduduSearchRequest(String order, String cursor, Integer size, String query) {
    this.scroll = new ScrollRequest(order, cursor, size);
    this.query = query;
    this.isMine = true;
  }

  public int getSize() {
    return scroll.getSize();
  }

}
