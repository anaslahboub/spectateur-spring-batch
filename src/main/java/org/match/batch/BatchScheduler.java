package org.match.batch;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job spectateurJob;

    public BatchScheduler(JobLauncher jobLauncher, Job spectateurJob) {
        this.jobLauncher = jobLauncher;
        this.spectateurJob = spectateurJob;
    }

    /**
     * Lance le batch toutes les 15 secondes
     * fixedDelay = attend 15s après la fin de l'exécution précédente
     * fixedRate = lance toutes les 15s (même si le précédent n'est pas terminé)
     */
    @Scheduled(fixedDelay = 600_000) // 15000 ms = 15 secondes
    public void launchBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())   // UNIQUE
                    .toJobParameters();

            System.out.println("🚀 Lancement du batch à : " + LocalDateTime.now());

            jobLauncher.run(spectateurJob, jobParameters);

            System.out.println("✅ Batch terminé avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'exécution du batch : " + e.getMessage());
            e.printStackTrace();
        }
    }
}