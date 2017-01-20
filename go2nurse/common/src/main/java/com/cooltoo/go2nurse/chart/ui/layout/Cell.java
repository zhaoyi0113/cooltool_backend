package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.ui.CellImage;
import com.cooltoo.go2nurse.chart.ui.CellText;
import com.cooltoo.go2nurse.chart.util.TextUtil;

import java.awt.*;

/**
 * Created by zhaolisong on 16/01/2017.
 */
public class Cell extends RectRegion {
    /** 表格内容 */
    private CellText  text;
    private int maxCharInCell = 0;
    private TextUtil textUtil = TextUtil.newInstance();
    /** 表格图片 */
    private CellImage image;

    public Cell (float mmWidth) {
        this(mmWidth, 0);
    }
    public Cell (float mmWidth, float padding) {
        super(null, null, mmWidth, null);
        super.mmPaddingTop(padding);
        super.mmPaddingLeft(padding);
        super.mmPaddingRight(padding);
        super.mmPaddingBottom(padding);
    }

    public CellImage image() {
        return image;
    }
    public void image(CellImage image) {
        this.image = image;
    }

    public CellText text() {
        return text;
    }
    public void text(CellText text) {
        String lines = textUtil.getContentInSpecialSize(text.content(), maxCharInCell);
        text.content(lines);
        this.text = text;
    }

    public int maxCharInCell() {
        return maxCharInCell;
    }
    public void maxCharInCell(int maxCharInCell) {
        this.maxCharInCell = maxCharInCell;
    }

    public Graphics save(int dpi, Graphics g) {
        super.save(dpi, g);

        if (null!=text()) {
            float[] validRegion = validRegion();
            text().save(dpi, validRegion, g);
        }

        if (null!=image()) {
            float[] validRegion = validRegion();
            image().save(dpi, validRegion, g);
        }

        return g;
    }
}
