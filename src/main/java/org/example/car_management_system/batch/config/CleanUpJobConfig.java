//package org.example.car_management_system.batch.config;
//
//import lombok.RequiredArgsConstructor;
//import org.example.car_management_system.batch.tasklet.CleanUpTasklet;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@RequiredArgsConstructor
//public class CleanUpJobConfig {
//    private final CleanUpTasklet cleanUpTasklet;
//
//    @Bean
//    public Job myBatchJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new JobBuilder("cleanUpJob", jobRepository)
//                .start(myBatchJobStep(jobRepository, transactionManager)) // <-- goji step
//                .build();
//    }
//
//    @Bean
//    public Step myBatchJobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new StepBuilder("cleanUpStep", jobRepository)
//                .tasklet(cleanUpTasklet, transactionManager)
//                .build();
//    }
//}
