package com.ddudu;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        "com.ddudu.application.notification",
        "com.ddudu.infra",
        "com.ddudu.domain"
    }
)
public class NotificationApplicationTestConfig {

}
