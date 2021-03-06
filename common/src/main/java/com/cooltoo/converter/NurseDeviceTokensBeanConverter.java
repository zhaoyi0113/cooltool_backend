package com.cooltoo.converter;

import com.cooltoo.beans.NurseDeviceTokensBean;
import com.cooltoo.entities.NurseDeviceTokensEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 4/29/16.
 */
@Component
public class NurseDeviceTokensBeanConverter implements Converter<NurseDeviceTokensEntity, NurseDeviceTokensBean> {
    @Override
    public NurseDeviceTokensBean convert(NurseDeviceTokensEntity source) {
        NurseDeviceTokensBean bean = new NurseDeviceTokensBean();
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setDeviceToken(source.getDeviceToken());
        bean.setId(source.getId());
        bean.setDeviceToken(source.getDeviceToken());
        return bean;
    }
}
