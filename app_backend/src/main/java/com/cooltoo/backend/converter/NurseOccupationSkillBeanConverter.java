package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Component
public class NurseOccupationSkillBeanConverter implements Converter<NurseOccupationSkillEntity, NurseOccupationSkillBean> {

    @Override
    public NurseOccupationSkillBean convert(NurseOccupationSkillEntity source) {
        NurseOccupationSkillBean bean = new NurseOccupationSkillBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setSkillId(source.getSkillId());
        bean.setPoint(source.getPoint());
        return bean;
    }
}
