package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.entities.CourseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/8.
 */
@Component
public class CourseBeanConverter implements Converter<CourseEntity, CourseBean> {
    @Override
    public CourseBean convert(CourseEntity source) {
        CourseBean bean = new CourseBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setIntroduction(source.getIntroduction());
        bean.setContent(source.getContent());
        bean.setFrontCover(source.getFrontCover());
        bean.setLink(source.getLink());
        bean.setUniqueId(source.getUniqueId());
        return bean;
    }
}
