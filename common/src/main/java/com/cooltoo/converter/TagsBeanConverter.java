package com.cooltoo.converter;

import com.cooltoo.beans.TagsBean;
import com.cooltoo.entities.TagsEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Component
public class TagsBeanConverter implements Converter<TagsEntity, TagsBean> {
    @Override
    public TagsBean convert(TagsEntity source) {
        TagsBean bean = new TagsBean();
        bean.setId(source.getId());
        bean.setCategoryId(source.getCategoryId());
        bean.setName(source.getName());
        bean.setImageId(source.getImageId());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;
    }
}
