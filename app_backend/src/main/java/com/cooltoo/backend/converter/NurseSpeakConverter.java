package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
        bean.setSpeakType(source.getSpeakType());
        bean.setAnonymousName(source.getAnonymousName());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
