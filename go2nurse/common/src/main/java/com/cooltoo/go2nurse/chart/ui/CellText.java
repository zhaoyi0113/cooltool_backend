package com.cooltoo.go2nurse.chart.ui;

import com.cooltoo.go2nurse.chart.util.Align;
import com.cooltoo.go2nurse.chart.util.DpiUtil;

import java.awt.*;

/**
 *
 * Created by zhaolisong on 16/01/2017.
 */
public class CellText {

    private int align = Align.ALIGN_LEFT | Align.ALIGN_TOP;
    private String content = null;
    private Font font = null;
    private boolean underline;
    private DpiUtil dpiUtil = DpiUtil.newInstance();
    private int maxCharSize = 0;

    public CellText(String content, Font font, int align, boolean underline) {
        this.content = content;
        this.font = font;
        this.align = align;
        this.underline = underline;
    }

    public String content() {
        return this.content;
    }
    public void content(String content) {
        this.content = content;
    }

    public Font font() {
        return font;
    }
    public void font(Font font) {
        this.font = font;
    }

    public int align() {
        return align;
    }
    public void align(int align) {
        this.align = align;
    }

    public boolean underline() {
        return underline;
    }
    public void underline(boolean underline) {
        this.underline = underline;
    }

    /** @return 文本的占用空间的大小（单位：pixel）{ leading, ascent, descent, height, width} */
    public int[] contentBound(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics(font);
//        String matchLength = contentMatchLength(content, maxCharSize);
        int[] textBound = new int[]{fm.getLeading(), fm.getAscent(), fm.getDescent(), fm.getHeight(), fm.stringWidth(content)};
        return textBound;
    }

    /**
     * @param dpi 每英寸点数
     * @param mmBound 有效内容区域 {mmLeft, mmTop, mmWidth, mmHeight}
     * @param g 绘图句柄
     */
    public Graphics save(int dpi, float[] mmBound, Graphics g) {
        int pixelLeft  = dpiUtil.mmToPixel(dpi, mmBound[0]);
        int pixelTop   = dpiUtil.mmToPixel(dpi, mmBound[1]);
        int pixelWidth = dpiUtil.mmToPixel(dpi, mmBound[2]);
        int pixelHeight= dpiUtil.mmToPixel(dpi, mmBound[3]);

        Font  oldFont = g.getFont();
        Color oldColor= g.getColor();

        g.setFont(font);
        g.setColor(Color.black);

        // 文本的占用空间的大小（单位：pixel）{ leading, ascent, descent, height, width}
        int[] pixelBound = contentBound(g);
        int   descent    = pixelBound[2];

        int x = pixelLeft;
        int y = pixelTop + pixelBound[0] + pixelBound[1];
        // horizontal align
        if ((align & Align.ALIGN_CENTER) > 0) {
            x  = pixelLeft + (pixelWidth - pixelBound[4])/2;
        }
        else if ((align & Align.ALIGN_RIGHT) > 0) {
            x  = pixelLeft + (pixelWidth - pixelBound[4]);
        }
        // vertical align
        if ((align & Align.ALIGN_MIDDLE) > 0) {
            y  = (pixelTop + pixelBound[0] + pixelBound[1]) + (pixelHeight - pixelBound[3])/2;
        }
        else if ((align & Align.ALIGN_BOTTOM) > 0) {
            y  = (pixelTop + pixelBound[0] + pixelBound[1]) + (pixelHeight - pixelBound[3]);
        }
        g.drawString(content, x, y);
        if (underline) {
            g.drawLine(pixelLeft, y+descent+2, pixelLeft+pixelWidth, y+descent+2);
            g.drawLine(pixelLeft, y+descent+3, pixelLeft+pixelWidth, y+descent+3);
        }

        g.setFont(oldFont);
        g.setColor(oldColor);
        return g;
    }
}
