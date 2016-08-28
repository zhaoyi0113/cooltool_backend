package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserConsultationTalkBean;
import com.cooltoo.go2nurse.entities.UserConsultationTalkEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/28.
 */
@Component
public class UserConsultationTalkBeanConverter implements Converter<UserConsultationTalkEntity, UserConsultationTalkBean> {
    @Override
    public UserConsultationTalkBean convert(UserConsultationTalkEntity source) {
        UserConsultationTalkBean bean = new UserConsultationTalkBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setConsultationId(source.getConsultationId());
        bean.setNurseId(source.getNurseId());
        bean.setTalkStatus(source.getTalkStatus());
        bean.setTalkContent(source.getTalkContent());
        return bean;
    }
}
