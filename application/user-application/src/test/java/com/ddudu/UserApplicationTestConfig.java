package com.ddudu;

import com.ddudu.application.common.port.auth.out.SocialResourcePort;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
    scanBasePackages = {
        "com.ddudu.application.user",
        "com.ddudu.application.common",
        "com.ddudu.infra",
        "com.ddudu.domain"
    }
)
public class UserApplicationTestConfig {

  @Bean
  SocialResourcePort socialResourcePort() {
    return (request) -> null;
  }

}
