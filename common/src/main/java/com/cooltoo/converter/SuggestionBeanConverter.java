package com.cooltoo.converter;

import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.entities.SuggestionEntity;
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
        bean.setTimeCreated(source.getTimeCreated());
        bean.setStatus(source.getStatus());
        bean.setSuggestion(source.getSuggestion());
        bean.setUserId(source.getUserId());
        bean.setUserType(source.getUserType());
        bean.setUserName(source.getUserName());
        bean.setPlatform(source.getPlatform());
        bean.setVersion(source.getVersion());
        return bean;
    }
}
