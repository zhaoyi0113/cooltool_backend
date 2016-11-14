package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.HospitalAdminRolesBean;
import com.cooltoo.nurse360.entities.HospitalAdminRolesEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Component
public class HospitalAdminRolesBeanConverter implements Converter<HospitalAdminRolesEntity, HospitalAdminRolesBean> {
    @Override
    public HospitalAdminRolesBean convert(HospitalAdminRolesEntity source) {
        HospitalAdminRolesBean bean = new HospitalAdminRolesBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setAdminId(source.getAdminId());
        bean.setRole(source.getRole());
        return bean;
    }
}
