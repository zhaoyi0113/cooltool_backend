package com.cooltoo.go2nurse.patient.converter;

import com.cooltoo.go2nurse.patient.beans.PatientBadgeBean;
import com.cooltoo.go2nurse.patient.entities.PatientBadgeEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/3.
 */
@Component
public class PatientBadgeBeanConverter implements Converter<PatientBadgeEntity, PatientBadgeBean>{

    @Override
    public PatientBadgeBean convert(PatientBadgeEntity entity) {
        PatientBadgeBean bean = new PatientBadgeBean();
        bean.setId(entity.getId());
        bean.setBadgeId(entity.getBadgeId());
        bean.setPatientId(entity.getPatientId());
        return bean;
    }
}
