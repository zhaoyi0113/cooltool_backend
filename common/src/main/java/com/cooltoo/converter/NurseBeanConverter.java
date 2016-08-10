package com.cooltoo.converter;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.entities.NurseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Component
public class NurseBeanConverter implements Converter<NurseEntity, NurseBean> {

    @Override
    public NurseBean convert(NurseEntity entity) {
        NurseBean bean = new NurseBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setGender(entity.getGender());
        bean.setAge(entity.getAge());
        bean.setMobile(entity.getMobile());
        bean.setPassword(entity.getPassword());
        bean.setIntegral(entity.getIntegral());
        bean.setRealName(entity.getRealName());
        bean.setIdentification(entity.getIdentification());
        bean.setShortNote(entity.getShortNote());
        bean.setAuthority(entity.getAuthority());
        bean.setProfilePhotoId(entity.getProfilePhotoId());
        bean.setBackgroundImageId(entity.getBackgroundImageId());
        return bean;
    }
}
