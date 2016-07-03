package com.cooltoo.go2nurse.converter;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserReExaminationBean;
import com.cooltoo.go2nurse.entities.UserReExaminationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by hp on 2016/7/3.
 */
@Component
public class UserReExaminationBeanConverter implements Converter<UserReExaminationEntity, UserReExaminationBean> {
    @Override
    public UserReExaminationBean convert(UserReExaminationEntity source) {
        UserReExaminationBean bean = new UserReExaminationBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setHospitalizedGroupId(source.getHospitalizedGroupId());
        bean.setReExaminationDate(source.getReExaminationDate());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
