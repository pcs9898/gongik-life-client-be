package org.example.gongiklifeclientbeworkhoursservice.batch.config;

import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbeworkhoursservice.batch.tasklet.AggregationTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {


  private final AggregationTasklet aggregationTasklet;

  @Bean
  public Step aggregateWorkhoursStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("aggregateWorkhoursStep", jobRepository)
        .tasklet(aggregationTasklet, transactionManager)
        .build();
  }

  @Bean
  public Job workhoursStatisticsJob(JobRepository jobRepository, Step aggregateWorkhoursStep) {
    return new JobBuilder("workhoursStatisticsJob", jobRepository)
        .start(aggregateWorkhoursStep)
        .build();
  }
}
