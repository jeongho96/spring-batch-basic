package com.fc.pass.fcspringbatch;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

//@Configuration
public class ItemWriterJobConfiguration {

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("itemReaderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<User> flatFileItemReader,
            ItemWriter<User> jpaItemWriter

    ){
        return new StepBuilder("step", jobRepository)
                .<User,User>chunk(2, transactionManager)
                .reader(flatFileItemReader)
                .writer(jpaItemWriter)
                .build();

    }

    @Bean
    public FlatFileItemReader<User> flatFileItemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("flatFileItemReader")
                .resource(new ClassPathResource("users.txt"))
                .linesToSkip(2) // 초반 2줄은 스킵.
                .delimited().delimiter(",") // 구분자를 결정.
                .names("name", "age", "region", "telephone")
                .targetType(User.class)
                .strict(true) // 해당 파일이 없어도 넘어가도 괜찮다면 이 옵션을 false를 줘도 됨.
                .build();
    }

    @Bean
    public ItemWriter<User> flatFileitemWriter(){
        return new FlatFileItemWriterBuilder<User>()
                .name("flatFileItemWriter")
                .resource(new PathResource("src/main/resources/new_users.txt"))
                .delimited().delimiter("__")
                .names("name", "age", "region", "telephone")
                .build();
    }

    @Bean
    public ItemWriter<User> flatFileItemWriter() {
        return new FlatFileItemWriterBuilder<User>()
                .name("flatFileItemWriter")
                .resource(new PathResource("src/main/resources/new_users.txt"))
                .delimited().delimiter("__")
                .names("name", "age", "region", "telephone")
                .build();
    }

    @Bean
    public ItemWriter<User> formattedFlatFileItemWriter() {
        return new FlatFileItemWriterBuilder<User>()
                .name("flatFileItemWriter")
                .resource(new PathResource("src/main/resources/new_formatted_users.txt"))
                .formatted()
                .format("%s의 나이는 %s입니다. 사는곳은 %s, 전화번호는 %s입니다.")
                .names("name", "age", "region", "telephone")
//                .shouldDeleteIfExists(false) // 존재하면 삭제하고
//                .append(true) // 동일한 값을 append 할 수 있음.(default는 false)
//                .shouldDeleteIfEmpty() // 비어있다면 그냥 삭제
                .build();
    }

    @Bean
    public JsonFileItemWriter<User> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<User>()
                .name("jsonFileItemWriter")
                .resource(new PathResource("src/main/resources/new_users.json"))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }

    @Bean
    public ItemWriter<User> jpaItemWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    // 대용량의 경우 jdbcBatchItemWriter를 사용.
    @Bean
    public ItemWriter<User> jdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO
                            USER(name, age, region, telephone)
                        VALUES
                            (:name, :age, :region, :telephone)
                        """)
                .beanMapped()
                .build();
    }
}
