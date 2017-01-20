package com.cooltoo.go2nurse.chart.ui.layout;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/01/2017.
 */
public class Row extends RectRegion {

    /** 行里的单元格 */
    private List<Cell> cells = new ArrayList<Cell>();

    public Row (float mmHeight) {
        this(mmHeight, 0);
    }

    public Row (float mmHeight, float padding) {
        super(null, null, null, mmHeight);
        super.mmPaddingTop(padding);
        super.mmPaddingLeft(padding);
        super.mmPaddingRight(padding);
        super.mmPaddingBottom(padding);
    }

    public void addCell(Cell cell) {
        float left  = 0;
        float top   = 0;
        float height= 0;
        if (this.cells.isEmpty()) {
            left  = mmLeft();
            top   = mmTop();
            height= mmHeight();
        }
        else {
            Cell lastCell = this.cells.get(this.cells.size()-1);
            left  = lastCell.mmLeft() + lastCell.mmWidth();
            top   = mmTop();
            height= mmHeight();
        }
        cell.mmLeft(left);
        cell.mmTop(top);
        cell.mmHeight(height);
        this.cells.add(cell);
        adjustCell();
    }

    public void adjustCell() {
        if (!this.cells.isEmpty()) {
            float[] validRegion = validRegion();
            float left  = validRegion[0];
            float top   = validRegion[1];
            float height= validRegion[3];
            for (int i=0; i<this.cells.size(); i++) {
                Cell cell = this.cells.get(i);
                cell.mmLeft(left);
                cell.mmTop(top);
                cell.mmHeight(height);

                left = left + cell.mmWidth();
            }
        }
    }

    public void addCellBorders(int border) {
        if (!this.cells.isEmpty()) {
            for (int i=0; i<this.cells.size(); i++) {
                Cell cell = this.cells.get(i);
                cell.border(cell.border()|border);
            }
        }
    }

    public Graphics save(int dpi, Graphics g) {
        // 保存单元格
        for (int i=0, count=this.cells.size(); i < count; i ++){
            Cell cell = this.cells.get(i);
            cell.save(dpi, g);
        }

        super.save(dpi, g);

        return g;
    }


}
