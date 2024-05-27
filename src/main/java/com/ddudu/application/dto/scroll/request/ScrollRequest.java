package com.ddudu.application.dto.scroll.request;

import com.ddudu.application.dto.scroll.OrderType;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class ScrollRequest {

  private static final int DEFAULT_SIZE = 10;

  @Parameter(
      name = "order",
      description = "정렬 순서 타입. 소문자 가능. 기본 최신순.",
      example = "latest"
  )
  private final OrderType order;

  @Parameter(
      name = "cursor",
      description = "커서. 이 커서의 다음 순서부터 조회.",
      example = "0"
  )
  private final String cursor;

  @Parameter(
      name = "size",
      description = "컨텐츠 사이즈. 기본 10.",
      example = "10"
  )
  private final int size;

  public ScrollRequest(
      String order,
      String cursor,
      Integer size
  ) {
    this.order = OrderType.from(order);
    this.cursor = cursor;
    this.size = Objects.isNull(size) ? DEFAULT_SIZE : size;
  }

}
