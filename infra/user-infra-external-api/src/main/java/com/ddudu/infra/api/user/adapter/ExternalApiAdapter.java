package com.ddudu.infra.api.user.adapter;

import com.ddudu.application.dto.auth.request.SocialRequest;
import com.ddudu.application.port.auth.out.SocialResourcePort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.infra.api.user.handler.SocialResourceHandler;
import com.ddudu.infra.api.user.response.SocialResource;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class ExternalApiAdapter implements SocialResourcePort {

  private final SocialResourceHandler socialResourceHandler;

  @Override
  public AuthProvider retrieveSocialResource(SocialRequest request) {
    String providerType = request.providerType();
    SocialResource socialResource = socialResourceHandler.requestSocialResource(
        request.getRawToken(), providerType);

    return AuthProvider.builder()
        .providerId(socialResource.id())
        .providerType(providerType)
        .build();
  }

}
