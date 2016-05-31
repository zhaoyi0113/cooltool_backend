package com.cooltoo.converter;

import com.cooltoo.beans.SensitiveWordBean;
import com.cooltoo.entities.SensitiveWordEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/5/31.
 */
@Component
public class SensitiveWordBeanConverter implements Converter<SensitiveWordEntity, SensitiveWordBean> {
    @Override
    public SensitiveWordBean convert(SensitiveWordEntity source) {
        SensitiveWordBean bean = new SensitiveWordBean();
        bean.setId(source.getId());
        bean.setWord(source.getWord());
        bean.setType(source.getType());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
