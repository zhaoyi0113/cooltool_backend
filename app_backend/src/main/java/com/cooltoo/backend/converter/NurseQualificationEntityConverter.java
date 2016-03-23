package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.entities.NurseQualificationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Component
public class NurseQualificationEntityConverter implements Converter<NurseQualificationBean, NurseQualificationEntity> {

    @Override
    public NurseQualificationEntity convert(NurseQualificationBean source) {
        NurseQualificationEntity entity = new NurseQualificationEntity();
        entity.setId(source.getId());
        entity.setUserId(source.getUserId());
        entity.setName(source.getName());
        entity.setWorkFileType(source.getWorkFileType());
        entity.setWorkFileId(source.getWorkFileId());
        entity.setStatus(source.getStatus());
        entity.setTimeCreated(source.getTimeCreated());
        return entity;
    }
}
