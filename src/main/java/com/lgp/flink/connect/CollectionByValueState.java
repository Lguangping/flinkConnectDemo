package com.lgp.flink.connect;

import com.lgp.flink.connect.bean.LabelBean;
import com.lgp.flink.connect.bean.LabelTest;
import com.lgp.flink.connect.util.EnvironmentUtil;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ValueState 实现连接流
 */
public class CollectionByValueState {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionByValueState.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = EnvironmentUtil.createStreamEnvironment();
        DataStream<LabelBean> broadcastSource =
                CollectionByFindSource.broadcastSource(env)
                        .keyBy(new KeyByLabel());
        DataStream<LabelBean> unionSource =
                CollectionByFindSource.unionSource(env)
                        .keyBy(new KeyByLabel());

        broadcastSource
                .connect(unionSource)
                .flatMap(new EnrichmentFunction())
                .print();

        env.execute("valueState实现测试");
    }

    private static class KeyByLabel implements KeySelector<LabelBean, String> {
        @Override
        public String getKey(LabelBean value) throws Exception {
            return value.getId();
        }
    }

    private static class EnrichmentFunction extends
            RichCoFlatMapFunction<LabelBean, LabelBean, LabelTest> {

        private ValueState<LabelBean> broadcastState;

        @Override
        public void open(Configuration parameters) {
            broadcastState = getRuntimeContext()
                    .getState(new ValueStateDescriptor<LabelBean>("广播", LabelBean.class));
        }

        @Override
        public void flatMap1(LabelBean label, Collector<LabelTest> out) throws Exception {
            broadcastState.update(label);
        }

        @Override
        public void flatMap2(LabelBean unionElement, Collector<LabelTest> out) throws Exception {
            LabelBean broadcastValue = broadcastState.value();
            String broadcastLabel = null;
            if (broadcastValue == null) {
                // 模拟后端查询 , 有可能查不到
                broadcastLabel = "查询得到的广播源数据";
            } else {
                broadcastLabel = broadcastValue.getLabel();
            }
            if (broadcastLabel != null) {
                out.collect(
                        new LabelTest(
                                unionElement.getId(),
                                broadcastLabel,
                                unionElement.getLabel()
                        )
                );
            }
        }
    }
}
