package com.cooltoo.backend.converter;

import com.cooltoo.backend.entities.HospitalDepartmentRelationEntity;
import com.cooltoo.beans.HospitalDepartmentRelationBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class HospitalDepartmentRelationEntityConverter implements Converter<HospitalDepartmentRelationBean, HospitalDepartmentRelationEntity> {
    @Override
    public HospitalDepartmentRelationEntity convert(HospitalDepartmentRelationBean source) {
        HospitalDepartmentRelationEntity entity = new HospitalDepartmentRelationEntity();
        entity.setId(source.getId());
        entity.setHospitalId(source.getHospitalId());
        entity.setDepartmentId(source.getDepartmentId());
        return entity;
    }
}
