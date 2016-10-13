package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.Nurse360NotificationHospitalRelationBean;
import com.cooltoo.nurse360.entities.Nurse360NotificationHospitalRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/10/9.
 */
@Component
public class Nurse360NotificationHospitalRelationBeanConverter implements Converter<Nurse360NotificationHospitalRelationEntity, Nurse360NotificationHospitalRelationBean> {
    @Override
    public Nurse360NotificationHospitalRelationBean convert(Nurse360NotificationHospitalRelationEntity source) {
        Nurse360NotificationHospitalRelationBean bean = new Nurse360NotificationHospitalRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNotificationId(source.getNotificationId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        return bean;
    }
}
