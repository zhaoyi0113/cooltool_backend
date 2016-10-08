package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NurseOrderRelationBean;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Component
public class NurseOrderRelationBeanConverter implements Converter<NurseOrderRelationEntity, NurseOrderRelationBean> {

    @Override
    public NurseOrderRelationBean convert(NurseOrderRelationEntity source) {
        NurseOrderRelationBean bean = new NurseOrderRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setOrderId(source.getOrderId());
        return bean;
    }
}
