package com.cooltoo.converter;

import com.cooltoo.beans.PlatformVersionBean;
import com.cooltoo.entities.PlatformVersionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 5/22/16.
 */
@Component
public class PlatformVersionBeanConverter implements Converter<PlatformVersionEntity, PlatformVersionBean> {
    @Override
    public PlatformVersionBean convert(PlatformVersionEntity source) {
        PlatformVersionBean bean = new PlatformVersionBean();
        bean.setId(source.getId());
        bean.setStatus(source.getStatus());
        bean.setVersion(source.getVersion());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setPlatformType(source.getPlatformType());
        bean.setLink(source.getLink());
        bean.setRequired(source.getRequired());
        return bean;
    }
}
