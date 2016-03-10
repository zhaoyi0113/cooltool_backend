package com.cooltoo.converter;

import com.cooltoo.beans.OccupationSkillBean;
import com.cooltoo.entities.OccupationSkillEntity;
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
        return bean;
    }
}
