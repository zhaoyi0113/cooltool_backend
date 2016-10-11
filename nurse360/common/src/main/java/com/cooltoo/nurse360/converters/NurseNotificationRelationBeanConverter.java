package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.NurseNotificationRelationBean;
import com.cooltoo.nurse360.entities.NurseNotificationRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/10/11.
 */
@Component
public class NurseNotificationRelationBeanConverter implements Converter<NurseNotificationRelationEntity, NurseNotificationRelationBean> {
    @Override
    public NurseNotificationRelationBean convert(NurseNotificationRelationEntity source) {
        NurseNotificationRelationBean bean = new NurseNotificationRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setNotificationId(source.getNotificationId());
        bean.setReadingStatus(source.getReadingStatus());
        return bean;
    }
}
