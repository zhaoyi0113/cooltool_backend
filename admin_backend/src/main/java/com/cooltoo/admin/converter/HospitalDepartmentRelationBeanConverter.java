package com.cooltoo.admin.converter;

import com.cooltoo.beans.HospitalDepartmentRelationBean;
import com.cooltoo.admin.entities.HospitalDepartmentRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class HospitalDepartmentRelationBeanConverter implements Converter<HospitalDepartmentRelationEntity, HospitalDepartmentRelationBean> {
    @Override
    public HospitalDepartmentRelationBean convert(HospitalDepartmentRelationEntity source) {
        HospitalDepartmentRelationBean bean = new HospitalDepartmentRelationBean();
        bean.setId(source.getId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        return bean;
    }
}
