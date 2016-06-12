package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CourseDiagnosticRelationBean;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/12.
 */
@Component
public class CourseDiagnosticRelationBeanConverter implements Converter<CourseDiagnosticRelationEntity, CourseDiagnosticRelationBean> {
    @Override
    public CourseDiagnosticRelationBean convert(CourseDiagnosticRelationEntity source) {
        CourseDiagnosticRelationBean bean = new CourseDiagnosticRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setCourseId(source.getCourseId());
        bean.setDiagnosticId(source.getDiagnosticId());
        return bean;
    }
}
