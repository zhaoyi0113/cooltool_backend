package com.cooltoo.go2nurse.converter;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.go2nurse.beans.UserQuestionnaireAnswerBean;
import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
@Component
public class UserQuestionnaireAnswerBeanConverter implements Converter<UserQuestionnaireAnswerEntity, UserQuestionnaireAnswerBean> {
    @Override
    public UserQuestionnaireAnswerBean convert(UserQuestionnaireAnswerEntity source) {
        UserQuestionnaireAnswerBean bean = new UserQuestionnaireAnswerBean();

        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setGroupId(source.getGroupId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setPatientName(source.getPatientName());
        bean.setPatientGender(source.getPatientGender());
        bean.setPatientAge(source.getPatientAge());
        bean.setPatientMobile(source.getPatientMobile());
        bean.setQuestionnaireId(source.getQuestionnaireId());
        bean.setQuestionnaireName(source.getQuestionnaireName());
        bean.setQuestionnaireConclusion(source.getQuestionnaireConclusion());
        bean.setQuestionId(source.getQuestionId());
        bean.setQuestionContent(source.getQuestionContent());
        bean.setAnswer(source.getAnswer());
        bean.setAnswerCompleted(source.getAnswerCompleted());
        bean.setHospitalId(source.getHospitalId());
        bean.setHospitalName(source.getHospitalName());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setDepartmentName(source.getDepartmentName());

        return bean;
    }
}
