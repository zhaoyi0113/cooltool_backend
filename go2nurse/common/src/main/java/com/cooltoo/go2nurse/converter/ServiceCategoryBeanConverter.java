package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.entities.ServiceCategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/13.
 */
@Component
public class ServiceCategoryBeanConverter implements Converter<ServiceCategoryEntity, ServiceCategoryBean> {
    @Override
    public ServiceCategoryBean convert(ServiceCategoryEntity source) {
        ServiceCategoryBean bean = new ServiceCategoryBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setDescription(source.getDescription());
        bean.setImageId(source.getImageId());
        bean.setGrade(source.getGrade());
        bean.setParentId(source.getParentId());
        return bean;
    }
}
