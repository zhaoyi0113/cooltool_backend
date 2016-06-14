package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.service.PatientService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/6/14.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_data.xml")
})
public class PatientServiceTest extends AbstractCooltooTest {

    @Autowired private PatientService service;

    @Test
    public void testCreate() {
        String name = "name_1";
        int gender = 1;
        Date birthday = new Date(System.currentTimeMillis());
        String identity = "13414321432143554321432";
        String mobile = "15811663430";

        long id = service.create(name, gender, birthday, identity, mobile);
        PatientBean bean = service.getOneById(id);
        Assert.assertTrue(id>0);
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(gender, bean.getGender().ordinal());
        Assert.assertEquals(birthday, bean.getBirthday());
        Assert.assertEquals(mobile, bean.getMobile());
        Assert.assertEquals(CommonStatus.ENABLED, bean.getStatus());
    }

    @Test
    public void testUpdate() {
        long id = 1;
        String name = "name_1";
        int gender = 2;
        Date birthday = new Date(System.currentTimeMillis());
        String identity = "xxxxxxxxxxxxxxxxxxxxxxxx";
        String mobile = "15811663430";
        String status = CommonStatus.DISABLED.name();

        PatientBean bean = service.update(id, name, gender, birthday, identity, mobile, status);
        Assert.assertNotNull(bean);
        Assert.assertEquals(id, bean.getId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(gender, bean.getGender().ordinal());
        Assert.assertEquals(birthday,  bean.getBirthday());
        Assert.assertEquals(identity,  bean.getIdentityCard());
        Assert.assertEquals(mobile,  bean.getMobile());
        Assert.assertEquals(status,  bean.getStatus().name());
    }

    @Test
    public void testGetAllByStatusAndIds(){
        List<Long> ids = Arrays.asList(new Long[]{6L, 7L, 8L, 9L});
        CommonStatus status = CommonStatus.ENABLED;
        List<PatientBean> beans = service.getAllByStatusAndIds(ids, status);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(6L, beans.get(0).getId());

        status = CommonStatus.DELETED;
        beans = service.getAllByStatusAndIds(ids, status);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(9L, beans.get(0).getId());

        status = CommonStatus.DISABLED;
        beans = service.getAllByStatusAndIds(ids, status);
        Assert.assertEquals(2, beans.size());
        Assert.assertEquals(7L, beans.get(0).getId());
        Assert.assertEquals(8L, beans.get(1).getId());
    }

    @Test
    public void testCountAll() {
        String name = "病人1";
        int gender = 1;
        String identity = "014";
        String mobile = "814";
        String status = CommonStatus.ENABLED.name();

        long count = service.countAll(null, -1, null, null, null);
        Assert.assertEquals(17, count);

        count = service.countAll(name, -1, null, null, null);
        Assert.assertEquals(9, count);

        count = service.countAll(name, gender, null, null, null);
        Assert.assertEquals(5, count);

        count = service.countAll(name, gender, null, null, status);
        Assert.assertEquals(4, count);

        count = service.countAll(name, gender, mobile, null, status);
        Assert.assertEquals(2, count);

        count = service.countAll(name, gender, mobile, identity, status);
        Assert.assertEquals(1, count);
    }
}
