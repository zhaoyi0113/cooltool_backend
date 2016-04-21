package com.cooltoo.converter;

import com.cooltoo.beans.ActivityBean;
import com.cooltoo.entities.ActivityEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/4/20.
 */
@Component
public class ActivityBeanConverter implements Converter<ActivityEntity, ActivityBean> {
    @Override
    public ActivityBean convert(ActivityEntity source) {
        ActivityBean bean = new ActivityBean();
        bean.setId(source.getId());
        bean.setTitle(source.getTitle());
        bean.setSubtitle(source.getSubtitle());
        bean.setDescription(source.getDescription());
        bean.setTime(source.getTime());
        bean.setPlace(source.getPlace());
        bean.setPrice(source.getPrice());
        bean.setContent(source.getContent());
        bean.setCreateTime(source.getCreateTime());
        bean.setStatus(source.getStatus());
        bean.setFrontCover(source.getFrontCover());
        return bean;
    }
}
