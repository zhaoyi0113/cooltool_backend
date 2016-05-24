package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseMessageBean;
import com.cooltoo.backend.entities.NurseMessageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/5/21.
 */
@Component
public class NurseMessageBeanConverter implements Converter<NurseMessageEntity, NurseMessageBean> {
    @Override
    public NurseMessageBean convert(NurseMessageEntity source) {
        NurseMessageBean bean = new NurseMessageBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setUserType(source.getUserType());
        bean.setReasonId(source.getReasonId());
        bean.setAbilityId(source.getAbilityId());
        bean.setAbilityType(source.getAbilityType());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
