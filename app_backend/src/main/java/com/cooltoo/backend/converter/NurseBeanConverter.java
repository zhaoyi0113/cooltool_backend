package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Component
public class NurseBeanConverter implements Converter<NurseEntity, NurseBean> {

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

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

        bean.setBackgroundImageUrl(storageService.getFilePath(entity.getBackgroundImageId()));
        bean.setProfilePhotoUrl(storageService.getFilePath(entity.getProfilePhotoId()));
        return bean;
    }
}
