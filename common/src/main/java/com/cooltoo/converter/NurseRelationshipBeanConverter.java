package com.cooltoo.converter;

import com.cooltoo.beans.NurseRelationshipBean;
import com.cooltoo.entities.NurseRelationshipEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/5/30.
 */
@Component
public class NurseRelationshipBeanConverter implements Converter<NurseRelationshipEntity, NurseRelationshipBean> {
    @Override
    public NurseRelationshipBean convert(NurseRelationshipEntity source) {
        NurseRelationshipBean bean = new NurseRelationshipBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setRelativeUserId(source.getRelativeUserId());
        bean.setRelationType(source.getRelationType());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
