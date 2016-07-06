package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.entities.QuestionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/28.
 */
@Component
public class QuestionBeanConverter implements Converter<QuestionEntity, QuestionBean> {
    @Override
    public QuestionBean convert(QuestionEntity source) {
        QuestionBean bean = new QuestionBean();
        bean.setId(source.getId());
        bean.setQuestionnaireId(source.getQuestionnaireId());
        bean.setContent(source.getContent());
        bean.setOptions(source.getOptions());
        bean.setType(source.getType());
        bean.setGrade(source.getGrade());
        bean.setImageId(source.getImageId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
