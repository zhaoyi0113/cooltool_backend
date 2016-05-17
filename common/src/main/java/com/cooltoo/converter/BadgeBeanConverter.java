package com.cooltoo.converter;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.entities.BadgeEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 2/24/16.
 */
@Component
public class BadgeBeanConverter implements Converter<BadgeEntity, BadgeBean> {
    @Override
    public BadgeBean convert(BadgeEntity source) {
        BadgeBean bean = new BadgeBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setGrade(source.getGrade());
        bean.setPoint(source.getPoint());
        bean.setImageId(source.getImageId());
        bean.setAbilityId(source.getAbilityId());
        bean.setAbilityType(source.getAbilityType());
        bean.setDescription(source.getDescription());
        return bean;
    }
}
