package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.CathartProfilePhotoBean;
import com.cooltoo.backend.entities.CathartProfilePhotoEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/4/18.
 */
@Component
public class CathartProfilePhotoBeanConverter implements Converter<CathartProfilePhotoEntity, CathartProfilePhotoBean> {
    @Override
    public CathartProfilePhotoBean convert(CathartProfilePhotoEntity source) {
        CathartProfilePhotoBean bean = new CathartProfilePhotoBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setImageId(source.getImageId());
        bean.setEnable(source.getEnable());
        bean.setTimeCreated(source.getTimeCreated());
        return bean;
    }
}
