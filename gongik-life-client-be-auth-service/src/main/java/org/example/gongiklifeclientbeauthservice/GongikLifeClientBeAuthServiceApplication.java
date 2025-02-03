package org.example.gongiklifeclientbeauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeAuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeAuthServiceApplication.class, args);
  }

}
