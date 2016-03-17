package com.cooltoo.backend.converter;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 3/10/16.
 */
@Component
public class NurseFriendBeanConverter implements Converter<NurseFriendsEntity,NurseFriendsBean> {

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public NurseFriendsBean convert(NurseFriendsEntity source) {
        NurseFriendsBean bean = new NurseFriendsBean();
        bean.setFriendId(source.getFriendId());
        bean.setUserId(source.getUserId());
        bean.setId(source.getId());
        bean.setDateTime(source.getDateTime());
        NurseEntity friend = nurseRepository.findOne(source.getFriendId());
        bean.setFriendName(friend.getName());
        bean.setHeadPhotoUrl(storageService.getFileUrl(friend.getProfilePhotoId()));
        return bean;
    }
}
