package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Component
public class NurseOccupationSkillBeanConverter implements Converter<NurseOccupationSkillEntity, SocialAbilitiesBean> {

    @Override
    public SocialAbilitiesBean convert(NurseOccupationSkillEntity source) {
        SocialAbilitiesBean bean = new SocialAbilitiesBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setSkillId(source.getSkillId());
        bean.setPoint(source.getPoint());
        return bean;
    }
}
