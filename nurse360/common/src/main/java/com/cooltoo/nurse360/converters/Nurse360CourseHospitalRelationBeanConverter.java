package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.Nurse360CourseHospitalRelationBean;
import com.cooltoo.nurse360.entities.Nurse360CourseHospitalRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/10/9.
 */
@Component
public class Nurse360CourseHospitalRelationBeanConverter implements Converter<Nurse360CourseHospitalRelationEntity, Nurse360CourseHospitalRelationBean> {
    @Override
    public Nurse360CourseHospitalRelationBean convert(Nurse360CourseHospitalRelationEntity source) {
        Nurse360CourseHospitalRelationBean bean = new Nurse360CourseHospitalRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCourseId(source.getCourseId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        return bean;
    }
}
