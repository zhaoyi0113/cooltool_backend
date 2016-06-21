package com.cooltoo.converter;

import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.beans.HospitalBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class HospitalEntityConverter implements Converter<HospitalBean, HospitalEntity> {
    @Override
    public HospitalEntity convert(HospitalBean source) {
        HospitalEntity entity = new HospitalEntity();
        entity.setId(source.getId());
        entity.setName(source.getName());
        entity.setProvince(source.getProvince());
        entity.setCity(source.getCity());
        entity.setDistrict(source.getDistrict());
        entity.setAddress(source.getAddress());
        entity.setEnable(source.getEnable());
        entity.setAliasName(source.getAliasName());
        entity.setSupportGo2nurse(source.getSupportGo2nurse());
        entity.setUniqueId(source.getUniqueId());
        return entity;
    }
}
