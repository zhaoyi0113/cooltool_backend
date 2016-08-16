package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.VideoInSpeakBean;
import com.cooltoo.backend.entities.VideoInSpeakEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/23.
 */
@Component
public class VideoInSpeakBeanConverter implements Converter<VideoInSpeakEntity, VideoInSpeakBean>{

    @Override
    public VideoInSpeakBean convert(VideoInSpeakEntity source) {
        VideoInSpeakBean bean = new VideoInSpeakBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setSpeakId(source.getSpeakId());
        bean.setVideoId(source.getVideoId());
        bean.setSnapshot(source.getSnapshot());
        bean.setBackground(source.getBackground());
        bean.setVideoStatus(source.getVideoStatus());
        bean.setPlatform(source.getPlatform());
        return bean;
    }
}
