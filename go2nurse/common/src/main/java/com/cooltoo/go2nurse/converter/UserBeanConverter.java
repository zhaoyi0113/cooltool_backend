package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.entities.UserEntity;
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
        if(source.getChannel() != null) {
            bean.setChannel(source.getChannel().ordinal());
        }
        if (null==bean.getBirthday()) {
            bean.setAge(0);
        }
        else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);

            calendar.setTime(source.getBirthday());
            int birth = calendar.get(Calendar.YEAR);

            bean.setAge((year - birth < 0) ? 0 : (year - birth));
        }
        return bean;
    }
}
