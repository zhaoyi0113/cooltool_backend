package com.cooltoo.converter;

import com.cooltoo.beans.EmploymentInformationBean;
import com.cooltoo.entities.EmploymentInformationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/4/20.
 */
@Component
public class EmploymentInformationBeanConverter implements Converter<EmploymentInformationEntity, EmploymentInformationBean> {
    @Override
    public EmploymentInformationBean convert(EmploymentInformationEntity source) {
        EmploymentInformationBean bean = new EmploymentInformationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setTitle(source.getTitle());
        bean.setFrontCover(source.getFrontCover());
        bean.setUrl(source.getUrl());
        bean.setGrade(source.getGrade());
        return bean;
    }
}
