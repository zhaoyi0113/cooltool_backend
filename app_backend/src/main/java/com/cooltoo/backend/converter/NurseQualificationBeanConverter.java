package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.entities.NurseQualificationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Component
public class NurseQualificationBeanConverter implements Converter<NurseQualificationEntity, NurseQualificationBean> {

    @Override
    public NurseQualificationBean convert(NurseQualificationEntity source) {
        NurseQualificationBean bean = new NurseQualificationBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setName(source.getName());
        bean.setWorkFileType(source.getWorkFileType());
        bean.setWorkFileId(source.getWorkFileId());
        bean.setStatus(source.getStatus());
        bean.setStatusDescr(source.getStatusDesc());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;

    }
}
