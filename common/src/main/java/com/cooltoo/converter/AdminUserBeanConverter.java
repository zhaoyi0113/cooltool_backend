package com.cooltoo.converter;

import com.cooltoo.beans.AdminUserBean;
import com.cooltoo.entities.AdminUserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Component
public class AdminUserBeanConverter implements Converter<AdminUserEntity, AdminUserBean> {

    @Override
    public AdminUserBean convert(AdminUserEntity source) {
        AdminUserBean bean = new AdminUserBean();
        bean.setId(source.getId());
        bean.setUserType(source.getUserType());
        bean.setUserName(source.getUserName());
        bean.setPassword(source.getPassword());
        bean.setPhoneNumber(source.getPhoneNumber());
        bean.setEmail(source.getEmail());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;
    }
}
