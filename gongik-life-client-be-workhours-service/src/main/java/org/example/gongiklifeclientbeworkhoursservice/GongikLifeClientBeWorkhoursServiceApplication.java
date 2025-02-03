package org.example.gongiklifeclientbeworkhoursservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeWorkhoursServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeWorkhoursServiceApplication.class, args);
  }

}
