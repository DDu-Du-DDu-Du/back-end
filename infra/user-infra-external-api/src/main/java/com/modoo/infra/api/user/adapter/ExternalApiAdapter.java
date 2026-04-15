package com.modoo.infra.api.user.adapter;

import com.modoo.application.common.dto.auth.request.SocialRequest;
import com.modoo.application.common.port.auth.out.SocialResourcePort;
import com.modoo.common.annotation.DrivenAdapter;
import com.modoo.domain.user.user.aggregate.vo.AuthProvider;
import com.modoo.infra.api.user.handler.SocialResourceHandler;
import com.modoo.infra.api.user.response.SocialResource;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class ExternalApiAdapter implements SocialResourcePort {

  private final SocialResourceHandler socialResourceHandler;

  @Override
  public AuthProvider retrieveSocialResource(SocialRequest request) {
    String providerType = request.providerType();
    SocialResource socialResource = socialResourceHandler.requestSocialResource(
        request.getRawToken(),
        providerType
    );

    return AuthProvider.builder()
        .providerId(socialResource.id())
        .providerType(providerType)
        .build();
  }

}
