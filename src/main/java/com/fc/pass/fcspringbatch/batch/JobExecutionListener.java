package com.fc.pass.fcspringbatch.batch;

public interface JobExecutionListener {

    void beforeJob(JobExecution jobExecution);
    void afterJob(JobExecution jobExecution);
}
