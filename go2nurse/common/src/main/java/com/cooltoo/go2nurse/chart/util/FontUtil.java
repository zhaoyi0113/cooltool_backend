package com.cooltoo.go2nurse.chart.util;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 17/01/2017.
 */
public class FontUtil {

    public static final float MmPerPound = 0.352857f;

    public static final Map<String, Font> baseFontMap = new HashMap<String, Font>();

    /**
     * @param fontFilePath 第一个参数是外部字体名
     * @return 加载的字体
     */
    public static Font loadBaseFont(String fontFilePath) {//
        FileInputStream fisFont = null;
        try {
            File fFont = new File(fontFilePath);
            if (baseFontMap.containsKey(fFont.getName())) {
                return baseFontMap.get(fFont.getName());
            }
            fisFont = new FileInputStream(fFont);
            Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, fisFont);
            baseFontMap.put(fFont.getName(), dynamicFont);
            return dynamicFont;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null!=fisFont) { try { fisFont.close(); } catch (Exception ex) {} }
        }
        return null;
    }

    public static FontUtil newInstance() {
        return new FontUtil();
    }

    private FontUtil() {}

    public float mmToPound(float mm) {
        return mm/MmPerPound;
    }
    public float poundToMm(float pound) {
        return pound * MmPerPound;
    }

    public Font getFont(String fontName, int style, float mmFontSize, int dpi) {
        mmFontSize = mmFontSize * dpi/72;

        Font baseFont = baseFontMap.get(fontName);
        if (null==baseFont) {
            throw new NullPointerException(fontName+" font can not found!");
        }
        return baseFont.deriveFont(style, mmToPound(mmFontSize));
    }
}
