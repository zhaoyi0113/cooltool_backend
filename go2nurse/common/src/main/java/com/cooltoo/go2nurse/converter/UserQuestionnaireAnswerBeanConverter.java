package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserQuestionnaireAnswerBean;
import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/28.
 */
@Component
public class UserQuestionnaireAnswerBeanConverter implements Converter<UserQuestionnaireAnswerEntity, UserQuestionnaireAnswerBean> {
    @Override
    public UserQuestionnaireAnswerBean convert(UserQuestionnaireAnswerEntity source) {
        UserQuestionnaireAnswerBean bean = new UserQuestionnaireAnswerBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setGroupId(source.getGroupId());
        bean.setPatientId(source.getPatientId());
        bean.setQuestionnaireId(source.getQuestionnaireId());
        bean.setQuestionId(source.getQuestionId());
        bean.setAnswer(source.getAnswer());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
