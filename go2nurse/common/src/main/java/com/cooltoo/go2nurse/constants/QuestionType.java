package com.cooltoo.go2nurse.constants;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public enum QuestionType {
    OTHER,
    SINGLE_SELECTION,  // 单选
    MULTI_SELECTION,    // 多选
    BMI, // 体质指数(Body_Mass_Index)
    ;

    private static final Logger logger = LoggerFactory.getLogger(QuestionType.class);

    public static QuestionType parseString(String type) {
        QuestionType enumeration = null;
        if (OTHER.name().equalsIgnoreCase(type)) {
            enumeration = OTHER;
        }
        else if (SINGLE_SELECTION.name().equalsIgnoreCase(type)) {
            enumeration = SINGLE_SELECTION;
        }
        else if (MULTI_SELECTION.name().equalsIgnoreCase(type)) {
            enumeration = MULTI_SELECTION;
        }
        else if (BMI.name().equalsIgnoreCase(type)) {
            enumeration = BMI;
        }
        return enumeration;
    }

    public static QuestionType parseInt(int type) {
        QuestionType enumeration = null;
        if (OTHER.ordinal() == type) {
            enumeration = OTHER;
        }
        else if (SINGLE_SELECTION.ordinal() == type) {
            enumeration = SINGLE_SELECTION;
        }
        else if (MULTI_SELECTION.ordinal() == type) {
            enumeration = MULTI_SELECTION;
        }
        else if (BMI.ordinal() == type) {
            enumeration = BMI;
        }
        return enumeration;
    }

    public static boolean exists(int type) {
        return null!=parseInt(type);
    }

    public static List<QuestionType> getAll() {
        List<QuestionType> enumerations = new ArrayList<>();
        enumerations.add(OTHER);
        enumerations.add(SINGLE_SELECTION);
        enumerations.add(MULTI_SELECTION);
        enumerations.add(BMI);
        return enumerations;
    }
//
//    public static List<QuestionType> getByTypes(List<Long> types) {
//        List<QuestionType> retVal = new ArrayList<>();
//        if (null==types || types.isEmpty()) {
//            return  retVal;
//        }
//        for (Long type : types) {
//            QuestionType de = parseInt(type.intValue());
//            if (null==de) {
//                continue;
//            }
//            retVal.add(de);
//        }
//        return retVal;
//    }
//
//    public static boolean needCalculate(QuestionType type) {
//        boolean needCalculate = false;
//        if(BMI.equals(type)) {
//            needCalculate = true;
//        }
//        return needCalculate;
//    }
//    public static double calculateIndex(QuestionType type, List<Double> args) {
//        if (BMI.equals(type)) {
//            if (null==args || args.size()!=2) {
//                logger.error("BMI param={} is not valid", args);
//                throw new BadRequestException(ErrorCode.DATA_ERROR);
//            }
//            double weight = args.get(0);
//            double height = args.get(1);
//            logger.info("weight={} height={}", weight, height);
//            return weight/(height*height);
//        }
//        return Double.NaN;
//    }
}
