package com.cooltoo.converter;

import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.beans.NurseHospitalRelationBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class NurseHospitalRelationEntityConverter implements Converter<NurseHospitalRelationBean, NurseHospitalRelationEntity> {
    @Override
    public NurseHospitalRelationEntity convert(NurseHospitalRelationBean source) {
        NurseHospitalRelationEntity entity = new NurseHospitalRelationEntity();
        entity.setId(source.getId());
        entity.setNurseId(source.getNurseId());
        entity.setHospitalId(source.getHospitalId());
        entity.setDepartmentId(source.getDepartmentId());
        entity.setTime(source.getTime());
        entity.setStatus(source.getStatus());
        return entity;
    }
}
