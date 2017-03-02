package com.cooltoo.go2nurse.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 01/03/2017.
 */
public class PatientSymptomItem {
    public static final String DATA_TYPE_STRING = "string";
    public static final String DATA_TYPE_INT    = "int";
    public static final String DATA_TYPE_FLOAT  = "float";

    private int id;
    private String dataType = "";
    private String name  = "";
    private Object value = null;
    private String unit  = "";
    private String maxNum;
    private String minNum;

    public static final List<String> allDataType() {
        List<String> dataType = new ArrayList<>();
        dataType.add(DATA_TYPE_STRING);
        dataType.add(DATA_TYPE_INT);
        dataType.add(DATA_TYPE_FLOAT);
        return dataType;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMaxNum() {
        return maxNum;
    }
    public void setMaxNum(String maxNum) {
        this.maxNum = maxNum;
    }

    public String getMinNum() {
        return minNum;
    }
    public void setMinNum(String minNum) {
        this.minNum = minNum;
    }

    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
