package com.cooltoo.go2nurse.patient.converter;

import com.cooltoo.go2nurse.patient.beans.PatientBadgeBean;
import com.cooltoo.go2nurse.patient.entities.PatientBadgeEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/3.
 */
@Component
public class PatientBadgeEntityConverter implements Converter<PatientBadgeBean, PatientBadgeEntity> {

    @Override
    public PatientBadgeEntity convert(PatientBadgeBean bean) {
        PatientBadgeEntity entity = new PatientBadgeEntity();
        entity.setId(bean.getId());
        entity.setBadgeId(bean.getBadgeId());
        entity.setPatientId(bean.getPatientId());
        return entity;
    }
}
