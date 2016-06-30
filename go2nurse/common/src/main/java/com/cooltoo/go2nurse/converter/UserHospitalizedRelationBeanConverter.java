package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.entities.UserHospitalizedRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/14.
 */
@Component
public class UserHospitalizedRelationBeanConverter implements Converter<UserHospitalizedRelationEntity, UserHospitalizedRelationBean> {
    @Override
    public UserHospitalizedRelationBean convert(UserHospitalizedRelationEntity source) {
        UserHospitalizedRelationBean bean = new UserHospitalizedRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setGroupId(source.getGroupId());
        bean.setHasLeave(source.getHasLeave());
        return bean;
    }
}
