package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Component
public class NurseBeanConverter implements Converter<NurseEntity, NurseBean> {

    @Autowired
    private FileStorageRepository fileStorageRepository;


    @Value("${storage.url}")
    private String storageUrl;

    @Override
    public NurseBean convert(NurseEntity entity) {
        NurseBean bean = new NurseBean();
        bean.setId(entity.getId());
        bean.setIdentificationId(entity.getIdentificationId());
        bean.setName(entity.getName());
        bean.setGender(entity.getGender());
        bean.setAge(entity.getAge());
        bean.setMobile(entity.getMobile());
        bean.setPassword(entity.getPassword());
        bean.setIntegral(entity.getIntegral());

        FileStorageEntity storageEntity = fileStorageRepository.findOne(entity.getBackgroundImageId());
        if(storageEntity != null) {
            String bgImgUrl = storageUrl + storageEntity.getId();
            FileStorageEntity photoEntity = fileStorageRepository.findOne(entity.getProfilePhotoId());
            String photoUrl = storageUrl + photoEntity.getId();
            bean.setProfilePhotoUrl(photoUrl);
            bean.setBackgroundImageUrl(bgImgUrl);
        }
        return bean;
    }
}
