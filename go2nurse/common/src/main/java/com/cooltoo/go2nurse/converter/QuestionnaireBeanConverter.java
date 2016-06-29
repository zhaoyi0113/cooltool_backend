package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.entities.QuestionnaireEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/28.
 */
@Component
public class QuestionnaireBeanConverter implements Converter<QuestionnaireEntity, QuestionnaireBean> {
    @Override
    public QuestionnaireBean convert(QuestionnaireEntity source) {
        QuestionnaireBean bean = new QuestionnaireBean();
        bean.setId(source.getId());
        bean.setTitle(source.getTitle());
        bean.setDescription(source.getDescription());
        bean.setConclusion(source.getConclusion());
        bean.setHospitalId(source.getHospitalId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
