package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/25.
 */
public enum ServiceVendorType {
    NONE        // 未设置
    , COMPANY    // 公司
    , HOSPITAL // 医院
    ;

    public static ServiceVendorType parseString(String type) {
        ServiceVendorType ret = null;
        if (COMPANY.name().equalsIgnoreCase(type)) {
            ret = COMPANY;
        } else if (HOSPITAL.name().equalsIgnoreCase(type)) {
            ret = HOSPITAL;
        } else if (NONE.name().equalsIgnoreCase(type)) {
            ret = NONE;
        }
        return ret;
    }

    public static ServiceVendorType parseInt(int type) {
        ServiceVendorType ret = null;
        if (COMPANY.ordinal()==type) {
            ret = COMPANY;
        } else if (HOSPITAL.ordinal()==type) {
            ret = HOSPITAL;
        } else if (NONE.ordinal()==type) {
            ret = NONE;
        }
        return ret;
    }

}
