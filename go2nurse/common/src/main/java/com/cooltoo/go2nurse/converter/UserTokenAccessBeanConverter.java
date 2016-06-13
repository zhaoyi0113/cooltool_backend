package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserTokenAccessBean;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/13.
 */
@Component
public class UserTokenAccessBeanConverter implements Converter<UserTokenAccessEntity, UserTokenAccessBean> {
    @Override
    public UserTokenAccessBean convert(UserTokenAccessEntity source) {
        UserTokenAccessBean bean = new UserTokenAccessBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setUserType(source.getUserType());
        bean.setToken(source.getToken());
        return bean;
    }
}
