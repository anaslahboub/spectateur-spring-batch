package org.match.batch;

import org.match.models.EntrySpectateur;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.json.JsonItemReader;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Step jsonStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         ItemReader<EntrySpectateurDto> jsonItemReader,
                         SpectateurProcessor process,
                         JpaItemWriter<EntrySpectateur> writer) {

        return new StepBuilder("jsonStep", jobRepository)
                .<EntrySpectateurDto, EntrySpectateur>chunk(10)
                .reader(jsonItemReader)
                .processor(process)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step xmlStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        StaxEventItemReader<EntrySpectateurDto> reader,
                        SpectateurProcessor process,
                        JpaItemWriter<EntrySpectateur> writer) {

        return new StepBuilder("xmlStep", jobRepository)
                .<EntrySpectateurDto, EntrySpectateur>chunk(4)
                .reader(reader)
                .processor(process)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, Step jsonStep, Step xmlStep) {
        return new JobBuilder("spectateurJob", jobRepository)
                .start(xmlStep)
                .next(jsonStep)
                .build();
    }
}
