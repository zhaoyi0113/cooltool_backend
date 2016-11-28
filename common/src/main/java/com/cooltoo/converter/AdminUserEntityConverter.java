package com.cooltoo.converter;

import com.cooltoo.beans.AdminUserBean;
import com.cooltoo.entities.AdminUserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Component
public class AdminUserEntityConverter implements Converter<AdminUserBean, AdminUserEntity> {

    @Override
    public AdminUserEntity convert(AdminUserBean source) {
        AdminUserEntity entity = new AdminUserEntity();
        entity.setId(source.getId());
        entity.setUserType(source.getUserType());
        entity.setUserName(source.getUserName());
        entity.setPassword(source.getPassword());
        entity.setPhoneNumber(source.getPhoneNumber());
        entity.setEmail(source.getEmail());
        entity.setTimeCreated(source.getTimeCreated());
        return entity;
    }
}
