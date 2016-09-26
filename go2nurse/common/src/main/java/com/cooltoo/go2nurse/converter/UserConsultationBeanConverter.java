package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.entities.UserConsultationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/28.
 */
@Component
public class UserConsultationBeanConverter implements Converter<UserConsultationEntity, UserConsultationBean> {
    @Override
    public UserConsultationBean convert(UserConsultationEntity source) {
        UserConsultationBean bean = new UserConsultationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCategoryId(source.getCategoryId());
        bean.setClinicalHistory(source.getClinicalHistory());
        bean.setDiseaseDescription(source.getDiseaseDescription());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setNurseId(source.getNurseId());
        bean.setCompleted(source.getCompleted());
        bean.setScore(source.getScore());
        return bean;
    }
}
