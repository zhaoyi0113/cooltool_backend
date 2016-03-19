package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/3/18.
 */
@Component
public class NurseSpeakThumbsUpBeanConverter implements Converter<NurseSpeakThumbsUpEntity, NurseSpeakThumbsUpBean> {
    @Override
    public NurseSpeakThumbsUpBean convert(NurseSpeakThumbsUpEntity source) {
        NurseSpeakThumbsUpBean bean = new NurseSpeakThumbsUpBean();
        bean.setId(source.getId());
        bean.setNurseSpeakId(source.getNurseSpeakId());
        bean.setThumbsUpUserId(source.getThumbsUpUserId());
        bean.setTime(source.getTime());
        return bean;
    }
}
