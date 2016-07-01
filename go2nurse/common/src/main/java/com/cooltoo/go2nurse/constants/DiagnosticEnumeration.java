package com.cooltoo.go2nurse.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/6/13.
 */
public enum DiagnosticEnumeration {

    EXTENSION_NURSING,      // 健康宣教
    HOSPITALIZED_DATE,      // 入院
    PHYSICAL_EXAMINATION,   // 检查
    OPERATION,                  // 手术
    DISCHARGED_FROM_THE_HOSPITAL    // 出院
    ;

    public static DiagnosticEnumeration parseString(String type) {
        DiagnosticEnumeration diagnostic = null;
        if (EXTENSION_NURSING.name().equalsIgnoreCase(type)) {
            diagnostic = EXTENSION_NURSING;
        }
        else if (HOSPITALIZED_DATE.name().equalsIgnoreCase(type)) {
            diagnostic = HOSPITALIZED_DATE;
        }
        else if (PHYSICAL_EXAMINATION.name().equalsIgnoreCase(type)) {
            diagnostic = PHYSICAL_EXAMINATION;
        }
        else if (OPERATION.name().equalsIgnoreCase(type)) {
            diagnostic = OPERATION;
        }
        else if (DISCHARGED_FROM_THE_HOSPITAL.name().equalsIgnoreCase(type)) {
            diagnostic = DISCHARGED_FROM_THE_HOSPITAL;
        }
        return diagnostic;
    }

    public static DiagnosticEnumeration parseInt(int type) {
        DiagnosticEnumeration diagnostic = null;
        if (EXTENSION_NURSING.ordinal() == type) {
            diagnostic = EXTENSION_NURSING;
        }
        else if (HOSPITALIZED_DATE.ordinal() == type) {
            diagnostic = HOSPITALIZED_DATE;
        }
        else if (PHYSICAL_EXAMINATION.ordinal() == type) {
            diagnostic = PHYSICAL_EXAMINATION;
        }
        else if (OPERATION.ordinal() == type) {
            diagnostic = OPERATION;
        }
        else if (DISCHARGED_FROM_THE_HOSPITAL.ordinal() == type) {
            diagnostic = DISCHARGED_FROM_THE_HOSPITAL;
        }
        return diagnostic;
    }

    public static boolean exists(int type) {
        return null!=parseInt(type);
    }

    public static List<DiagnosticEnumeration> getAllDiagnostic() {
        List<DiagnosticEnumeration> diagnostics = new ArrayList<>();
        diagnostics.add(EXTENSION_NURSING);
        diagnostics.add(HOSPITALIZED_DATE);
        diagnostics.add(PHYSICAL_EXAMINATION);
        diagnostics.add(OPERATION);
        diagnostics.add(DISCHARGED_FROM_THE_HOSPITAL);
        return diagnostics;
    }

    public static List<DiagnosticEnumeration> getDiagnosticByTypes(List<Long> types) {
        List<DiagnosticEnumeration> retVal = new ArrayList<>();
        if (null==types || types.isEmpty()) {
            return  retVal;
        }
        for (Long type : types) {
            DiagnosticEnumeration de = parseInt(type.intValue());
            if (null==de) {
                continue;
            }
            retVal.add(de);
        }
        return retVal;
    }
}
