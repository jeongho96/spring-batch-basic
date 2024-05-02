package com.fc.pass.fcspringbatch.customer;

import com.fc.pass.fcspringbatch.batch.BatchStatus;
import com.fc.pass.fcspringbatch.batch.JobExecution;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;



@SpringBootTest
class DormantBatchJobTest {


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DormantBatchJob dormantBatchJob;

    @BeforeEach
    public void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 시간이 일년을 경과한 고객이 세명이고," +
            "일 년 이내에 로그인한 고객이 다섯명이면 3명의 고객이 휴먼전환대상이다.")
    void test1() {

        // given
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);

        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);

        // when
        // 배치 작업을 돌려서 저장된 휴먼 계정의 수가 3개여야 함.
        final JobExecution result = dormantBatchJob.execute();

        // then
        // DB의 저장된 값이 3개인지 확인.
        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Customer.Status.DORMANT)
                .count();

        // 같지 않다면 문제가 있음.
        Assertions.assertThat(dormantCount).isEqualTo(3);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    }



    @Test
    @DisplayName("고객이 10명이 있지만 모두 다 휴먼전환대상이면" +
            "휴먼전환 대상은 10명이다.")
    void test2() {
        // given
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);


        // when
        // 배치 작업을 돌려서 저장된 휴먼 계정의 수가 3개여야 함.
        final JobExecution result = dormantBatchJob.execute();

        // then
        // DB의 저장된 값이 3개인지 확인.
        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Customer.Status.DORMANT)
                .count();

        // 같지 않다면 문제가 있음.
        Assertions.assertThat(dormantCount).isEqualTo(10);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    }

    // null이나 0인 경우를 고려해야 버그가 잘 생기지 않음.
    @Test
    @DisplayName("고객이 없는 경우에도 배치는 정상동작해야한다.")
    void test3() {
        // given

        // when
        // 배치 작업을 돌려서 저장된 휴먼 계정의 수가 3개여야 함.
        final JobExecution result = dormantBatchJob.execute();

        // then
        // DB의 저장된 값이 3개인지 확인.
        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Customer.Status.DORMANT)
                .count();

        // 같지 않다면 문제가 있음.
        Assertions.assertThat(dormantCount).isEqualTo(0);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }


    private void saveCustomer(long loginMinusDays) {
        final String uuid = UUID.randomUUID().toString();
        final Customer test = new Customer(uuid, uuid + "@gmail.com");
        test.setLoginAt(LocalDateTime.now().minusDays(loginMinusDays));
        customerRepository.save(test);
    }

    @Test
    @DisplayName("배치가 실패하면 BatchStatus는 FAILED를 반환해야한다.")
    void test4() {

        // given
        final DormantBatchJob dormantBatchJob = new DormantBatchJob(null);

        // when
        final JobExecution result = dormantBatchJob.execute();

        // then
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.FAILED);
    }
}