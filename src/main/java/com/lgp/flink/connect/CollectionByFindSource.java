package com.lgp.flink.connect;

import com.lgp.flink.connect.bean.LabelBean;
import com.lgp.flink.connect.bean.LabelTest;
import com.lgp.flink.connect.source.RandomSource;
import com.lgp.flink.connect.source.SlowRandomSource;
import com.lgp.flink.connect.util.EnvironmentUtil;
import com.lgp.flink.connect.util.StateUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 如果没有广播源的对应数据
 * 此时从 来源 找一次
 */
public class CollectionByFindSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionByFindSource.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = EnvironmentUtil.createStreamEnvironment();
        DataStream<LabelBean> broadcastSource = broadcastSource(env);
        DataStream<LabelBean> unionSource = unionSource(env);

        BroadcastStream<LabelBean> broadcast = broadcastSource.broadcast(StateUtil.DESCRIPROT);

        unionSource.connect(broadcast)
                .process(new FindSource())
                .print();

        env.execute("从来源加载测试");
    }

    public static DataStream<LabelBean> broadcastSource(StreamExecutionEnvironment env) {
        return log(
                env.addSource(new SlowRandomSource("广播源"))
        );
    }

    /**
     * 打印广播流数据
     */
    public static DataStream<LabelBean> log(DataStream<LabelBean> broadcastSource) {
        return broadcastSource.map(new MapFunction<LabelBean, LabelBean>() {
            @Override
            public LabelBean map(LabelBean value) throws Exception {
                LOGGER.info("广播流数据" + value);
                return value;
            }
        });
    }

    public static DataStream<LabelBean> unionSource(StreamExecutionEnvironment env) {
        return env.addSource(new RandomSource("合并源"));
    }


    private static class FindSource extends BroadcastProcessFunction<LabelBean, LabelBean, LabelTest> {
        @Override
        public void processElement(LabelBean value, ReadOnlyContext ctx, Collector<LabelTest> out) throws Exception {
            String id = value.getId();
            String brocastValue = ctx.getBroadcastState(StateUtil.DESCRIPROT).get(id);
            if (brocastValue == null) {
                // 模拟后端查询 , 有可能查不到
                brocastValue = "查询得到的广播源数据";
            }
            if (brocastValue != null) {
                out.collect(
                        new LabelTest(id, brocastValue, value.getLabel())
                );
            }
        }

        @Override
        public void processBroadcastElement(LabelBean value, Context ctx, Collector<LabelTest> out) throws Exception {
            // 获取广播状态并更新状态数据
            ctx.getBroadcastState(StateUtil.DESCRIPROT)
                    .put(
                            value.getId(), value.getLabel()
                    );
        }
    }
}
