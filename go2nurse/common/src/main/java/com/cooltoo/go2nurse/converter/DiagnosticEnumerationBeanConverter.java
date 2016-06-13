package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/13.
 */
@Component
public class DiagnosticEnumerationBeanConverter implements Converter<DiagnosticEnumeration, DiagnosticEnumerationBean> {
    @Override
    public DiagnosticEnumerationBean convert(DiagnosticEnumeration source) {
        DiagnosticEnumerationBean bean = new DiagnosticEnumerationBean();
        bean.setId(source.ordinal());
        bean.setName(source.name());
        return bean;
    }
}
