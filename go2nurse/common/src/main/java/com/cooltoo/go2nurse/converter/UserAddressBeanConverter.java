package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.entities.UserAddressEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/2.
 */
@Component
public class UserAddressBeanConverter implements Converter<UserAddressEntity, UserAddressBean> {
    @Override
    public UserAddressBean convert(UserAddressEntity source) {
        UserAddressBean bean = new UserAddressBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setProvinceId(source.getProvinceId());
        bean.setCityId(source.getCityId());
        bean.setAddress(source.getAddress());
        bean.setGrade(source.getGrade());
        bean.setIsDefault(source.getIsDefault());
        return bean;
    }
}
