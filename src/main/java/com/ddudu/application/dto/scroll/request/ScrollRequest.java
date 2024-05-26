package com.ddudu.application.dto.scroll.request;

import com.ddudu.application.dto.scroll.OrderType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class ScrollRequest {

    private static final int DEFAULT_SIZE = 10;

    @Parameter(description = "정렬 순서 타입. 소문자 가능", example = "latest")
    private final OrderType order;

    @Parameter(description = "커서. 이 커서의 다음 순서부터 조회.", example = "1")
    private final String cursor;

    @Parameter(description = "컨텐츠 사이즈. 기본 10.", example = "10")
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
