package com.modoo;

import com.modoo.application.common.port.notification.out.NotificationSendPort;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
    scanBasePackages = {
        "com.modoo.application.notification",
        "com.modoo.infra",
        "com.modoo.domain"
    }
)
public class NotificationApplicationTestConfig {

  @Bean
  public NotificationSendPort notificationSendPort() {
    return (deviceTokens, title, body) -> {
    };
  }

}
