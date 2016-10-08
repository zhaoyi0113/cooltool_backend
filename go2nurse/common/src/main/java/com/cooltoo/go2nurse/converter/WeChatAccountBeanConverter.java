package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Component
public class WeChatAccountBeanConverter implements Converter<WeChatAccountEntity, WeChatAccountBean> {
    @Override
    public WeChatAccountBean convert(WeChatAccountEntity source) {
        WeChatAccountBean bean = new WeChatAccountBean();
        bean.setId(source.getId());
        bean.setTimeCreated(source.getTimeCreated());
        bean.setStatus(source.getStatus());
        bean.setAppId(source.getAppId());
        bean.setAppSecret(source.getAppSecret());
        bean.setMchId(source.getMchId());
        bean.setName(source.getName());
        return bean;
    }
}
