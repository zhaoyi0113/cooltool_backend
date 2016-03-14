package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.MessageBean;
import com.cooltoo.backend.entities.MessageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/7.
 */
@Component
public class MessageBeanConverter implements Converter<MessageEntity, MessageBean> {
    @Override
    public MessageBean convert(MessageEntity source) {
        MessageBean bean = new MessageBean();
        bean.setId(source.getId());
        bean.setNurseId(source.getNurseId());
        bean.setMillisecond(source.getMillisecond());
        bean.setContent(source.getContent());
        return bean;
    }
}
