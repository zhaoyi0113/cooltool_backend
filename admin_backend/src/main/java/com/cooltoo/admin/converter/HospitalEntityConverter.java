package com.cooltoo.admin.converter;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.admin.entities.HospitalEntity;
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
        return entity;
    }
}
