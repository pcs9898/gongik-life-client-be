package org.example.gongiklifeclientbeinstitutionservice.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.batch.listener.JobDurationListener;
import org.example.gongiklifeclientbeinstitutionservice.batch.processor.InstitutionItemProcessor;
import org.example.gongiklifeclientbeinstitutionservice.batch.reader.InstitutionItemReader;
import org.example.gongiklifeclientbeinstitutionservice.batch.writer.InstitutionItemWriter;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionWithDiseaseRestrictionsDto;
import org.example.gongiklifeclientbeinstitutionservice.repository.DiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionCategoryRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionTagRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.RegionalMilitaryOfficeRepository;
import org.example.gongiklifeclientbeinstitutionservice.util.RandomDoubleGenerator;
import org.example.gongiklifeclientbeinstitutionservice.util.RandomNumberGenerator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final RegionalMilitaryOfficeRepository regionalMilitaryOfficeRepository;
  private final InstitutionCategoryRepository institutionCategoryRepository;
  private final InstitutionTagRepository institutionTagRepository;
  private final InstitutionRepository institutionRepository;
  private final DiseaseRestrictionRepository diseaseRestrictionRepository;
  private final InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;
  private final DataCheckDecider dataCheckDecider;
  private final JobDurationListener jobDurationListener;
  private final RandomNumberGenerator randomNumberGenerator;
  private final RandomDoubleGenerator randomDoubleGenerator;

  @Bean
  public Job importInstitutionJob(Step SeedInstitutionsToPostgreSQLStep) {

    log.info("importInstitutionJob");

    return new JobBuilder("importInstitutionJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(dataCheckDecider)
        .on("NO_DATA").to(SeedInstitutionsToPostgreSQLStep)
        .from(dataCheckDecider).on("DATA_EXISTS").end()
        .build()
        .listener(jobDurationListener)
        .build();
  }

  @Bean
  public Step SeedInstitutionsToPostgreSQLStep(InstitutionItemReader reader,
      InstitutionItemProcessor processor,
      InstitutionItemWriter writer) {
    return new StepBuilder("SeedInstitutionsToPostgreSQLStep", jobRepository)
        .<String[], InstitutionWithDiseaseRestrictionsDto>chunk(10, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  @StepScope
  public InstitutionItemReader institutionItemReader() {
    return new InstitutionItemReader();
  }

  @Bean
  @StepScope
  public InstitutionItemProcessor institutionItemProcessor() {
    return new InstitutionItemProcessor(regionalMilitaryOfficeRepository,
        institutionCategoryRepository,
        institutionTagRepository, randomNumberGenerator, randomDoubleGenerator);
  }

  @Bean
  @StepScope
  public InstitutionItemWriter institutionItemWriter() {
    return new InstitutionItemWriter(institutionRepository,
        diseaseRestrictionRepository,
        institutionDiseaseRestrictionRepository);
  }
}