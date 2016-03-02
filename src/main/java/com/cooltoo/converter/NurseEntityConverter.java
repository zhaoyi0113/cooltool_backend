package com.cooltoo.converter;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.entities.NurseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Component
public class NurseEntityConverter implements Converter<NurseBean, NurseEntity> {
    @Override
    public NurseEntity convert(NurseBean bean) {
        NurseEntity entity = new NurseEntity();
        entity.setId(bean.getId());
        entity.setIdentificationId(bean.getIdentificationId());
        entity.setName(bean.getName());
        entity.setGender(bean.getGender());
        entity.setAge(bean.getAge());
        entity.setMobile(bean.getMobile());
        return entity;
    }
}
