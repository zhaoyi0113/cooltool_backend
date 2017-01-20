package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.util.PageSize;

/**
 * 左边距
 * Created by zhaolisong on 16/01/2017.
 */
public class MarginLeft  extends RectRegion {

    public MarginLeft(float mmWidth) {
        super(null, null, mmWidth, null);
    }

    /**
     * @param pageSize 一页大小 {width, height} */
    public void fitPage(PageSize pageSize) {
        if (null!=pageSize) {
            mmHeight(pageSize.mmHeight());
        }
    }
}
