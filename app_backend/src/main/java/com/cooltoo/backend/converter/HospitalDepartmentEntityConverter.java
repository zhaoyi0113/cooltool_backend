package com.cooltoo.backend.converter;

import com.cooltoo.backend.entities.HospitalDepartmentEntity;
import com.cooltoo.beans.HospitalDepartmentBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class HospitalDepartmentEntityConverter implements Converter<HospitalDepartmentBean, HospitalDepartmentEntity> {
    @Override
    public HospitalDepartmentEntity convert(HospitalDepartmentBean source) {
        HospitalDepartmentEntity entity = new HospitalDepartmentEntity();
        entity.setId(source.getId());
        entity.setName(source.getName());
        return entity;
    }
}