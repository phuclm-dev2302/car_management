package org.example.car_management_system.batch.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job cleanUpJob;

    @Scheduled(cron = "0 0 1 * * ?") // Mỗi ngày lúc 01:00 AM
    public void runDailyCleanUpJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(cleanUpJob, jobParameters);
            System.out.println("[Cron] Clean-up job triggered.");
        } catch (Exception e) {
            System.err.println("Failed to run cleanUpJob: " + e.getMessage());
        }
    }
}

