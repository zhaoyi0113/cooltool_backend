package com.cooltoo.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
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

    public static String createNoncestr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            int index = rd.nextInt(chars.length()-1);
            char ch = chars.charAt(index);
            res += ch;
        }
        return res;
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


    private final static String[] hexDigits = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5编码
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String md5Encode(String origin, String charset, String algorithm) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance(algorithm);
            if (null==charset || "".equals(charset)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            }
            else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charset)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
