package com.ddudu.infra.fcm.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class FcmConfig {

  private final FcmProperties fcmProperties;

  @PostConstruct
  public void init() throws IOException {
    ClassPathResource resource = new ClassPathResource(fcmProperties.filePath());
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
        .setProjectId(fcmProperties.projectId())
        .build();

    FirebaseApp.initializeApp(options);
  }

  @Bean
  public FirebaseApp firebaseApp() {
    return FirebaseApp.getInstance();
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
    return FirebaseMessaging.getInstance(firebaseApp);
  }

}
