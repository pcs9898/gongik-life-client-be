package org.example.gongiklifeclientbegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeGatewayApplication.class, args);
  }

}
