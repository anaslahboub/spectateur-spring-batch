package org.match.batch;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job spectateurJob;

    public BatchScheduler(JobLauncher jobLauncher, Job spectateurJob) {
        this.jobLauncher = jobLauncher;
        this.spectateurJob = spectateurJob;
    }


    @Scheduled(fixedDelay = 600_000)
    public void launchBatchJob() {
        try {
                log.info("Démarrage de l'exécution du batch - ID: {}", System.currentTimeMillis());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(spectateurJob, jobParameters);

            log.info("✅ Batch terminé avec succès");

        } catch (Exception e) {

            log.error("❌ Erreur lors de l'exécution du batch - ID: {}", System.currentTimeMillis(), e);
            e.printStackTrace();
        }
    }
}