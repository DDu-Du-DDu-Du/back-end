package com.modoo;

import com.modoo.application.common.port.auth.out.SocialResourcePort;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
    scanBasePackages = {
        "com.modoo.application.user",
        "com.modoo.application.common",
        "com.modoo.infra",
        "com.modoo.domain"
    }
)
public class UserApplicationTestConfig {

  @Bean
  SocialResourcePort socialResourcePort() {
    return (request) -> null;
  }

}
