package com.cooltoo.converter;

import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.beans.HospitalDepartmentBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class HospitalDepartmentBeanConverter implements Converter<HospitalDepartmentEntity, HospitalDepartmentBean> {
    @Override
    public HospitalDepartmentBean convert(HospitalDepartmentEntity source) {
        HospitalDepartmentBean bean = new HospitalDepartmentBean();
        bean.setId(source.getId());
        bean.setHospitalId(source.getHospitalId());
        bean.setName(source.getName());
        bean.setDescription(source.getDescription());
        bean.setEnable(source.getEnable());
        bean.setImageId(source.getImageId());
        bean.setDisableImageId(source.getDisableImageId());
        bean.setParentId(source.getParentId());
        bean.setParentValid(source.getParentId()>0);
        bean.setUniqueId(source.getUniqueId());
        bean.setPhoneNumber(source.getPhoneNumber());
        bean.setLongitude(source.getLongitude());
        bean.setLatitude(source.getLatitude());
        bean.setAddressImageId(source.getAddressImageId());
        bean.setAddressLink(source.getAddressLink());
        bean.setAddress(source.getAddress());
        bean.setOutpatientAddress(source.getOutpatientAddress());
        return bean;
    }
}
