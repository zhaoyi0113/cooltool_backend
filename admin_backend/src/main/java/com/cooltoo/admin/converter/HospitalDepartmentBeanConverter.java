package com.cooltoo.admin.converter;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.admin.entities.HospitalDepartmentEntity;
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
