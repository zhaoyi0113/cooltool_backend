package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakTopicBean;
import com.cooltoo.backend.entities.NurseSpeakTopicEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/2.
 */
@Component
public class NurseSpeakTopicConverter implements Converter<NurseSpeakTopicEntity, NurseSpeakTopicBean> {

    @Override
    public NurseSpeakTopicBean convert(NurseSpeakTopicEntity source) {
        NurseSpeakTopicBean bean = new NurseSpeakTopicBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCreatorId(source.getCreatorId());
        bean.setTitle(source.getTitle());
        bean.setProfileImageId(source.getProfileImageId());
        bean.setLabel(source.getLabel());
        bean.setTaxonomy(source.getTaxonomy());
        bean.setDescription(source.getDescription());
        bean.setProvince(source.getProvince());
        bean.setClickNumber(source.getClickNumber());
        // following properties need calculate
        bean.setCommentNumber(0);
        bean.setSubscriberNumber(0);
        return bean;
    }
}
