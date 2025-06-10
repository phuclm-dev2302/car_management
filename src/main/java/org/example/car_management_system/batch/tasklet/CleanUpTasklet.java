package org.example.car_management_system.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class CleanUpTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        System.out.println("[Batch] Running daily cleanup task...");
        // TODO: Add cleanup logic here (e.g., delete old logs, inactive cars, etc.)
        return RepeatStatus.FINISHED;
    }
}

