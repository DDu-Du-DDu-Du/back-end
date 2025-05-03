package com.ddudu.domain.user.user.aggregate.vo;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.domain.user.user.aggregate.enums.ProviderType;
import com.ddudu.domain.user.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthProvider {

  @Getter(AccessLevel.NONE)
  private final ProviderType providerType;
  private final String providerId;

  @Builder
  private AuthProvider(String providerType, String providerId) {
    validate(providerId);

    this.providerType = ProviderType.from(providerType);
    this.providerId = providerId;
  }

  public String getProviderType() {
    return providerType.name();
  }

  private void validate(String providerId) {
    checkArgument(
        StringUtils.isNotBlank(providerId), UserErrorCode.BLANK_PROVIDER_ID.getCodeName());
  }

}
