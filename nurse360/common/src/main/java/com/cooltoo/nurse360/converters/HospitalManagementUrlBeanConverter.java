package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.HospitalManagementUrlBean;
import com.cooltoo.nurse360.entities.HospitalManagementUrlEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Component
public class HospitalManagementUrlBeanConverter implements Converter<HospitalManagementUrlEntity, HospitalManagementUrlBean> {
    @Override
    public HospitalManagementUrlBean convert(HospitalManagementUrlEntity source) {
        HospitalManagementUrlBean bean = new HospitalManagementUrlBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setHttpType(source.getHttpType());
        bean.setHttpUrl(source.getHttpUrl());
        bean.setIntroduction(source.getIntroduction());
        bean.setNeedToken(source.getNeedToken());
        return bean;
    }
}
