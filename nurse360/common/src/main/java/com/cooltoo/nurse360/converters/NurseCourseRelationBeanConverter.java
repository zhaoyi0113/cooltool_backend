package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.NurseCourseRelationBean;
import com.cooltoo.nurse360.entities.NurseCourseRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/10/9.
 */
@Component
public class NurseCourseRelationBeanConverter implements Converter<NurseCourseRelationEntity, NurseCourseRelationBean> {
    @Override
    public NurseCourseRelationBean convert(NurseCourseRelationEntity source) {
        NurseCourseRelationBean bean = new NurseCourseRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setCourseId(source.getCourseId());
        bean.setReadingStatus(source.getReadingStatus());
        return bean;
    }
}
