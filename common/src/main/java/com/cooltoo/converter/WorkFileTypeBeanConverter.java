package com.cooltoo.converter;

import com.cooltoo.beans.WorkFileTypeBean;
import com.cooltoo.entities.WorkFileTypeEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Component
public class WorkFileTypeBeanConverter implements Converter<WorkFileTypeEntity, WorkFileTypeBean> {

    @Override
    public WorkFileTypeBean convert(WorkFileTypeEntity source) {
        WorkFileTypeBean bean = new WorkFileTypeBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setType(source.getType());
        bean.setFactor(source.getFactor());
        bean.setMaxFileCount(source.getMaxFileCount());
        bean.setMinFileCount(source.getMinFileCount());
        bean.setImageId(source.getImageId());
        bean.setDisableImageId(source.getDisableImageId());
        return bean;
    }
}
