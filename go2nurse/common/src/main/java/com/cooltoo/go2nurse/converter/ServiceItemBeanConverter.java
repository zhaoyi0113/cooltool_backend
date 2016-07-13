package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.entities.ServiceItemEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/13.
 */
@Component
public class ServiceItemBeanConverter implements Converter<ServiceItemEntity, ServiceItemBean> {
    @Override
    public ServiceItemBean convert(ServiceItemEntity source) {
        ServiceItemBean bean = new ServiceItemBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCategoryId(source.getCategoryId());
        bean.setName(source.getName());
        bean.setClazz(source.getClazz());
        bean.setDescription(source.getDescription());
        bean.setImageId(source.getImageId());
        bean.setServicePrice(source.getServicePrice());
        bean.setServiceTimeDuration(source.getServiceTimeDuration());
        bean.setServiceTimeUnit(source.getServiceTimeUnit());
        return bean;
    }
}
