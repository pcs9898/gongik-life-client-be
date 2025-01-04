package org.example.gongiklifeclientbediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GongikLifeClientBeDiscoveryApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeDiscoveryApplication.class, args);
  }

}
