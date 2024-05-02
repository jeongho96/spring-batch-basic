package com.fc.pass.fcspringbatch.application;

import com.fc.pass.fcspringbatch.batch.ItemProcessor;
import com.fc.pass.fcspringbatch.customer.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DormantBatchItemProcessor implements ItemProcessor<Customer, Customer> {


    @Override
    public Customer process(Customer item) {

        // 로그인 날짜       /   365일 전      /   오늘
        // 오늘로부터 365일전이 로그인 날짜보다 이후면 휴먼 대상
        final boolean isDormantTarget = LocalDate.now()
                .minusDays(365)
                .isAfter(item.getLoginAt().toLocalDate());

        if(isDormantTarget){
            item.setStatus(Customer.Status.DORMANT);
            return item;
        } else{
            return null;
        }
    }
}
