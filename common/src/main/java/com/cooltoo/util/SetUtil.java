package com.cooltoo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public List getMapValueSet(Map map) {
        List val = new ArrayList();
        if (null==map) {
            return val;
        }
        Collection mapVal = map.values();
        if (null!=mapVal && !mapVal.isEmpty()) {
            for (Object obj : mapVal) {
                val.add(obj);
            }
        }
        return val;
    }

    public List mergeListValue(List list1, List list2Result) {
        if (VerifyUtil.isListEmpty(list1) && VerifyUtil.isListEmpty(list2Result)) {
            return new ArrayList<>();
        }
        if (VerifyUtil.isListEmpty(list1)) {
            return list2Result;
        }
        if (VerifyUtil.isListEmpty(list2Result)) {
            if (null==list2Result) {
                list2Result = new ArrayList();
            }
        }

        for (Object tmp : list1) {
            if (list2Result.contains(tmp)) {
                continue;
            }
            list2Result.add(tmp);
        }
        return list2Result;
    }
}
