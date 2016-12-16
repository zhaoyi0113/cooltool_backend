package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DenyPatientBean;
import com.cooltoo.go2nurse.entities.DenyPatientEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 14/12/2016.
 */
@Component
public class DenyPatientBeanConverter implements Converter<DenyPatientEntity, DenyPatientBean> {
    @Override
    public DenyPatientBean convert(DenyPatientEntity source) {
        DenyPatientBean bean = new DenyPatientBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setVendorType(source.getVendorType());
        bean.setVendorId(source.getVendorId());
        bean.setDepartId(source.getDepartId());
        bean.setNurseId(source.getNurseId());
        bean.setWhoDenyPatient(source.getWhoDenyPatient());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        return bean;
    }
}
