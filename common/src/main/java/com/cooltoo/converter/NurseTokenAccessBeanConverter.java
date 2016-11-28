package com.cooltoo.converter;

import com.cooltoo.beans.NurseTokenAccessBean;
import com.cooltoo.entities.NurseTokenAccessEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/28.
 */
@Component
public class NurseTokenAccessBeanConverter implements Converter<NurseTokenAccessEntity, NurseTokenAccessBean> {
    @Override
    public NurseTokenAccessBean convert(NurseTokenAccessEntity source) {
        NurseTokenAccessBean bean = new NurseTokenAccessBean();
        bean.setId(source.getId());
        bean.setUserId(source.getUserId());
        bean.setType(source.getType());
        bean.setToken(source.getToken());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
