package com.cooltoo.converter;

import com.cooltoo.beans.PlatformVersionBean;
import com.cooltoo.entities.PlatformVersionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 5/22/16.
 */
@Component
public class PlatformVersionEntityConverter implements Converter<PlatformVersionBean, PlatformVersionEntity> {
    @Override
    public PlatformVersionEntity convert(PlatformVersionBean source) {
        PlatformVersionEntity entity = new PlatformVersionEntity();
        entity.setId(source.getId());
        entity.setStatus(source.getStatus());
        entity.setVersion(source.getVersion());
        entity.setTimeCreated(source.getTimeCreated());
        entity.setPlatformType(source.getPlatformType());
        entity.setLink(source.getLink());
        entity.setRequired(source.getRequired());
        entity.setMessage(source.getMessage());
        entity.setReleaseNote(source.getReleaseNote());
        return entity;
    }
}
