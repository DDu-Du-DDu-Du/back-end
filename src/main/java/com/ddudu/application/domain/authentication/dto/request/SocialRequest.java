package com.ddudu.application.domain.authentication.dto.request;

public record SocialRequest(String socialToken, String providerType) {

  public String getRawToken() {
    return socialToken.split(" ")[1];
  }

}
