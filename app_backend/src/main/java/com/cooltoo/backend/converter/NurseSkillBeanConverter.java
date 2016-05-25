package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSkillBean;
import com.cooltoo.backend.entities.NurseSkillEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Component
public class NurseSkillBeanConverter implements Converter<NurseSkillEntity, NurseSkillBean> {

    @Override
    public NurseSkillBean convert(NurseSkillEntity source) {
        NurseSkillBean bean = new NurseSkillBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setSkillId(source.getSkillId());
        bean.setPoint(source.getPoint());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
