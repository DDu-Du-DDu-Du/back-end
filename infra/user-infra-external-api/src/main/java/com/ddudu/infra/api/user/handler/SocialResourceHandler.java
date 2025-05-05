package com.ddudu.infra.api.user.handler;

import com.ddudu.infra.api.user.destination.SocialResourceDestination;
import com.ddudu.infra.api.user.operator.HttpOperator;
import com.ddudu.infra.api.user.response.KakaoResource;
import com.ddudu.infra.api.user.response.SocialResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialResourceHandler {

  private final ObjectMapper objectMapper;
  private final HttpOperator httpOperator;

  public SocialResource requestSocialResource(String accessToken, String providerType) {
    String destination = SocialResourceDestination.getDestinationBy(providerType);
    ResponseEntity<String> response = httpOperator.operateExchangeWithGetMethod(
        accessToken, destination);

    try {
      return objectMapper.readValue(response.getBody(), KakaoResource.class);
    } catch (JsonProcessingException e) {
      // TODO: specify exception
      throw new RuntimeException(e);
    }
  }

}
