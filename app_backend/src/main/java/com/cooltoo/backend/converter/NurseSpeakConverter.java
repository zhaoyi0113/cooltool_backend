package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 3/15/16.
 */
@Component
public class NurseSpeakConverter implements Converter<NurseSpeakEntity, NurseSpeakBean> {

    @Override
    public NurseSpeakBean convert(NurseSpeakEntity source) {
        NurseSpeakBean bean = new NurseSpeakBean();
        bean.setUserId(source.getUserId());
        bean.setContent(source.getContent());
        bean.setTime(source.getTime());
        bean.setId(source.getId());
        return bean;
    }
}
