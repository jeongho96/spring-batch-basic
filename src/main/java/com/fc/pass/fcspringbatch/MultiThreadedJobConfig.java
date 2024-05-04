package com.fc.pass.fcspringbatch;


import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
//@Configuration
public class MultiThreadedJobConfig {


    @Bean
    public Job job(
            JobRepository jobRepository,
            Step step
    ){

        return new JobBuilder("multiThreadJob",jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }


    @Bean
    public Step step(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<User> jpaPagingItemReader
    ){
        return new StepBuilder("step", jobRepository)
                .<User,User>chunk(5, transactionManager)
                .reader(jpaPagingItemReader)
                .writer(result -> log.info(result.toString()))
                .taskExecutor(new SimpleAsyncTaskExecutor()) // 병렬 처리
                .build();
    }

    @Bean
    public JpaPagingItemReader<User> jpaPagingItemReader(
            EntityManagerFactory entityManagerFactory
    ){
        return new JpaPagingItemReaderBuilder<User>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(5)
                // 일반 방식이면 앞에 과정이 성공했으면 실패한 구간부터 수행하면 되니까 이걸 true하면 기록할 수 있어서 좋음.
                // 그러나 multithread는 병렬로 수행되기 때문에 1 ~ 5가 예를 들어 성공했다고 하더라도 앞이나 뒤가 성공했다고 보장 x
                // 가능하면 multithread의 경우 false로 구현.
                .saveState(false)
                .queryString("SELECT u FROM User u ORDER BY u.id")
                .build();
    }


}
