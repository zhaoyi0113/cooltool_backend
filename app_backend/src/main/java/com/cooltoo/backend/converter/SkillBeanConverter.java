package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.SkillBean;
import com.cooltoo.backend.entities.SkillEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 3/10/16.
 */
@Component
public class SkillBeanConverter implements Converter<SkillEntity, SkillBean> {

    @Override
    public SkillBean convert(SkillEntity source) {
        SkillBean bean = new SkillBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setImageId(source.getImageId());
        bean.setDisableImageId(source.getDisableImageId());
        bean.setFactor(source.getFactor());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
