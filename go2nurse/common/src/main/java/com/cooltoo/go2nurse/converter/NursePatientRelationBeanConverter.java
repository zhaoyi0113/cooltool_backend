package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NursePatientRelationBean;
import com.cooltoo.go2nurse.entities.NursePatientRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Component
public class NursePatientRelationBeanConverter implements Converter<NursePatientRelationEntity, NursePatientRelationBean> {

    @Override
    public NursePatientRelationBean convert(NursePatientRelationEntity source) {
        NursePatientRelationBean bean = new NursePatientRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        return bean;
    }
}
