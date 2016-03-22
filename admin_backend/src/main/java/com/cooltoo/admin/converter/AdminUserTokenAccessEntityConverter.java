package com.cooltoo.admin.converter;

import com.cooltoo.admin.beans.AdminUserTokenAccessBean;
import com.cooltoo.admin.entities.AdminUserTokenAccessEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Component
public class AdminUserTokenAccessEntityConverter implements Converter<AdminUserTokenAccessBean, AdminUserTokenAccessEntity> {

    @Override
    public AdminUserTokenAccessEntity convert(AdminUserTokenAccessBean source) {
        AdminUserTokenAccessEntity entity = new AdminUserTokenAccessEntity();
        entity.setId(source.getId());
        entity.setUserType(source.getUserType());
        entity.setUserId(source.getUserId());
        entity.setTimeCreated(source.getTimeCreated());
        entity.setToken(source.getToken());
        entity.setStatus(source.getStatus());
        return entity;
    }
}
