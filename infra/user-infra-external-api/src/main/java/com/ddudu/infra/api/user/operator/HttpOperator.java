package com.ddudu.infra.api.user.operator;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpOperator {

  private final RestTemplate restTemplate;

  public ResponseEntity<String> operateExchangeWithGetMethod(
      String accessToken,
      String destination
  ) {
    HttpHeaders headers = new HttpHeaders();

    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

    return restTemplate.exchange(destination, HttpMethod.GET, request, String.class);
  }

}
