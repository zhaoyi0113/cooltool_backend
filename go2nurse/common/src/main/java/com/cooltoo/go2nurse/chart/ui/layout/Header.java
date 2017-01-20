package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.util.PageSize;

/**
 * 页眉
 * Created by zhaolisong on 15/01/2017.
 */
public class Header extends RectRegion {

    public Header(float mmHeight) {
        super(null, null, null, mmHeight);
    }


    /**
     * @param pageSize 一页大小 {width, height} */
    public void fitPage(PageSize pageSize) {
        if (null!=pageSize) {
            mmWidth(pageSize.mmWidth());
        }
    }
}
