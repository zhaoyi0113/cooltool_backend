package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 4/8/16.
 */
@Component
public class NurseSkillNominationBeanConverter implements Converter<NurseSkillNominationEntity, NurseSkillNominationBean> {
    @Override
    public NurseSkillNominationBean convert(NurseSkillNominationEntity source) {
        NurseSkillNominationBean bean = new NurseSkillNominationBean();
        bean.setSkillId(source.getSkillId());
        bean.setUserId(source.getUserId());
        bean.setSkillType(source.getSkillType());
        return bean;
    }
}
