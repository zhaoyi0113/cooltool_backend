package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.service.UserAddressService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hp on 2016/7/2.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/user_address_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/region_data.xml")
})
public class UserAddressServiceTest extends AbstractCooltooTest {

    @Autowired private UserAddressService service;

    @Test
    public void testCreateAddress() {
        long userId = 9;
        int provinceId = 2;
        int cityId = 3;
        String address = "address test";
        int grade = 8;
        UserAddressBean bean = service.createAddress(userId, provinceId, cityId, grade, address);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(provinceId, bean.getProvinceId());
        Assert.assertEquals(cityId, bean.getCityId());
        Assert.assertEquals(address, bean.getAddress());
        Assert.assertEquals(grade, bean.getGrade());
    }

    @Test
    public void testGetAll() {
        long userId = 3;
        List<UserAddressBean> beans = service.getUserAddress(userId);
        Assert.assertEquals(3, beans.size());
        userId = 2;
        beans = service.getUserAddress(userId);
        Assert.assertEquals(2, beans.size());
    }

    @Test
    public void testDeleteById() {
        long userId = 1;
        List<UserAddressBean> beans = service.getUserAddress(userId);
        Assert.assertEquals(1, beans.size());

        long addressId = 1;
        service.deleteById(addressId);

        beans = service.getUserAddress(userId);
        Assert.assertEquals(0, beans.size());
    }

    @Test
    public void testDeleteByIds() {
        long userId = 3;
        List<UserAddressBean> beans = service.getUserAddress(userId);
        Assert.assertEquals(3, beans.size());

        List<Long> addressesIds = Arrays.asList(new Long[]{4L, 5L});
        service.deleteByIds(addressesIds);

        beans = service.getUserAddress(userId);
        Assert.assertEquals(1, beans.size());
    }

    @Test
    public void testUpdate() {
        long addressId = 1;
        int provinceId = 6;
        int cityId = 7;
        int grade = 8;
        String address = "address test test";
        CommonStatus status = CommonStatus.DELETED;
        UserAddressBean bean = service.update(addressId, provinceId, cityId, grade, address, status);
        Assert.assertEquals(addressId, bean.getId());
        Assert.assertEquals(provinceId, bean.getProvinceId());
        Assert.assertEquals(cityId, bean.getCityId());
        Assert.assertEquals(grade, bean.getGrade());
        Assert.assertEquals(address, bean.getAddress());
        Assert.assertEquals(status, bean.getStatus());
    }


}
