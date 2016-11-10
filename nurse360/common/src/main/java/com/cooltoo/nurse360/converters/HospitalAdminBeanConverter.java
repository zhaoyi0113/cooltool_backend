package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Component
public class HospitalAdminBeanConverter implements Converter<HospitalAdminEntity, HospitalAdminBean> {
    @Override
    public HospitalAdminBean convert(HospitalAdminEntity source) {
        HospitalAdminBean bean = new HospitalAdminBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setAdminType(source.getAdminType());
        bean.setName(source.getName());
        bean.setPassword(source.getPassword());
        bean.setTelephone(source.getTelephone());
        bean.setEmail(source.getEmail());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        return bean;
    }
}
