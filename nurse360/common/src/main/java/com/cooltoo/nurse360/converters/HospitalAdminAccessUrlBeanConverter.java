package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.HospitalAdminAccessUrlBean;
import com.cooltoo.nurse360.entities.HospitalAdminAccessUrlEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Component
public class HospitalAdminAccessUrlBeanConverter implements Converter<HospitalAdminAccessUrlEntity, HospitalAdminAccessUrlBean> {
    @Override
    public HospitalAdminAccessUrlBean convert(HospitalAdminAccessUrlEntity source) {
        HospitalAdminAccessUrlBean bean = new HospitalAdminAccessUrlBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setAdminId(source.getAdminId());
        bean.setUrlId(source.getUrlId());
        return bean;
    }
}
