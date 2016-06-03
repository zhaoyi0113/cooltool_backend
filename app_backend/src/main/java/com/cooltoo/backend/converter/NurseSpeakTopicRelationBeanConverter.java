package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakTopicRelationBean;
import com.cooltoo.backend.entities.NurseSpeakTopicRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/2.
 */
@Component
public class NurseSpeakTopicRelationBeanConverter implements Converter<NurseSpeakTopicRelationEntity, NurseSpeakTopicRelationBean> {
    @Override
    public NurseSpeakTopicRelationBean convert(NurseSpeakTopicRelationEntity source) {
        NurseSpeakTopicRelationBean bean = new NurseSpeakTopicRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setTopicId(source.getTopicId());
        bean.setSpeakId(source.getSpeakId());
        return bean;
    }
}
