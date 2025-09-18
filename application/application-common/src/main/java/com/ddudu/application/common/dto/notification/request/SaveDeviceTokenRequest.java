package com.ddudu.application.common.dto.notification.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveDeviceTokenRequest(
    @NotBlank(message = "12002 INVALID_CHANNEL")
    String channel,
    @NotNull(message = "12003 NULL_TOKEN")
    @Size(max = 512, message = "12004 EXCESSIVE_TOKEN_LENGTH")
    String token
) {

}
