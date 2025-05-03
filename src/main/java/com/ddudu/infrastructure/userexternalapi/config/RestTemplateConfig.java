package com.ddudu.infrastructure.userexternalapi.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(10))
        .setReadTimeout(Duration.ofSeconds(10))
        .additionalInterceptors((this::retry))
        .build();
  }

  // TODO: dependencies clash with one in primary adapter (spring web & security).
  //  Module separation necessary.
  private ClientHttpResponse retry(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution
  ) {
    RetryTemplate retryTemplate = new RetryTemplate();

    try {
      return retryTemplate.execute(context -> execution.execute(request, body));
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

}
