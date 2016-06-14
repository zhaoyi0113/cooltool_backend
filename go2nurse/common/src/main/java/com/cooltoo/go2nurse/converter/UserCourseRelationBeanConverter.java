package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserCourseRelationBean;
import com.cooltoo.go2nurse.entities.UserCourseRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/14.
 */
@Component
public class UserCourseRelationBeanConverter implements Converter<UserCourseRelationEntity, UserCourseRelationBean> {
    @Override
    public UserCourseRelationBean convert(UserCourseRelationEntity source) {
        UserCourseRelationBean bean = new UserCourseRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setCourseId(source.getCourseId());
        bean.setReadingStatus(source.getReadingStatus());
        return bean;
    }
}
