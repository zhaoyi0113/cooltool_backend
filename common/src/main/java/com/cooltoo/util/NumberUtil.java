package com.cooltoo.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    public static final String TIME_HH_MM = "HH:mm";
    public static final String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_YYYY_MM_DD = "yyyy-MM-dd";
    public static long getHourMin(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_HH_MM);
        try {
            java.util.Date time = sdf.parse(datetime);
            return time.getTime();
        } catch (Exception e) {
            return Long.MIN_VALUE;
        }
    }
    public static long getTime(String datetime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            java.util.Date time = sdf.parse(datetime);
            return time.getTime();
        } catch (Exception e) {
            return -1;
        }
    }

    public static String timeToString(Date time, String pattern) {
        if (null==time) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.format(time);
        } catch (Exception e) {
            return "";
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

    private static final String PRICE="^\\d{0,7}\\.{0,1}\\d{0,2}$";
    public static Integer getCent(String price) {
        if (price instanceof String) {
            if (!price.matches(PRICE)) {
                return null;
            }
            StringBuilder strCent = new StringBuilder(price);
            char[] cPrice = price.toCharArray();
            int dotIndex = price.indexOf('.');
            if (dotIndex>=0) {
                if (dotIndex+3!=cPrice.length) {
                    int zeroAdded = dotIndex + 3 - cPrice.length;
                    for (int i = 0; i < zeroAdded; i++) {
                        strCent.append("0");
                    }
                }
            }
            else {
                strCent.append(".00");
                dotIndex = strCent.toString().indexOf('.');
            }
            strCent.deleteCharAt(dotIndex);

            try {
                Integer cent = Integer.parseInt(strCent.toString());
                return cent;
            }
            catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    private static final String seed = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] charSeed = seed.toCharArray();
    public static int uniqueIdSize = 6;
    public static String randomIdentity() {
        StringBuilder uniqueId = new StringBuilder();
        int temp = -1;

        //采用一个简单的算法以保证生成随机数的不同
        Random rand = new Random(System.currentTimeMillis()+System.nanoTime());
        for (int i = 1; i < uniqueIdSize + 1; i++) {
            int t = rand.nextInt(charSeed.length);
            uniqueId.append(charSeed[t]);
        }
        return uniqueId.toString();
    }

    public static List<String> parseRandomIdentity(String randomIdentity) {
        List<String> identities = new ArrayList<>();
        if (randomIdentity instanceof String) {
            char[] chars = randomIdentity.toCharArray();
            StringBuilder identity = new StringBuilder();
            for (int i=0, count=chars.length; i<count; i++) {
                char tmpChar = chars[i];
                int charIndex = seed.indexOf(tmpChar);
                if (charIndex>=0) {
                    identity.append(tmpChar);
                }
                if (i+1==count || charIndex<0){
                    String validIdentity = identity.toString();
                    identity.setLength(0);
                    if (validIdentity.length()==uniqueIdSize) {
                        identities.add(validIdentity);
                    }
                }
            }
        }
        return identities;
    }

    public static String getUniqueString() {
        StringBuilder ret = new StringBuilder();
        ret.append(System.currentTimeMillis());
        ret.append((System.nanoTime()+"").substring(3,10));
        return ret.toString();
    }
}
