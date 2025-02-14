package org.example.gongiklifeclientbemailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeMailServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeMailServiceApplication.class, args);
  }

}
