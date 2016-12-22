package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.entities.Nurse360NotificationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/10/11.
 */
@Component
public class Nurse360NotificationBeanConverter implements Converter<Nurse360NotificationEntity, Nurse360NotificationBean> {

    @Override
    public Nurse360NotificationBean convert(Nurse360NotificationEntity source) {
        Nurse360NotificationBean bean = new Nurse360NotificationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setTitle(source.getTitle());
        bean.setIntroduction(source.getIntroduction());
        bean.setContent(source.getContent());
        bean.setSignificance(source.getSignificance());
        bean.setVendorType(source.getVendorType());
        bean.setVendorId(source.getVendorId());
        bean.setDepartId(source.getDepartId());
        return bean;
    }
}
