package com.cooltoo.go2nurse.chart.util;

/**
 * Created by zhaolisong on 15/01/2017.
 */
public class PageSize {

    public static final PageSize A4 = new PageSize(210.0f, 297.0f);
    public static final PageSize A3 = new PageSize(297.0f, 420.0f);
    public static final PageSize A2 = new PageSize(420.0f, 594.0f);
    public static final PageSize A1 = new PageSize(594.0f, 841.0f);
    public static final PageSize A0 = new PageSize(841.0f, 1189.0f);

    /** width in millimeters */
    private float mmWidth;
    /** height in millimeters */
    private float mmHeight;

    private PageSize (float mmWidth, float mmHeight) {
        this.mmWidth = mmWidth;
        this.mmHeight = mmHeight;
    }

    public float mmWidth() {
        return mmWidth;
    }

    public float mmHeight() {
        return mmHeight;
    }
}
