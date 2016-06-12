package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CourseDepartmentRelationBean;
import com.cooltoo.go2nurse.entities.CourseDepartmentRelationEntity;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by hp on 2016/6/12.
 */
public class CourseDepartmentRelationBeanConverter implements Converter<CourseDepartmentRelationEntity, CourseDepartmentRelationBean> {
    @Override
    public CourseDepartmentRelationBean convert(CourseDepartmentRelationEntity source) {
        CourseDepartmentRelationBean bean = new CourseDepartmentRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCourseId(source.getCourseId());
        bean.setDepartmentId(source.getDepartmentId());
        return bean;
    }
}
