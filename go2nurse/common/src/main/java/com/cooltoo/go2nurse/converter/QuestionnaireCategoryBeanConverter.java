package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.QuestionnaireCategoryBean;
import com.cooltoo.go2nurse.beans.QuestionnaireConclusionBean;
import com.cooltoo.go2nurse.entities.QuestionnaireCategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/5.
 */
@Component
public class QuestionnaireCategoryBeanConverter implements Converter<QuestionnaireCategoryEntity, QuestionnaireCategoryBean> {
    @Override
    public QuestionnaireCategoryBean convert(QuestionnaireCategoryEntity source) {
        QuestionnaireCategoryBean bean = new QuestionnaireCategoryBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setInstruction(source.getInstruction());
        return bean;
    }
}
