package org.example.gongiklifeclientbegraphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeGraphqlApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeGraphqlApplication.class, args);
  }

}
