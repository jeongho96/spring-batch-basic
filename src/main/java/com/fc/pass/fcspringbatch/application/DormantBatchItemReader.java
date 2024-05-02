package com.fc.pass.fcspringbatch.application;

import com.fc.pass.fcspringbatch.batch.ItemReader;
import com.fc.pass.fcspringbatch.customer.Customer;
import com.fc.pass.fcspringbatch.customer.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


@Component
public class DormantBatchItemReader implements ItemReader<Customer> {
    private final CustomerRepository customerRepository;
    private int pageNo = 0;

    public DormantBatchItemReader(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer read(){
        final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending());
            final Page<Customer> page = customerRepository.findAll(pageRequest);

            final Customer customer;

            if(page.isEmpty()){
                pageNo = 0;
                return null;
            }
            else {
                pageNo++;
                customer = page.getContent().get(0);
            }

        return null;
    }
}
