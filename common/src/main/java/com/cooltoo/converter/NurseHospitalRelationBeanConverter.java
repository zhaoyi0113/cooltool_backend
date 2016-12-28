package com.cooltoo.converter;

import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.beans.NurseHospitalRelationBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Component
public class NurseHospitalRelationBeanConverter implements Converter<NurseHospitalRelationEntity, NurseHospitalRelationBean> {
    @Override
    public NurseHospitalRelationBean convert(NurseHospitalRelationEntity source) {
        NurseHospitalRelationBean bean = new NurseHospitalRelationBean();
        bean.setId(source.getId());
        bean.setNurseId(source.getNurseId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setApproval(null==source.getApproval() ? YesNoEnum.NONE : source.getApproval());
        bean.setApprovalTime(source.getApprovalTime());
        return bean;
    }
}
