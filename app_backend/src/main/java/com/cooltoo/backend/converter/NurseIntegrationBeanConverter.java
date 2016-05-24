package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseIntegrationBean;
import com.cooltoo.backend.entities.NurseIntegrationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/5/21.
 */
@Component
public class NurseIntegrationBeanConverter implements Converter<NurseIntegrationEntity, NurseIntegrationBean> {
    @Override
    public NurseIntegrationBean convert(NurseIntegrationEntity source) {
        NurseIntegrationBean bean = new NurseIntegrationBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setUserType(source.getUserType());
        bean.setReasonId(source.getReasonId());
        bean.setAbilityId(source.getAbilityId());
        bean.setAbilityType(source.getAbilityType());
        bean.setPoint(source.getPoint());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
