package com.cooltoo.go2nurse.chart.ui.layout;

import com.cooltoo.go2nurse.chart.util.PageSize;

/**
 * 页脚
 * Created by zhaolisong on 16/01/2017.
 */
public class Footer extends RectRegion implements Cloneable {

    public Footer(float mmHeight) {
        super(null, null, null, mmHeight);
    }


    /**
     * @param pageSize 一页大小 {width, height} */
    public void fitPage(PageSize pageSize) {
        if (null!=pageSize) {
            mmTop(pageSize.mmHeight() - mmHeight());
            mmWidth(pageSize.mmWidth());
        }
    }

    public Footer clone() {
        Footer footer = new Footer(mmHeight());
        return footer;
    }
}
