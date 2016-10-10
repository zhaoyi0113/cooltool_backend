package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.Nurse360CourseCategoryBean;
import com.cooltoo.nurse360.entities.Nurse360CourseCategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/10/9.
 */
@Component
public class Nurse360CourseCategoryBeanConverter implements Converter<Nurse360CourseCategoryEntity, Nurse360CourseCategoryBean> {
    @Override
    public Nurse360CourseCategoryBean convert(Nurse360CourseCategoryEntity source) {
        Nurse360CourseCategoryBean bean = new Nurse360CourseCategoryBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setIntroduction(source.getIntroduction());
        bean.setImageId(source.getImageId());
        return bean;
    }
}
