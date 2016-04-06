package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.SuggestionBean;
import com.cooltoo.backend.entities.SuggestionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Component
public class SuggestionBeanConverter implements Converter<SuggestionEntity, SuggestionBean> {
    @Override
    public SuggestionBean convert(SuggestionEntity source) {
        SuggestionBean bean = new SuggestionBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setSuggestion(source.getSuggestion());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;
    }
}
