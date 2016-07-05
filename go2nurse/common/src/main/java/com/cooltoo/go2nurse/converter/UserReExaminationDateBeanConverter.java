package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;
import com.cooltoo.go2nurse.entities.UserReExaminationDateEntity;
import com.cooltoo.util.NumberUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/3.
 */
@Component
public class UserReExaminationDateBeanConverter implements Converter<UserReExaminationDateEntity, UserReExaminationDateBean> {
    @Override
    public UserReExaminationDateBean convert(UserReExaminationDateEntity source) {
        UserReExaminationDateBean bean = new UserReExaminationDateBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setIsStartDate(source.getIsStartDate());
        bean.setHasOperation(source.getHasOperation());
        bean.setGroupId(source.getGroupId());
        bean.setHospitalizedGroupId(source.getHospitalizedGroupId());
        bean.setReExaminationDate(source.getReExaminationDate());
        bean.setIgnore(source.getIgnore());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        String strReExamDate = NumberUtil.timeToString(bean.getReExaminationDate(), NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        bean.setStrReExaminationDate(strReExamDate);
        return bean;
    }
}
