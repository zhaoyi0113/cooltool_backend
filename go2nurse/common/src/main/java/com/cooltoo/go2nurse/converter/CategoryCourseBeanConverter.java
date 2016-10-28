package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CaseBean;
import com.cooltoo.go2nurse.beans.CategoryCourseOrderBean;
import com.cooltoo.go2nurse.entities.CaseEntity;
import com.cooltoo.go2nurse.entities.CategoryCourseOrderEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/10/28.
 */
@Component
public class CategoryCourseBeanConverter implements Converter<CategoryCourseOrderEntity, CategoryCourseOrderBean> {
    @Override
    public CategoryCourseOrderBean convert(CategoryCourseOrderEntity source) {
        CategoryCourseOrderBean bean = new CategoryCourseOrderBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setCategoryId(source.getCategoryId());
        bean.setCourseId(source.getCourseId());
        bean.setOrder(source.getOrder());
        return bean;
    }
}
