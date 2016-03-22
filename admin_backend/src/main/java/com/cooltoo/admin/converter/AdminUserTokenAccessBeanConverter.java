package com.cooltoo.admin.converter;

import com.cooltoo.admin.beans.AdminUserTokenAccessBean;
import com.cooltoo.admin.entities.AdminUserTokenAccessEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Component
public class AdminUserTokenAccessBeanConverter implements Converter<AdminUserTokenAccessEntity, AdminUserTokenAccessBean> {
    @Override
    public AdminUserTokenAccessBean convert(AdminUserTokenAccessEntity source) {
        AdminUserTokenAccessBean bean = new AdminUserTokenAccessBean();
        bean.setId(source.getId());
        bean.setUserType(source.getUserType());
        bean.setUserId(source.getUserId());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setToken(source.getToken());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
