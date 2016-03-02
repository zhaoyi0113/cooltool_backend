package com.cooltoo.converter;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.entities.NurseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Component
public class NurseBeanConverter implements Converter<NurseEntity, NurseBean> {
    @Override
    public NurseBean convert(NurseEntity entity) {
        NurseBean bean = new NurseBean();
        bean.setId(entity.getId());
        bean.setIdentificationId(entity.getIdentificationId());
        bean.setName(entity.getName());
        bean.setGender(entity.getGender());
        bean.setAge(entity.getAge());
        bean.setMobile(entity.getMobile());
        return bean;
    }
}
