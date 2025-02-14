package org.example.gongiklifeclientbereportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GongikLifeClientBeReportServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GongikLifeClientBeReportServiceApplication.class, args);
  }

}
