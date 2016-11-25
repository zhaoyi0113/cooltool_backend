package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CasebookBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.entities.CasebookEntity;
import com.cooltoo.go2nurse.entities.UserConsultationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/28.
 */
@Component
public class CasebookBeanConverter implements Converter<CasebookEntity, CasebookBean> {
    @Override
    public CasebookBean convert(CasebookEntity source) {
        CasebookBean bean = new CasebookBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setDescription(source.getDescription());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setNurseId(source.getNurseId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        return bean;
    }
}
