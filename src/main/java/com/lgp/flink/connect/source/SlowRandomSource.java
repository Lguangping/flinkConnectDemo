package com.lgp.flink.connect.source;

public class SlowRandomSource extends RandomSource {
    public SlowRandomSource(String label) {
        super(label);
    }

    @Override
    protected int sleepMultiple() {
        return 3;
    }
}