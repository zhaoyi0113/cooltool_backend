package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.util.DpiUtil;

import java.awt.*;

/**
 * 矩形区域
 * Created by zhaolisong on 16/01/2017.
 */
public class RectRegion {
    public static final int NO_BORDER = 0;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int BOX = 15;

    int   border  = NO_BORDER;
    float mmTop   = 0;
    float mmLeft  = 0;
    float mmWidth = 0;
    boolean mmHeightModifiable = true;
    float mmHeight= 0;
    float mmPaddingTop   = 2;
    float mmPaddingLeft  = 2;
    float mmPaddingRight = 2;
    float mmPaddingBottom= 2;
    float mmBorderWidth  = 0.2f;
    Color leftBorderColor   = Color.black;
    Color topBorderColor    = Color.black;
    Color rightBorderColor  = Color.black;
    Color bottomBorderColor = Color.black;
    DpiUtil dpiUtil = DpiUtil.newInstance();

    boolean isDirty = false;
    float[] validRegion = null;

    public RectRegion(Float mmTop, Float mmLeft,
                      Float mmWidth, Float mmHeight) {
        this(mmTop, mmLeft, mmWidth, mmHeight, null, null, null, null);
    }

    public RectRegion(Float mmTop, Float mmLeft,
                      Float mmWidth, Float mmHeight,
                      Float mmPaddingTop, Float mmPaddingLeft,
                      Float mmPaddingRight, Float mmPaddingBottom) {
        if (null!=mmTop) { this.mmTop = mmTop; }
        if (null!=mmLeft) { this.mmLeft = mmLeft; }
        if (null!=mmWidth) { this.mmWidth = mmWidth; }
        if (null!=mmHeight) { this.mmHeight = mmHeight; }
        if (null!=mmPaddingTop) { this.mmPaddingTop = mmPaddingTop; }
        if (null!=mmPaddingLeft) { this.mmPaddingLeft = mmPaddingLeft; }
        if (null!=mmPaddingRight) { this.mmPaddingRight = mmPaddingRight; }
        if (null!=mmPaddingBottom) { this.mmPaddingBottom = mmPaddingBottom; }
    }

    public DpiUtil dpiUtil() {
        return dpiUtil;
    }
    public float mmTop() {
        return mmTop;
    }
    public float mmLeft() {
        return mmLeft;
    }
    public float mmWidth() {
        return mmWidth;
    }
    public float mmHeight() {
        return mmHeight;
    }
    public float mmPaddingTop() {
        return mmPaddingTop;
    }
    public float mmPaddingLeft() {
        return mmPaddingLeft;
    }
    public float mmPaddingRight() {
        return mmPaddingRight;
    }
    public float mmPaddingBottom() {
        return mmPaddingBottom;
    }
    public float mmBorderWidth() {
        return mmBorderWidth;
    }
    public boolean isDirty() {
        return isDirty;
    }
    public boolean mmHeightModifiable() {
        return mmHeightModifiable;
    }
    /** @return 有效内容区域 {mmLeft, mmTop, mmWidth, mmHeight} */
    public float[] validRegion() {
        if (isDirty || null==validRegion) {
            float left = mmLeft() + mmPaddingLeft();
            float top = mmTop() + mmPaddingTop();
            float width = mmWidth() - mmPaddingLeft() - mmPaddingRight();
            float height = mmHeight() - mmPaddingTop() - mmPaddingBottom();
            validRegion = new float[]{left, top, width, height};
            isDirty = false;
        }

        return validRegion;
    }

    public void mmTop(float mmTop) {
        this.mmTop = mmTop;
        this.isDirty = true;
    }
    public void mmLeft(float mmLeft) {
        this.mmLeft = mmLeft;
        this.isDirty = true;
    }
    public void mmWidth(float mmWidth) {
        this.mmWidth = mmWidth;
        this.isDirty = true;
    }
    public void mmHeight(float mmHeight) {
        if (mmHeightModifiable()) {
            this.mmHeight = mmHeight;
            this.isDirty = true;
        }
    }
    public void mmPaddingTop(float mmPaddingTop) {
        this.mmPaddingTop = mmPaddingTop;
        this.isDirty = true;
    }
    public void mmPaddingLeft(float mmPaddingLeft) {
        this.mmPaddingLeft = mmPaddingLeft;
        this.isDirty = true;
    }
    public void mmPaddingRight(float mmPaddingRight) {
        this.mmPaddingRight = mmPaddingRight;
        this.isDirty = true;
    }
    public void mmPaddingBottom(float mmPaddingBottom) {
        this.mmPaddingBottom = mmPaddingBottom;
        this.isDirty = true;
    }
    public void mmBorderWidth(float mmBorderWidth) {
        this.mmBorderWidth = mmBorderWidth;
        this.isDirty = true;
    }
    public void isDirty(boolean isDirty) {
        this.isDirty = isDirty;
        this.isDirty = true;
    }
    public void mmHeightModifiable(boolean mmHeightModifiable) {
        this.mmHeightModifiable = mmHeightModifiable;
    }

    public int border() {
        return this.border;
    }
    public void border(int border) {
        this.border = border;
    }
    public void addBorder(int border) {
        this.border(border()|border);
    }

    public void borderColor(Color color) {
        leftBorderColor   = color;
        topBorderColor    = color;
        rightBorderColor  = color;
        bottomBorderColor = color;
    }
    public void borderColorLeft(Color leftBorderColor) {
        this.leftBorderColor = leftBorderColor;
    }
    public void borderColorTop(Color topBorderColor) {
        this.topBorderColor = topBorderColor;
    }
    public void borderColorRight(Color rightBorderColor) {
        this.rightBorderColor = rightBorderColor;
    }
    public void borderColorBottom(Color bottomBorderColor) {
        this.bottomBorderColor = bottomBorderColor;
    }


    public Graphics save(int dpi, Graphics g) {
        if (dpiUtil.zero(mmWidth) || dpiUtil.zero(mmHeight)) {
            return g;
        }
        if (border>NO_BORDER) {
            int pixelTop         = dpiUtil.mmToPixel(dpi, mmTop);
            int pixelLeft        = dpiUtil.mmToPixel(dpi, mmLeft);
            int pixelWidth       = dpiUtil.mmToPixel(dpi, mmWidth);
            int pixelHeight      = dpiUtil.mmToPixel(dpi, mmHeight);
            int pixelBorderWidth = dpiUtil.mmToPixel(dpi, mmBorderWidth);
            pixelBorderWidth = ((pixelBorderWidth % 2) >0 && 1!=pixelBorderWidth) ? (pixelBorderWidth + 1) : pixelBorderWidth;
            int halfOfPixelBorderWidth = pixelBorderWidth / 2;


            Graphics2D g2d = (Graphics2D) g;
            Color oldColor = g2d.getColor();
            Stroke oldStroke = g2d.getStroke();

            g2d.setColor(Color.black);
            g2d.setStroke(new BasicStroke(pixelBorderWidth));


            if ((border & LEFT) > 0) {
                if (null!=leftBorderColor && Color.black!=leftBorderColor) {
                    g2d.setColor(leftBorderColor);
                }
                g2d.drawLine(
                        pixelLeft- halfOfPixelBorderWidth,
                        pixelTop,
                        pixelLeft- halfOfPixelBorderWidth,
                        pixelTop - halfOfPixelBorderWidth + pixelHeight);
            }
            if ((border & TOP) > 0) {
                if (null!=topBorderColor && Color.black!=topBorderColor) {
                    g2d.setColor(topBorderColor);
                }
                g2d.drawLine(
                        pixelLeft- halfOfPixelBorderWidth,
                        pixelTop - halfOfPixelBorderWidth,
                        pixelLeft- halfOfPixelBorderWidth + pixelWidth,
                        pixelTop - halfOfPixelBorderWidth);
            }
            if ((border & RIGHT) > 0) {
                if (null!=rightBorderColor && Color.black!=rightBorderColor) {
                    g2d.setColor(rightBorderColor);
                }
                g2d.drawLine(
                        pixelLeft- halfOfPixelBorderWidth + pixelWidth,
                        pixelTop,
                        pixelLeft- halfOfPixelBorderWidth + pixelWidth,
                        pixelTop - halfOfPixelBorderWidth + pixelHeight);
            }
            if ((border & BOTTOM) > 0) {
                if (null!=bottomBorderColor && Color.black!=bottomBorderColor) {
                    g2d.setColor(bottomBorderColor);
                }
                g2d.drawLine(
                        pixelLeft- halfOfPixelBorderWidth,
                        pixelTop - halfOfPixelBorderWidth + pixelHeight,
                        pixelLeft- halfOfPixelBorderWidth + pixelWidth,
                        pixelTop - halfOfPixelBorderWidth + pixelHeight);
            }

            g2d.setColor(oldColor);
            g2d.setStroke(oldStroke);
        }
        return g;
    }

    public String toString() {
        StringBuilder me = new StringBuilder();
        me.append(getClass().getName()).append("@").append(hashCode()).append("[");
        me.append("top=").append(mmTop);
        me.append(", left=").append(mmLeft);
        me.append(", width=").append(mmWidth);
        me.append(", height=").append(mmHeight);
        return me.toString();
    }
}
