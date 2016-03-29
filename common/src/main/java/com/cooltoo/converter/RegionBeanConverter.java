package com.cooltoo.converter;

import com.cooltoo.beans.RegionBean;
import com.cooltoo.entities.RegionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Component
public class RegionBeanConverter implements Converter<RegionEntity, RegionBean> {

    @Override
    public RegionBean convert(RegionEntity source) {
        RegionBean bean = new RegionBean();
        bean.setId(source.getId());
        bean.setCode(source.getCode());
        bean.setName(source.getName());
        bean.setEnName(source.getEnName());
        bean.setParentId(source.getParentId());
        return bean;
    }
}
