package com.cooltoo.converter;

import com.cooltoo.beans.NurseAuthorizationBean;
import com.cooltoo.entities.NurseAuthorizationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 19/12/2016.
 */
@Component
public class NurseAuthorizationBeanConverter implements Converter<NurseAuthorizationEntity, NurseAuthorizationBean> {

    @Override
    public NurseAuthorizationBean convert(NurseAuthorizationEntity source) {
        NurseAuthorizationBean bean = new NurseAuthorizationBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setDenyAllAuthHeadNurse(source.getDenyAllAuthHeadNurse());
        bean.setAuthOrderHeadNurse(source.getAuthOrderHeadNurse());
        bean.setAuthOrderAdmin(source.getAuthOrderAdmin());
        bean.setAuthNotificationHeadNurse(source.getAuthNotificationHeadNurse());
        bean.setAuthConsultationHeadNurse(source.getAuthConsultationHeadNurse());
        return bean;
    }
}
