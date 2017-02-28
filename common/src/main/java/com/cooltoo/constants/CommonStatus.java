package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 12/23/15.
 */
public enum CommonStatus {
    DISABLED, ENABLED, DELETED;

    public static CommonStatus parseString(String status) {
        CommonStatus retVal = null;
        if (ENABLED.name().equalsIgnoreCase(status)) {
            retVal= ENABLED;
        }
        else if (DISABLED.name().equalsIgnoreCase(status)) {
            retVal = DISABLED;
        }
        else if (DELETED.name().equalsIgnoreCase(status)) {
            retVal = DELETED;
        }
        return retVal;
    }

    public static CommonStatus parseInt(int status) {
        CommonStatus retVal = null;
        if (ENABLED.ordinal() == status) {
            retVal = ENABLED;
        }
        else if (DISABLED.ordinal() == status) {
            retVal = DISABLED;
        }
        else if (DELETED.ordinal() == status) {
            retVal = DELETED;
        }
        return retVal;
    }

    public static List<CommonStatus> getAll() {
        List<CommonStatus> all = new ArrayList<>();
        all.add(CommonStatus.DISABLED);
        all.add(CommonStatus.ENABLED);
        all.add(CommonStatus.DELETED);
        return all;
    }

    public static List<CommonStatus> parseAll(String statuses) {
        if (null==statuses || statuses.trim().isEmpty()) {
            return null;
        }
        statuses = statuses.toUpperCase().trim();
        List<CommonStatus> all = new ArrayList<>();
        if (statuses.contains(ENABLED.name())) {
            all.add(ENABLED);
        }
        if (statuses.contains(DISABLED.name())) {
            all.add(DISABLED);
        }
        if (statuses.contains(DELETED.name())) {
            all.add(DELETED);
        }
        if (all.isEmpty()) {
            all = null;
        }
        return all;

    }
}
