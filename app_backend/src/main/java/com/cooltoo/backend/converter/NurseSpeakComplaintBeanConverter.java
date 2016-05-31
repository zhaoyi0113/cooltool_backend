package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakComplaintBean;
import com.cooltoo.backend.entities.NurseSpeakComplaintEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/5/30.
 */
@Component
public class NurseSpeakComplaintBeanConverter implements Converter<NurseSpeakComplaintEntity, NurseSpeakComplaintBean> {

    @Override
    public NurseSpeakComplaintBean convert(NurseSpeakComplaintEntity source) {
        if (null==source) {
            return null;
        }
        NurseSpeakComplaintBean bean = new NurseSpeakComplaintBean();
        bean.setId(source.getId());
        bean.setInformantId(source.getInformantId());
        bean.setSpeakId(source.getSpeakId());
        bean.setReason(source.getReason());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
