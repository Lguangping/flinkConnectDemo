package com.lgp.flink.connect.bean;

import java.io.Serializable;

public class LabelBean implements Serializable {
    public LabelBean() {
    }

    public LabelBean(String id, String label) {
        this.id = id;
        this.label = label;
    }

    private String id;
    private String label;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "LabelBean{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
