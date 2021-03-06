package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.util.NumberUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Created by hp on 2016/6/13.
 */
@Component
public class UserBeanConverter implements Converter<UserEntity, UserBean> {
    @Override
    public UserBean convert(UserEntity source) {
        UserBean bean = new UserBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setGender(source.getGender());
        bean.setBirthday(source.getBirthday());
        bean.setProfilePhoto(source.getProfilePhoto());
        bean.setMobile(source.getMobile());
        bean.setPassword(source.getPassword());
        bean.setAuthority(source.getAuthority());
        bean.setType(source.getType());
        bean.setUniqueId(source.getUniqueId());
        bean.setAddress(source.getAddress());
        bean.setHasDecide(source.getHasDecide());
        bean.setAge(NumberUtil.getAge(bean.getBirthday()));
        if(source.getChannel() != null) {
            bean.setChannel(source.getChannel().ordinal());
        }
        return bean;
    }
}
