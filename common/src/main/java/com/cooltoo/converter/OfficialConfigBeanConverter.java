package com.cooltoo.converter;

import com.cooltoo.beans.OfficialConfigBean;
import com.cooltoo.entities.OfficialConfigEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/5/9.
 */
@Component
public class OfficialConfigBeanConverter implements Converter<OfficialConfigEntity, OfficialConfigBean> {
    @Override
    public OfficialConfigBean convert(OfficialConfigEntity source) {
        OfficialConfigBean configBean = new OfficialConfigBean();
        configBean.setId(source.getId());
        configBean.setName(source.getName());
        configBean.setValue(source.getValue());
        configBean.setImageId(source.getImageId());
        configBean.setStatus(source.getStatus());
        configBean.setCreateTime(source.getCreateTime());
        return configBean;
    }
}
