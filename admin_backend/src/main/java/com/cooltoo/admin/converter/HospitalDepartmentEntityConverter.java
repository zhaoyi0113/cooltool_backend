package com.cooltoo.admin.converter;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.admin.entities.HospitalDepartmentEntity;
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
