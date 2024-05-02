package com.fc.pass.fcspringbatch.batch;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class JobExecution {

    private BatchStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
