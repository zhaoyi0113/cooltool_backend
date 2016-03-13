package com.cooltoo.converter;

import com.cooltoo.beans.MessageBean;
import com.cooltoo.entities.MessageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/3/7.
 */
@Component
public class MessageEntityConverter implements Converter<MessageBean, MessageEntity> {
    @Override
    public MessageEntity convert(MessageBean source) {
        MessageEntity entity = new MessageEntity();
        entity.setId(source.getId());
        entity.setNurseId(source.getNurseId());
        entity.setMillisecond(source.getMillisecond());
        entity.setContent(source.getContent());
        return null;
    }
}
