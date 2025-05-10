package com.ddudu;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        "com.ddudu.application.planning",
        "com.ddudu.infra",
        "com.ddudu.domain"
    }
)
public class PlanningApplicationTestConfig {

}
