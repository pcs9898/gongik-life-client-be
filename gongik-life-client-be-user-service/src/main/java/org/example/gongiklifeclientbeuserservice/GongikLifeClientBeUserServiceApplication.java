package org.example.gongiklifeclientbeuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeUserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeUserServiceApplication.class, args);
  }

}
