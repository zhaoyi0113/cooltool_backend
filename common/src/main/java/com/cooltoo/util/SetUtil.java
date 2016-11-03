package com.cooltoo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/2.
 */
public final class SetUtil {

    public static SetUtil newInstance() {
        return new SetUtil();
    }

    private SetUtil() {}

    public List getSetByPage(List srcSet, int pageIndex, int sizePerPage, List resultSet) {
        if (null==resultSet) {
            resultSet = new ArrayList();
        }
        if (VerifyUtil.isListEmpty(srcSet)) {
            return resultSet;
        }

        int endIndex   = (pageIndex*sizePerPage + sizePerPage);
        int startIndex = (pageIndex*sizePerPage)>=0
                ? (pageIndex*sizePerPage)
                : 0;

        for (int i = startIndex; i < srcSet.size(); i++) {
            if (i < endIndex) {
                resultSet.add(srcSet.get(i));
                continue;
            }
            break;
        }

        return resultSet;
    }
}