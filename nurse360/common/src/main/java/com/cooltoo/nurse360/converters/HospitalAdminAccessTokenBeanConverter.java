package com.cooltoo.nurse360.converters;

import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.entities.HospitalAdminAccessTokenEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Component
public class HospitalAdminAccessTokenBeanConverter implements Converter<HospitalAdminAccessTokenEntity, HospitalAdminAccessTokenBean> {
    @Override
    public HospitalAdminAccessTokenBean convert(HospitalAdminAccessTokenEntity source) {
        HospitalAdminAccessTokenBean bean = new HospitalAdminAccessTokenBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setAdminId(source.getAdminId());
        bean.setToken(source.getToken());
        return bean;
    }
}
