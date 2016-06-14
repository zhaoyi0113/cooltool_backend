package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.entities.UserDiagnosticPointRelationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/14.
 */
@Component
public class UserDiagnosticPointRelationConverter implements Converter<UserDiagnosticPointRelationEntity, UserDiagnosticPointRelationBean> {
    @Override
    public UserDiagnosticPointRelationBean convert(UserDiagnosticPointRelationEntity source) {
        UserDiagnosticPointRelationBean bean = new UserDiagnosticPointRelationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setDiagnosticId(source.getDiagnosticId());
        bean.setDiagnosticTime(source.getDiagnosticTime());
        return bean;
    }
}
