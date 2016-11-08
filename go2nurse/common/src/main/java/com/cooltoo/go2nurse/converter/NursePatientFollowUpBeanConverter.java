package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.entities.NursePatientFollowUpEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/7.
 */
@Component
public class NursePatientFollowUpBeanConverter implements Converter<NursePatientFollowUpEntity, NursePatientFollowUpBean> {
    @Override
    public NursePatientFollowUpBean convert(NursePatientFollowUpEntity source) {
        NursePatientFollowUpBean bean = new NursePatientFollowUpBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setNurseId(source.getNurseId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        return bean;
    }
}
