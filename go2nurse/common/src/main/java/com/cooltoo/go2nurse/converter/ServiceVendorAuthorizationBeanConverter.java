package com.cooltoo.go2nurse.converter;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.ServiceVendorAuthorizationBean;
import com.cooltoo.go2nurse.entities.ServiceVendorAuthorizationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/12/1.
 */
@Component
public class ServiceVendorAuthorizationBeanConverter implements Converter<ServiceVendorAuthorizationEntity, ServiceVendorAuthorizationBean> {

    @Override
    public ServiceVendorAuthorizationBean convert(ServiceVendorAuthorizationEntity source) {
        ServiceVendorAuthorizationBean bean = new ServiceVendorAuthorizationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setForbidden(CommonStatus.ENABLED.equals(source.getStatus()));
        bean.setUserId(source.getUserId());
        bean.setVendorType(source.getVendorType());
        bean.setVendorId(source.getVendorId());
        bean.setDepartId(source.getDepartId());
        return bean;
    }
}
