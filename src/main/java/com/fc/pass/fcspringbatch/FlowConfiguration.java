package com.fc.pass.fcspringbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
public class FlowConfiguration {

    @Bean
    public Job flowJob(
            JobRepository jobRepository,
            Step step1,
            Step step2,
            Step step3
    ){
        return new JobBuilder("flowJob", jobRepository)
                // 일반적인 실행 방법.
                /*.start(step1)
                .next(step2)
                .next(step3)*/
                // step flow 방식.
                .start(step1)
                .on("*").to(step2) //  .stopAndRestart(step 이름) : 멈추고 재실행(만약 이미 성공한 step1이 있어도 step2는 실행가능)
                .from(step1)
//                .on("FAILED").to(step3) // 실패 시 step3로
                .on("FAILED").end() // 실패 해도 그냥 완성. .end() 완성 / .fail() 실패처리 /
                .end()
                .build();

    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((a,b) ->{
                    log.info("step1 실행");
                    return null;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet((a,b) ->{
                    log.info("step2 실행");
                    return null;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet((a,b) ->{
                    log.info("step3 실행");
                    return null;
                }, transactionManager)
                .build();
    }

}
