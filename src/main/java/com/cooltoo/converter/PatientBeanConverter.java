package com.cooltoo.converter;

import com.cooltoo.beans.PatientBean;
import com.cooltoo.entities.PatientEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 2/29/16.
 */
@Component
public class PatientBeanConverter implements Converter<PatientEntity, PatientBean> {
    @Override
    public PatientBean convert(PatientEntity source) {
        PatientBean bean = new PatientBean();
        bean.setOfficeId(source.getOfficeId());
        bean.setNickname(source.getNickname());
        bean.setName(source.getName());
        bean.setMobile(source.getMobile());
        bean.setCertificateId(source.getCertificateId());
        bean.setId(source.getId());
        bean.setAge(source.getAge());
        bean.setBirthday(source.getBirthday());
        bean.setUsercol(source.getUsercol());
        return bean;
    }
}
