package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceVendorBean;
import com.cooltoo.go2nurse.entities.ServiceVendorEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/19.
 */
@Component
public class ServiceVendorBeanConverter implements Converter<ServiceVendorEntity, ServiceVendorBean> {
    @Override
    public ServiceVendorBean convert(ServiceVendorEntity source) {
        ServiceVendorBean bean = new ServiceVendorBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setDescription(source.getDescription());
        bean.setLogoId(source.getLogoId());
        return bean;
    }
}
