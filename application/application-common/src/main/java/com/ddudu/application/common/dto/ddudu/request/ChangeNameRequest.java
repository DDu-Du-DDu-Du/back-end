package com.ddudu.application.common.dto.ddudu.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeNameRequest(
    @NotBlank(message = "2002 BLANK_NAME")
    @Size(max = 50, message = "2003 EXCESSIVE_NAME_LENGTH")
    String name
) {

}
