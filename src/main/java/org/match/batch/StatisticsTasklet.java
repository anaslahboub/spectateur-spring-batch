package org.match.batch;



import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.match.service.StatisticsService;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@Slf4j
public class StatisticsTasklet implements Tasklet {

  private final StatisticsService  statisticsService;

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        statisticsService.calculateAllStatistics();
        return RepeatStatus.FINISHED;
    }

}