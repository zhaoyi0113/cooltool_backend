package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.util.DpiUtil;
import com.cooltoo.go2nurse.chart.util.ImageUtil;
import com.cooltoo.go2nurse.chart.util.PageSize;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 页包含（页眉，页脚，左边距，右边距，标题行，记录行）
 * Created by zhaolisong on 16/01/2017.
 */
public class Page {
    /** 页面大小 */
    PageSize pageSize = PageSize.A4;

    /** 页眉 */
    Header header;
    /** 页脚 */
    Footer footer;
    /** 左边距 */
    MarginLeft marginLeft;
    /** 右边距 */
    MarginRight marginRight;


    /** 行 */
    List<Row> rows = new ArrayList<>();
    /** 固定的行数 */
    int fixedRowsSize = 0;


    /** 所有内容都画到该 Image 上 */
    BufferedImage pageImg = null;
    /** 有效内容区域 */
    float[] validRegion = null;
    /** 是否需要重新计算 */
    boolean isDirty = false;
    /** dpi */
    int dpi = 0;
    /** dpi utility */
    DpiUtil dpiUtil = DpiUtil.newInstance();
    /** Image utility */
    ImageUtil imageUtil = ImageUtil.newInstance();

    /**
     * @param dpi Dots Per Inch
     * @param pageSize size of page(etc A4) {@link PageSize}
     */
    public Page(int dpi, PageSize pageSize) {
        this.dpi = dpi;
        this.pageSize = pageSize;
    }

    public Header header() {
        return header;
    }
    public void header(Header header) {
        this.header = header;
        this.header.fitPage(pageSize);
    }

    public Footer footer() {
        return footer;
    }
    public void footer(Footer footer) {
        this.footer = footer;
        this.footer.fitPage(pageSize);
    }

    public MarginLeft marginLeft() {
        return marginLeft;
    }
    public void marginLeft(MarginLeft marginLeft) {
        this.marginLeft = marginLeft;
        this.marginLeft.fitPage(pageSize);
    }

    public MarginRight marginRight() {
        return marginRight;
    }
    public void marginRight(MarginRight marginRight) {
        this.marginRight = marginRight;
        this.marginRight.fitPage(pageSize);
    }

    /** @return 有效内容区域 {left, top, width, height} */
    public float[] validRegion() {
        if (null==pageSize) {
            throw new NullPointerException("page size is null");
        }
        if (isDirty || null==validRegion) {
            float top = null == header ? 0 : header.mmHeight();
            float bottom = null == footer ? 0 : footer.mmHeight();
            float left = null == marginLeft ? 0 : marginLeft.mmWidth();
            float right = null == marginRight ? 0 : marginRight.mmWidth();
            float width = pageSize.mmWidth() - left - right;
            float height = pageSize.mmHeight() - top - bottom;
            validRegion = new float[]{left, top, width, height};
        }

        return validRegion;
    }
    public float validWidth() {
        float[] validRegion = validRegion();
        return validRegion[2];
    }

    public void addRow(Row row) {
        if (null==row) { return; }
        float left = 0;
        float top  = 0;
        float width= 0;
        if (!this.rows.isEmpty()) {
            Row lastRow = this.rows.get(this.rows.size()-1);
            left = lastRow.mmLeft(); // 设置 X
            top  = lastRow.mmTop() + lastRow.mmHeight();// 设置 Y
            width= lastRow.mmWidth();// 设置 宽度
        }
        else {
            float[] validRegion = validRegion();
            left = validRegion[0]; // 设置 X
            top  = validRegion[1]; // 设置 Y
            width= validRegion[2]; // 设置 宽度
        }
        row.mmLeft(left); // 设置 X
        row.mmTop(top);  // 设置 Y
        row.mmWidth(width);// 设置 宽度
        row.adjustCell();
        this.rows.add(row);
    }

    public Row getLastRow() {
        if (!this.rows.isEmpty()) {
            return this.rows.get(this.rows.size()-1);
        }
        return null;
    }

    public int rowSize() {
        return null==rows ? 0 : rows.size();
    }

    public Row removeLastRow() {
        if (!this.rows.isEmpty()) {
            return this.rows.remove(this.rows.size()-1);
        }
        return null;
    }

    public void removeUnfixedRows() {
        if (fixedRowsSize>0) {
            if (null!=rows && !rows.isEmpty() && rows.size()>fixedRowsSize) {
                for (int i = rows.size()-1; i>=0 && fixedRowsSize!=rows.size(); i--) {
                    rows.remove(i);
                }
            }
        }
        else {
            rows.clear();
        }
    }

    public int fixedRowsSize() {
        return fixedRowsSize;
    }
    public void fixedRowsSize(int fixedRowsSize) {
        this.fixedRowsSize = fixedRowsSize;
    }

    public void save(File file) {
        // 创建图片
        pageImg = new BufferedImage(
                dpiUtil.mmToPixel(dpi, pageSize.mmWidth()),
                dpiUtil.mmToPixel(dpi, pageSize.mmHeight()),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) pageImg.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 清空图片内容
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, pageImg.getWidth(), pageImg.getHeight());

        // 设置页眉
        if (null!=header()) {
            header().save(dpi, g2d);
        }

        // 设置页脚
        if (null!=footer()) {
            footer().save(dpi, g2d);
        }

        // 设置左边距
        if (null!=marginLeft()) {
            marginLeft().save(dpi, g2d);
        }

        // 设置右边距
        if (null!=marginRight()) {
            marginRight().save(dpi, g2d);
        }

        // 添加表
        for (int i = 0, count = this.rows.size(); i < count; i ++){
            Row row = this.rows.get(i);
            row.save(dpi, g2d);
        }
        g2d.dispose();

        // 保存图片
        if (null!=file) {
            imageUtil.sharperImage(pageImg);
            imageUtil.saveImage(pageImg, file, dpi);
        }
    }
}
