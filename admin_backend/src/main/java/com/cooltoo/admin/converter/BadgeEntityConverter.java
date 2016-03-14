package com.cooltoo.admin.converter;

import com.cooltoo.admin.entities.BadgeEntity;
import com.cooltoo.admin.beans.BadgeBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 2/24/16.
 */
@Component
public class BadgeEntityConverter implements Converter<BadgeBean,BadgeEntity> {
    @Override
    public BadgeEntity convert(BadgeBean source) {
        BadgeEntity entity = new BadgeEntity();
        entity.setId(source.getId());
        entity.setName(source.getName());
        entity.setGrade(source.getGrade());
        entity.setImageUrl(source.getImageUrl());
        entity.setPoint(source.getPoint());
        entity.setFileId(source.getFileId());
        return entity;
    }
}
