package org.example.gongiklifeclientbenotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeNotificationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeNotificationServiceApplication.class, args);
  }

}
