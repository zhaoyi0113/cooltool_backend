package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.entities.CourseCategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/8.
 */
@Component
public class CourseCategoryBeanConverter implements Converter<CourseCategoryEntity, CourseCategoryBean> {
    @Override
    public CourseCategoryBean convert(CourseCategoryEntity source) {
        CourseCategoryBean bean = new CourseCategoryBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setIntroduction(source.getIntroduction());
        bean.setImageId(source.getImageId());
        return bean;
    }
}
