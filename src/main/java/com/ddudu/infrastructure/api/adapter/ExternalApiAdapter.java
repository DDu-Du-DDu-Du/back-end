package com.ddudu.infrastructure.api.adapter;

import com.ddudu.application.domain.authentication.dto.request.SocialRequest;
import com.ddudu.application.domain.user.domain.AuthProvider;
import com.ddudu.application.port.out.SocialResourcePort;
import com.ddudu.infrastructure.api.handler.SocialResourceHandler;
import com.ddudu.infrastructure.api.response.SocialResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalApiAdapter implements SocialResourcePort {

  private final SocialResourceHandler socialResourceHandler;

  @Override
  public AuthProvider retrieveSocialResource(SocialRequest request) {
    String providerType = request.providerType();
    SocialResource socialResource = socialResourceHandler.requestSocialResource(
        request.socialToken(), providerType);

    return AuthProvider.builder()
        .providerId(socialResource.socialId())
        .providerType(providerType)
        .build();
  }

}
