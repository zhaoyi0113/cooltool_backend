package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserCurrentVisitBean;
import com.cooltoo.go2nurse.entities.UserCurrentVisitEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/12/6.
 */
@Component
public class UserCurrentVisitBeanConverter implements Converter<UserCurrentVisitEntity, UserCurrentVisitBean> {

    @Override
    public UserCurrentVisitBean convert(UserCurrentVisitEntity source) {
        UserCurrentVisitBean bean = new UserCurrentVisitBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setDiagnosticPoint(source.getDiagnosticPoint());
        return bean;
    }
}
