package com.ddudu.application.domain.user.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthProvider {

  private final ProviderType providerType;
  private final String providerId;

  @Builder
  public AuthProvider(String providerType, String providerId) {
    validate(providerId);

    this.providerType = ProviderType.from(providerType);
    this.providerId = providerId;
  }

  private void validate(String providerId) {
    checkArgument(StringUtils.isBlank(providerId), UserErrorCode.BLANK_PROVIDER_ID.name());
  }

}