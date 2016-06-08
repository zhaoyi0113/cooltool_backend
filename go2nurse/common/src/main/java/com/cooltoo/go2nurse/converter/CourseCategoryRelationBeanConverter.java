package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CourseCategoryRelationBean;
import com.cooltoo.go2nurse.entities.CourseCategoryRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/8.
 */
@Component
public class CourseCategoryRelationBeanConverter implements Converter<CourseCategoryRelationEntity, CourseCategoryRelationBean> {
    @Override
    public CourseCategoryRelationBean convert(CourseCategoryRelationEntity source) {
        CourseCategoryRelationBean bean = new CourseCategoryRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCourseId(source.getCourseId());
        bean.setCourseCategoryId(source.getCourseCategoryId());
        return bean;
    }
}
