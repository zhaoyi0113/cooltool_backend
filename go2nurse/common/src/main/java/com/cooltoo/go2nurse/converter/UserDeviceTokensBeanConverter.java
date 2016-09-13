package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserDeviceTokensBean;
import com.cooltoo.go2nurse.entities.UserDeviceTokensEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/9/13.
 */
@Component
public class UserDeviceTokensBeanConverter implements Converter<UserDeviceTokensEntity, UserDeviceTokensBean> {
    @Override
    public UserDeviceTokensBean convert(UserDeviceTokensEntity source) {
        UserDeviceTokensBean bean = new UserDeviceTokensBean();
        bean.setId(source.getId());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setDeviceType(source.getDeviceType());
        bean.setDeviceToken(source.getDeviceToken());
        return bean;
    }
}
