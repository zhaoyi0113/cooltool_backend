package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.entities.ConsultationCategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/22.
 */
@Component
public class ConsultationCategoryBeanConverter implements Converter<ConsultationCategoryEntity, ConsultationCategoryBean> {
    @Override
    public ConsultationCategoryBean convert(ConsultationCategoryEntity source) {
        ConsultationCategoryBean bean = new ConsultationCategoryBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setDescription(source.getDescription());
        bean.setImageId(source.getImageId());
        bean.setIconId(source.getIconId());
        bean.setOrder(source.getOrderIndex());
        return bean;
    }
}
