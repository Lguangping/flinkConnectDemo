package com.lgp.flink.connect.source;

import com.lgp.flink.connect.bean.LabelBean;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomSource implements SourceFunction<LabelBean> {
    private String label;

    public RandomSource(String label) {
        this.label = label;
    }

    private Random random = new Random();
    private boolean isRunning = true;
    private final int SLEEP_MAX = 10;

    /**
     * 睡眠倍数
     * @return
     */
    protected int sleepMultiple() {
        return 1;
    }

    @Override
    public void run(SourceContext<LabelBean> sct) throws Exception {
        while (isRunning) {
            int randomInt = random.nextInt(SLEEP_MAX);
            TimeUnit.SECONDS.sleep(randomInt * sleepMultiple());
            sct.collect(
                    new LabelBean("id:" + randomInt, label)
            );
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}