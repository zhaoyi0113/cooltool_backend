package com.cooltoo.go2nurse.beans;

import com.cooltoo.util.VerifyUtil;

import java.io.Serializable;

/**
 * Created by hp on 2016/6/29.
 */
public class QuestionnaireConclusionBean {

    private String item="";
    private String interval="";
    private int[] iInterval;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public boolean isThisConclusion(int score) {
        int[] interval = getIntInterval();
        if (interval.length==2) {
            if (Integer.MIN_VALUE==interval[0]) {
                return score<interval[1];
            }
            else if (Integer.MAX_VALUE==interval[1]) {
                return score>interval[0];
            }
            return interval[0]<=score && score<=interval[1];
        }
        return false;
    }

    private int[] getIntInterval() {
        if (null!=iInterval) {
            return iInterval;
        }
        if (VerifyUtil.isStringEmpty(interval)) {
            iInterval = new int[0];
            return iInterval;
        }
        try {
            String strMinMax = interval;
            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;
            strMinMax = strMinMax.trim().replace(" ", "");
            if (strMinMax.indexOf('<') >= 0) {
                strMinMax = strMinMax.replace("<", "");
                max = Integer.valueOf(strMinMax);
            } else if (strMinMax.indexOf('>') >= 0) {
                strMinMax = strMinMax.replace(">", "");
                min = Integer.valueOf(strMinMax);
            } else if (strMinMax.indexOf('-') >= 0) {
                String[] minMax = strMinMax.split("-");
                min = Integer.valueOf(minMax[0]);
                max = Integer.valueOf(minMax[1]);
            }
            iInterval = new int[]{min, max};
            return iInterval;
        }
        catch (Exception ex) {
            iInterval = new int[0];
            return iInterval;
        }
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("item=").append(item);
        msg.append(", interval=").append(interval);
        msg.append("]");
        return msg.toString();
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"item\":\"").append(item).append("\"");
        json.append(", \"interval\":\"").append(interval).append("\"");
        json.append("}");
        return json.toString();
    }
}
