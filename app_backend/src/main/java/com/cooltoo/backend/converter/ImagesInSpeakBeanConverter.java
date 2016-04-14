package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.ImagesInSpeakBean;
import com.cooltoo.backend.entities.ImagesInSpeakEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/4/14.
 */
@Component
public class ImagesInSpeakBeanConverter implements Converter<ImagesInSpeakEntity, ImagesInSpeakBean> {
    @Override
    public ImagesInSpeakBean convert(ImagesInSpeakEntity source) {
        ImagesInSpeakBean bean = new ImagesInSpeakBean();
        bean.setId(source.getId());
        bean.setSpeakId(source.getSpeakId());
        bean.setImageId(source.getImageId());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;
    }
}
