package com.modoo.infra.api.user.operator;

import com.modoo.common.util.HttpLogAction;
import com.modoo.common.util.LogUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
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
    HttpMethod method = HttpMethod.GET;

    log.info(
        "{} method={} url={}",
        HttpLogAction.REQ.externalPrefix(),
        method,
        destination
    );

    long start = System.currentTimeMillis();

    ResponseEntity<String> response = restTemplate.exchange(
        destination,
        method,
        request,
        String.class
    );

    long durationMs = System.currentTimeMillis() - start;
    String prefix = HttpLogAction.RES.externalPrefix();

    if (LogUtil.isSlow(durationMs)) {
      prefix = HttpLogAction.SLOW.externalPrefix();
    }

    if (response.getStatusCode()
        .isError()) {
      prefix = HttpLogAction.ERR.externalPrefix();
    }

    log.info(
        "{} method={} url={} status={} durationMs={}",
        prefix,
        method,
        destination,
        response.getStatusCode(),
        durationMs
    );

    return response;
  }

}
