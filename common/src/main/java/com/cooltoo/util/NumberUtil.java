package com.cooltoo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by lg380357 on 2016/3/2.
 */
public class NumberUtil {
    /**
     * ChinaMobile:134,135,136,137,138,139,150,151,157(TD),158,159,187,188
     * ChinaUnicom:130,131,132,152,155,156,185,186
     * ChinaTelecom:133,153,180,189,(1349)
     */
    private static final String MobileRegex = "^[1][358]\\d{9}";
    public static boolean isMobileValid(String mobile) {
        if (mobile instanceof String) {
            return mobile.matches(MobileRegex);
        }
        return false;
    }

    private static final String IdentificationRegex="^\\d{15}|\\d{18}|\\d{17}[\\dXx]{1}$";
    public static boolean isIdentificationValid(String identification) {
        if (identification instanceof String) {
            return identification.matches(IdentificationRegex);
        }
        return false;
    }

    public static final String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static long getTime(String datetime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            java.util.Date time = sdf.parse(datetime);
            return time.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static BigDecimal getDecimal(String decimal, int scale) {
        try {
            BigDecimal instance = new BigDecimal(decimal);
            BigDecimal one      = new BigDecimal("1");
            return instance.divide(one, scale, BigDecimal.ROUND_HALF_UP);
        }
        catch (Exception ex) {
            return null;
        }
    }
}
