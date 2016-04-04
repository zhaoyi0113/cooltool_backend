package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 3/10/16.
 */
@Component
public class NurseFriendBeanConverter implements Converter<NurseFriendsEntity,NurseFriendsBean> {

    @Override
    public NurseFriendsBean convert(NurseFriendsEntity source) {
        NurseFriendsBean bean = new NurseFriendsBean();
        bean.setFriendId(source.getFriendId());
        bean.setUserId(source.getUserId());
        bean.setId(source.getId());
        bean.setDateTime(source.getDateTime());
        bean.setIsAgreed(source.getIsAgreed());
        return bean;
    }
}
