package com.fc.pass.fcspringbatch.application;

import com.fc.pass.fcspringbatch.batch.ItemWriter;
import com.fc.pass.fcspringbatch.customer.Customer;
import com.fc.pass.fcspringbatch.customer.CustomerRepository;
import com.fc.pass.fcspringbatch.customer.EmailProvider;
import org.springframework.stereotype.Component;

@Component
public class DormantBatchItemWriter implements ItemWriter<Customer> {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public DormantBatchItemWriter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    @Override
    public void write(Customer item){
        customerRepository.save(item);
        // 4. 메일을 보낸다.
        emailProvider.send(item.getEmail(), "휴먼전환 안내메일입니다.", "내용");
    }
}
