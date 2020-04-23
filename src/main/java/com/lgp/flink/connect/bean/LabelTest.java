package com.lgp.flink.connect.bean;

import java.io.Serializable;

public class LabelTest implements Serializable {
    private String id;
    private String broadcast;
    private String union;

    public LabelTest() {
    }

    public LabelTest(String id, String broadcast, String union) {
        this.id = id;
        this.broadcast = broadcast;
        this.union = union;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    public String getUnion() {
        return union;
    }

    public void setUnion(String union) {
        this.union = union;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LabelTest{" +
                "id='" + id + '\'' +
                ", broadcast='" + broadcast + '\'' +
                ", union='" + union + '\'' +
                '}';
    }
}
