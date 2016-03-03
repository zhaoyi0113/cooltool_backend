package com.cooltoo.converter;

import com.cooltoo.beans.PatientBean;
import com.cooltoo.entities.PatientEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 2/29/16.
 */
@Component
public class PatientEntityConverter implements Converter<PatientBean, PatientEntity> {
    @Override
    public PatientEntity convert(PatientBean source) {
        PatientEntity entity = new PatientEntity();
        entity.setName(source.getName());
        entity.setCertificateId(source.getCertificateId());
        entity.setId(source.getId());
        entity.setMobile(source.getMobile());
        entity.setNickname(source.getNickname());
        entity.setOfficeId(source.getOfficeId());
        entity.setAge(source.getAge());
        entity.setBirthday(source.getBirthday());
        entity.setUsercol(source.getUsercol());
        return entity;
    }
}
