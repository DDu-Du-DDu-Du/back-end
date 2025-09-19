package com.ddudu.infra.fcm.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fcm")
public record FcmProperties(String filePath, String projectId, boolean validateOnly) {

}
