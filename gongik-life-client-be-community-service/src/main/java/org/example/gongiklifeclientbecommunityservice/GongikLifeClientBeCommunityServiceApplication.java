package org.example.gongiklifeclientbecommunityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeCommunityServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeCommunityServiceApplication.class, args);
  }

}
