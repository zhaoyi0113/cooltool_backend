package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CourseHospitalRelationBean;
import com.cooltoo.go2nurse.entities.CourseHospitalRelationEntity;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by hp on 2016/6/12.
 */
public class CourseHospitalRelationBeanConverter implements Converter<CourseHospitalRelationEntity, CourseHospitalRelationBean> {
    @Override
    public CourseHospitalRelationBean convert(CourseHospitalRelationEntity source) {
        CourseHospitalRelationBean bean = new CourseHospitalRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCourseId(source.getCourseId());
        bean.setHospitalId(source.getHospitalId());
        return bean;
    }
}
