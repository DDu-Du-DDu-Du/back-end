package com.ddudu.infrastructure.userexternalapi.adapter;

import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.application.user.auth.dto.request.SocialRequest;
import com.ddudu.application.user.auth.port.out.SocialResourcePort;
import com.ddudu.application.common.annotation.DrivenAdapter;
import com.ddudu.infrastructure.userexternalapi.handler.SocialResourceHandler;
import com.ddudu.infrastructure.userexternalapi.response.SocialResource;
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
