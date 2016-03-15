package com.cooltoo.backend.converter;

import com.cooltoo.backend.entities.HospitalDepartmentEntity;
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
        bean.setName(source.getName());
        return bean;
    }
}
