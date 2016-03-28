package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.entities.SpeakTypeEntity;
import com.cooltoo.constants.SpeakType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/28.
 */
@Component
public class SpeakTypeBeanConverter implements Converter<SpeakTypeEntity, SpeakTypeBean> {
    @Override
    public SpeakTypeBean convert(SpeakTypeEntity source) {
        SpeakTypeBean speakType = new SpeakTypeBean();
        speakType.setId(source.getId());
        speakType.setName(source.getName());
        speakType.setType(source.getType());
        speakType.setFactor(source.getFactor());
        speakType.setImageId(source.getImageId());
        speakType.setDisableImageId(source.getDisableImageId());
        return speakType;
    }
}
