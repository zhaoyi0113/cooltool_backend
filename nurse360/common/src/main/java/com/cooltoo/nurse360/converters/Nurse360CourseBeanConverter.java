package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.Nurse360CourseBean;
import com.cooltoo.nurse360.entities.Nurse360CourseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/10/9.
 */
@Component
public class Nurse360CourseBeanConverter implements Converter<Nurse360CourseEntity, Nurse360CourseBean> {
    @Override
    public Nurse360CourseBean convert(Nurse360CourseEntity source) {
        Nurse360CourseBean bean = new Nurse360CourseBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setIntroduction(source.getIntroduction());
        bean.setContent(source.getContent());
        bean.setFrontCover(source.getFrontCover());
        bean.setLink(source.getLink());
        bean.setUniqueId(source.getUniqueId());
        bean.setKeyword(source.getKeyword());
        bean.setCategoryId(source.getCategoryId());
        bean.setPublisherId(source.getPublisherId());
        return bean;
    }
}
