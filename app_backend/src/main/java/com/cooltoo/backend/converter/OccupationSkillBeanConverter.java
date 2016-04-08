package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.entities.OccupationSkillEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 3/10/16.
 */
@Component
public class OccupationSkillBeanConverter implements Converter<OccupationSkillEntity, OccupationSkillBean> {

    @Override
    public OccupationSkillBean convert(OccupationSkillEntity source) {
        OccupationSkillBean bean = new OccupationSkillBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setImageId(source.getImageId());
        bean.setDisableImageId(source.getDisableImageId());
        bean.setFactor(source.getFactor());
        return bean;
    }
}
