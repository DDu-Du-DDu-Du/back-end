package com.ddudu.application.domain.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SocialRequest(
    @NotBlank(message = "BLANK_SOCIAL_TOKEN")
    String socialToken,
    @NotBlank(message = "BLANK_PROVIDER_TYPE")
    String providerType
) {

  public String getRawToken() {
    return socialToken.split(" ")[1];
  }

}
