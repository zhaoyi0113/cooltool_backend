package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.util.PageSize;

/**
 * Created by zhaolisong on 16/01/2017.
 */
public class MarginRight extends RectRegion {

    public MarginRight(float mmWidth) {
        super(null, null, mmWidth, null);
    }


    /**
     * @param pageSize 一页大小 {width, height} */
    public void fitPage(PageSize pageSize) {
        if (null!=pageSize) {
            mmLeft(pageSize.mmWidth()-mmWidth());
            mmHeight(pageSize.mmHeight());
        }
    }
}
