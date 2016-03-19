package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/3/18.
 */
@Component
public class NurseSpeakThumbsUpEntityConverter implements Converter<NurseSpeakThumbsUpBean, NurseSpeakThumbsUpEntity> {
    @Override
    public NurseSpeakThumbsUpEntity convert(NurseSpeakThumbsUpBean source) {
        NurseSpeakThumbsUpEntity entity = new NurseSpeakThumbsUpEntity();
        entity.setId(source.getId());
        entity.setNurseSpeakId(source.getNurseSpeakId());
        entity.setThumbsUpUserId(source.getThumbsUpUserId());
        entity.setTime(source.getTime());
        return entity;
    }
}
