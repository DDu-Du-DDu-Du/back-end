package com.modoo;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        "com.modoo.application.planning",
        "com.modoo.infra",
        "com.modoo.domain"
    }
)
public class PlanningApplicationTestConfig {

}
