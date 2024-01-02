package com.ddudu.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateTodoRequest(
    @NotNull(message = "목표 ID가 입력되지 않았습니다.")
    @Positive(message = "목표 ID는 양수입니다.")
    Long goalId,
    @NotBlank(message = "할 일이 입력되지 않았습니다.")
    @Size(max = 50, message = "할 일은 최대 50자 입니다.")
    String name,
    LocalDateTime beginAt
) {

}
