package org.example.gongiklifeclientbeinstitutionservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@Slf4j
@EnableElasticsearchRepositories(basePackages = "org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.data.elasticsearch.url}")
  private String url;


  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(url)
        .build();
  }

}