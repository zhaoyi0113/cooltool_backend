package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.PatientSymptomsBean;
import com.cooltoo.go2nurse.entities.PatientSymptomsEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 01/03/2017.
 */
@Component
public class PatientSymptomsBeanConverter implements Converter<PatientSymptomsEntity, PatientSymptomsBean> {

    @Override
    public PatientSymptomsBean convert(PatientSymptomsEntity source) {
        PatientSymptomsBean bean = new PatientSymptomsBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setOrderId(source.getOrderId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setSymptoms(source.getSymptoms());
        bean.setSymptomsDescription(source.getSymptomsDescription());
        bean.setSymptomsImages(source.getSymptomsImages());
        bean.setQuestionnaire(source.getQuestionnaire());
        return bean;
    }
}
