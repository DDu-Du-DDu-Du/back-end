package com.ddudu.application.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record SocialRequest(
    @NotBlank(message = "5003 BLANK_SOCIAL_TOKEN")
    String socialToken,
    @NotBlank(message = "5004 BLANK_PROVIDER_TYPE")
    String providerType
) {

  public String getRawToken() {
    return socialToken.split(" ")[1];
  }

}
