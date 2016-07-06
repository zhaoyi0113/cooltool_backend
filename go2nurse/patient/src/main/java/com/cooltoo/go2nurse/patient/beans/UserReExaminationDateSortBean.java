package com.cooltoo.go2nurse.patient.beans;

import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/6.
 */
public class UserReExaminationDateSortBean {

    private long groupId;
    private List<UserReExaminationDateBean> reExaminationDate = new ArrayList<>();

    public long getGroupId() {
        return groupId;
    }

    public List<UserReExaminationDateBean> getReExaminationDate() {
        return reExaminationDate;
    }

    public void setReExaminationDate(List<UserReExaminationDateBean> reExaminationDate) {
        this.reExaminationDate = reExaminationDate;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

}
