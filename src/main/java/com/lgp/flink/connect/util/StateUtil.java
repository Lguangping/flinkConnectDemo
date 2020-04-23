package com.lgp.flink.connect.util;

import org.apache.flink.api.common.state.MapStateDescriptor;

public class StateUtil {
    public static final MapStateDescriptor<String, String> DESCRIPROT = new MapStateDescriptor<>("collectionState", String.class, String.class);
}
