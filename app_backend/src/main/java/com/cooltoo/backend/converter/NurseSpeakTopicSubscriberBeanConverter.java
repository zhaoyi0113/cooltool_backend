package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakTopicSubscriberBean;
import com.cooltoo.backend.entities.NurseSpeakTopicSubscriberEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/2.
 */
@Component
public class NurseSpeakTopicSubscriberBeanConverter implements Converter<NurseSpeakTopicSubscriberEntity, NurseSpeakTopicSubscriberBean> {
    @Override
    public NurseSpeakTopicSubscriberBean convert(NurseSpeakTopicSubscriberEntity source) {
        NurseSpeakTopicSubscriberBean bean = new NurseSpeakTopicSubscriberBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setTopicId(source.getTopicId());
        bean.setUserId(source.getUserId());
        bean.setUserType(source.getUserType());
        return bean;
    }
}
