package com.fc.pass.fcspringbatch.batch;

public interface ItemWriter <O> {

    void write(O item);
}
