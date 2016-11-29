package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.Nurse360DeviceTokensBean;
import com.cooltoo.go2nurse.entities.Nurse360DeviceTokensEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@Component
public class Nurse360DeviceTokensBeanConverter implements Converter<Nurse360DeviceTokensEntity, Nurse360DeviceTokensBean> {
    @Override
    public Nurse360DeviceTokensBean convert(Nurse360DeviceTokensEntity source) {
        Nurse360DeviceTokensBean bean = new Nurse360DeviceTokensBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setDeviceType(source.getDeviceType());
        bean.setDeviceToken(source.getDeviceToken());
        return bean;
    }
}
