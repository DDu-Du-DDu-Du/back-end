package com.ddudu;

import com.ddudu.application.common.port.notification.out.NotificationSendPort;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
    scanBasePackages = {
        "com.ddudu.application.notification",
        "com.ddudu.infra",
        "com.ddudu.domain"
    }
)
public class NotificationApplicationTestConfig {

  @Bean
  public NotificationSendPort notificationSendPort() {
    return (deviceTokens, title, body) -> {};
  }

}
