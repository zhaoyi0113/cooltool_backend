package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.AdvertisementBean;
import com.cooltoo.go2nurse.entities.AdvertisementEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/9/6.
 */
@Component
public class AdvertisementBeanConverter implements Converter<AdvertisementEntity, AdvertisementBean> {
    @Override
    public AdvertisementBean convert(AdvertisementEntity source) {
        AdvertisementBean bean = new AdvertisementBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setFrontCover(source.getFrontCover());
        bean.setDetailsUrl(source.getDetailsUrl());
        bean.setOrder(source.getOrderIndex());
        bean.setDescription(source.getDescription());
        return bean;
    }
}
