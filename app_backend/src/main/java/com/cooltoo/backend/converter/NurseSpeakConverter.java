package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 3/15/16.
 */
@Component
public class NurseSpeakConverter implements Converter<NurseSpeakEntity, NurseSpeakBean> {

    @Autowired
    private NurseRepository nurseRepository;

    @Override
    public NurseSpeakBean convert(NurseSpeakEntity source) {
        NurseSpeakBean bean = new NurseSpeakBean();
        bean.setUserId(source.getUserId());
        NurseEntity entity = nurseRepository.findOne(source.getUserId());
        if(entity != null){
            bean.setUserName(entity.getName());
        }
        bean.setContent(source.getContent());
        bean.setTime(source.getTime());
        bean.setId(source.getId());
        bean.setSpeakType(source.getSpeakType());
        return bean;
    }
}
