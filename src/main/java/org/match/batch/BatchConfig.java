package org.match.batch;

import lombok.extern.slf4j.Slf4j;
import org.match.models.EntrySpectateur;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class BatchConfig {



    @Autowired
    SpectateurJsonReader jsonItemReader;
        @Bean
        public Step jsonStep(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             SpectateurProcessor process,
                             JpaItemWriter<EntrySpectateur> writer) {
            log.info("🔧 Configuration du step JSON avec chunk size: 10");
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
            log.info("🔧 Configuration du step XML avec chunk size: 4");
            return new StepBuilder("xmlStep", jobRepository)
                    .<EntrySpectateurDto, EntrySpectateur>chunk(4)
                    .reader(reader)
                    .processor(process)
                    .writer(writer)
                    .transactionManager(transactionManager)
                    .build();
        }

        @Bean
        public Step statisticsStep(JobRepository jobRepository,
                                   PlatformTransactionManager txManager,
                                   StatisticsTasklet statisticsTasklet) {

            log.info("🔧 Configuration du step de statistiques (tasklet)");

            return new StepBuilder("statisticsStep", jobRepository)
                    .tasklet(statisticsTasklet, txManager)
                    .build();
        }

        @Bean
        public Job runJob(JobRepository jobRepository, Step jsonStep, Step xmlStep, Step statisticsStep) {
            log.info("🚀 Configuration du Job Batch avec l'ordre: XML → JSON → Statistiques");
            log.info("📋 Détails du job:");
            log.info("   - Step 1: XML (chunk: 4)");
            log.info("   - Step 2: JSON (chunk: 10)");
            log.info("   - Step 3: Statistiques (tasklet)");

            return new JobBuilder("spectateurJob", jobRepository)
                    .start(xmlStep)
                    .next(jsonStep)
                    .next(statisticsStep)
                    .build();
        }
}
