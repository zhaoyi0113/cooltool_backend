package com.cooltoo.converter;

import com.cooltoo.beans.TagsCategoryBean;
import com.cooltoo.entities.TagsCategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Component
public class TagsCategoryBeanConverter implements Converter<TagsCategoryEntity, TagsCategoryBean> {
    @Override
    public TagsCategoryBean convert(TagsCategoryEntity source) {
        TagsCategoryBean bean = new TagsCategoryBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setImageId(source.getImageId());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;
    }
}
