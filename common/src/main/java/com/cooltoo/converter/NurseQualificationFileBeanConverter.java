package com.cooltoo.converter;

import com.cooltoo.beans.NurseQualificationFileBean;
import com.cooltoo.entities.NurseQualificationFileEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/4/4.
 */
@Component
public class NurseQualificationFileBeanConverter implements Converter<NurseQualificationFileEntity, NurseQualificationFileBean> {

    @Override
    public NurseQualificationFileBean convert(NurseQualificationFileEntity source) {
        NurseQualificationFileBean bean = new NurseQualificationFileBean();
        bean.setId(source.getId());
        bean.setQualificationId(source.getQualificationId());
        bean.setWorkfileTypeId(source.getWorkfileTypeId());
        bean.setWorkfileId(source.getWorkfileId());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setTimeExpiry(source.getTimeExpiry());
        return bean;
    }
}
