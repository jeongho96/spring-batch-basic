package com.fc.pass.fcspringbatch.batch;

public interface ItemProcessor <I,O>{

    O process(I item);
}
