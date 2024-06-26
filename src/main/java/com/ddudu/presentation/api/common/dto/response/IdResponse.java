package com.ddudu.presentation.api.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "식별자 응답")
public record IdResponse(
    @Schema(description = "식별자", example = "1")
    Long id
) {

}
