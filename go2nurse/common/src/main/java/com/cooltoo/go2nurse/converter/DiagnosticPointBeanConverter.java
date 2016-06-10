package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DiagnosticPointBean;
import com.cooltoo.go2nurse.entities.DiagnosticPointEntity;
import com.cooltoo.go2nurse.repository.DiagnosticPointRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 6/10/16.
 */
@Component
public class DiagnosticPointBeanConverter implements Converter<DiagnosticPointEntity, DiagnosticPointBean> {
    @Override
    public DiagnosticPointBean convert(DiagnosticPointEntity source) {
        DiagnosticPointBean bean = new DiagnosticPointBean();
        bean.setStatus(source.getStatus());
        bean.setId(source.getId());
        bean.setImageId(source.getImageId());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setName(source.getName());
        return bean;
    }
}
