/*
 * 源代码文件版权申明：
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential
 * 版权申明 2019 福韵数据服务有限公司 保留所有权利
 * 未经授权，严禁复制泄露此文件
 */

package com.lgp.flink.connect.util;


import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Author : Li Guang Ping
 * Description : 环境配置工具类
 * Date : 20-03-10 上午11:08
 **/
public class EnvironmentUtil {
    private final static int SECONDS_30 = 30 * 1000;
    private final static int MINUTES_1 = 60 * 1000;

    public static StreamExecutionEnvironment createStreamEnvironment() {
        // 获取环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 检查点设置 每分钟启动一个检查点 , 每个检查点启动之间必须间隔30秒 , 检查点必须在5分钟内完成，或者被丢弃
        env.enableCheckpointing(MINUTES_1, CheckpointingMode.AT_LEAST_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(SECONDS_30);
        env.getCheckpointConfig().setCheckpointTimeout(MINUTES_1 * 5);
        // 设置两个分区处理任务
        env.setParallelism(2);

        // 固定间隔重启策略 最多重启3次,每次间隔2秒
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(2)));

        // 设置每一个算子在flink平台上都会以一个单独的模块展现。
        env.disableOperatorChaining();
        return env;
    }
}
