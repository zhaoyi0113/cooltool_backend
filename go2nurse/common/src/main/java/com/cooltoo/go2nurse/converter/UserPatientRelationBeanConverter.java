package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserPatientRelationBean;
import com.cooltoo.go2nurse.entities.UserPatientRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/14.
 */
@Component
public class UserPatientRelationBeanConverter implements Converter<UserPatientRelationEntity, UserPatientRelationBean> {
    @Override
    public UserPatientRelationBean convert(UserPatientRelationEntity source) {
        UserPatientRelationBean bean = new UserPatientRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        return bean;
    }
}
