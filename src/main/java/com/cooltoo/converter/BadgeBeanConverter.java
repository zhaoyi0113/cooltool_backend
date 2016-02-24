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
        bean.setImageUrl(source.getImageUrl());
        bean.setPoint(source.getPoint());
        return bean;
    }
}
