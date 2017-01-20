package com.cooltoo.go2nurse.chart.util;

/**
 * Created by zhaolisong on 15/01/2017.
 */
public class DpiUtil {

    public static final int DPI_300 = 300;

    public static final float MmPerInch = 25.4f;
    public static final float InchPerMM = 1/MmPerInch;

    public static DpiUtil newInstance() {
        return new DpiUtil();
    }

    private DpiUtil() {}

    public int mmToPixel(int dpi, float mm) {
        if (dpi<=0 || mm<=0) {
            return 0;
        }

        float inch = mm * InchPerMM;
        float pixel = inch * dpi;

        return (int)(pixel*10 + 5)/10;
    }

    public boolean zero(float mm) {
        if (mm - 0.0 < 0.0000001f) {
            return true;
        }
        return false;
    }
}
