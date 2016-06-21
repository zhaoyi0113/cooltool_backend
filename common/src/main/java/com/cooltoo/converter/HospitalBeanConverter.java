package com.cooltoo.converter;

import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.beans.HospitalBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class HospitalBeanConverter implements Converter<HospitalEntity, HospitalBean> {
    @Override
    public HospitalBean convert(HospitalEntity source) {
        HospitalBean bean = new HospitalBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setProvince(source.getProvince());
        bean.setCity(source.getCity());
        bean.setDistrict(source.getDistrict());
        bean.setAddress(source.getAddress());
        bean.setEnable(source.getEnable());
        bean.setAliasName(source.getAliasName());
        bean.setSupportGo2nurse(source.getSupportGo2nurse());
        bean.setUniqueId(source.getUniqueId());
        return bean;
    }
}
