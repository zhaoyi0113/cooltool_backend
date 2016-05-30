package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by Test111 on 2016/3/18.
 */
@Component
public class NurseSpeakCommentBeanConverter implements Converter <NurseSpeakCommentEntity, NurseSpeakCommentBean> {
    @Override
    public NurseSpeakCommentBean convert(NurseSpeakCommentEntity source) {
        NurseSpeakCommentBean bean = new NurseSpeakCommentBean();
        bean.setId(source.getId());
        bean.setNurseSpeakId(source.getNurseSpeakId());
        bean.setCommentMakerId(source.getCommentMakerId());
        bean.setCommentReceiverId(source.getCommentReceiverId());
        bean.setComment(source.getComment());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        return bean;
    }
}
