package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by Test111 on 2016/3/18.
 */
@Component
public class NurseSpeakCommentEntityConverter implements Converter<NurseSpeakCommentBean, NurseSpeakCommentEntity> {
    @Override
    public NurseSpeakCommentEntity convert(NurseSpeakCommentBean source) {
        NurseSpeakCommentEntity entity = new NurseSpeakCommentEntity();
        entity.setId(source.getId());
        entity.setNurseSpeakId(source.getNurseSpeakId());
        entity.setCommentMakerId(source.getCommentMakerId());
        entity.setCommentReceiverId(source.getCommentReceiverId());
        entity.setComment(source.getComment());
        entity.setTime(source.getTime());
        return entity;
    }
}
