package com.fc.pass.fcspringbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
//@Configuration // 스프링에서는 Job이 2개가 있으면 멀티플 잡이라고 실행을 막음.
public class JobConfiguration {

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("job-chunk", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    @JobScope // job에 있는 jobparameter를 전달받기 위해 필요함.
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager
    , @Value("#{jobParameters['name']}") String name) {
        log.info("name : {}", name);
        return new StepBuilder("step", jobRepository)
                .tasklet((a,b) -> RepeatStatus.FINISHED, transactionManager)
                .build();
    }

    // tasklet 기반 step 수행.
    /*    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        final Tasklet tasklet = new Tasklet() {
            private int count = 0;

            @Override
            public RepeatStatus execute(StepContribution a, ChunkContext b) throws Exception {
                count++;

                if(count == 15){
                    log.info("Tasklet FINISHED");
                    return RepeatStatus.FINISHED;
                }

                log.info("Tasklet CONTINUABLE {}", count);
                return RepeatStatus.CONTINUABLE;
            }
        };

        return new StepBuilder("step", jobRepository)
                .tasklet(tasklet, transactionManager) // tasklet 기반 스탭 수행.
                .build();
    }*/

    // 완료된 step을 재실행하는 코드
    /*@Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager){

        return new StepBuilder("step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                   log.info("step 실행");
                   return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true) // 성공했어도 재시작.
                .startLimit(5)
                .build();
    }*/

    // step의 다양한 기능 확인.
    /* @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager){

        final ItemReader<Integer> itemReader = new ItemReader<>(){
            private int count = 0;

            @Override
            public Integer read() {
                count++;

                log.info("Read {}", count);

                if(count == 20)
                    return null;

                if(count >= 15)
                    throw new IllegalStateException("예외 발생.");



                return count;
            }
        };

        // 람다식과 기본 버전. (skipPolicy가 복잡하면 이렇게 정의해주자.
//        final SkipPolicy skipPolicy = new SkipPolicy(){
//            @Override
//            public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
//
//                return t instanceof IllegalStateException && skipCount < 5;
//            }
//        };
//        final SkipPolicy skipPolicy = (t, skipCount) -> t instanceof IllegalStateException && skipCount < 5;

        final ItemProcessor<Integer, Integer> itemProcessor = new ItemProcessor<>(){
            @Override
            public Integer process(Integer item) throws Exception {

                if(item == 15)
                    throw new IllegalStateException();


                return item;

            }
        };

        return new StepBuilder("step", jobRepository)
                .<Integer,Integer>chunk(10, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(read -> {})
                .faultTolerant() // skip, norollback, noskip, retry 관련 기능을 쓰기 위해 선언.
//                .skip(IllegalArgumentException.class) // 간단한 예외상황일 때는 예외 사유와 그 상황을 스킵하는 횟수만 적으면 됨.
//                .skipLimit(5)
//                .skipPolicy((t, skipCount) -> t instanceof IllegalStateException && skipCount < 5)
//                .noRollback(IllegalStateException.class) // 롤백관련 옵션은 이거 하나.
                .retry(IllegalStateException.class)
                .retryLimit(5)
                .build();
    }*/


    // 기본적인 chunk 구조의 코드.
    /*@Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager){

        final ItemReader<Integer> itemReader = new ItemReader<>(){
            private int count = 0;

            @Override
            public Integer read() {
                count++;

                log.info("Read {}", count);

                if(count == 15)
                    return null;

                return count;
            }
        };

        return new StepBuilder("step", jobRepository)
                .chunk(10, transactionManager)
                .reader(itemReader)
                .processor()
                .writer(read -> {})
                .build();
    }*/


}
