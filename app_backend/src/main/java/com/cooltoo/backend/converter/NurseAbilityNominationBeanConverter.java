package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseAbilityNominationBean;
import com.cooltoo.backend.entities.NurseAbilityNominationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 4/8/16.
 */
@Component
public class NurseAbilityNominationBeanConverter implements Converter<NurseAbilityNominationEntity, NurseAbilityNominationBean> {
    @Override
    public NurseAbilityNominationBean convert(NurseAbilityNominationEntity source) {
        NurseAbilityNominationBean bean = new NurseAbilityNominationBean();
        bean.setAbilityId(source.getAbilityId());
        bean.setUserId(source.getUserId());
        bean.setAbilityType(source.getAbilityType());
        return bean;
    }
}
