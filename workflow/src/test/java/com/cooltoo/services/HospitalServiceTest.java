package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.HospitalService;
import com.cooltoo.beans.HospitalBean;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Transactional
public class HospitalServiceTest extends AbstractCooltooTest {

    @Autowired
    private HospitalService service;

    @Test
    public void testNew1() {
        HospitalBean bean = new HospitalBean();
        bean.setName("name111");
        bean.setProvince("province111");
        bean.setCity("city111");
        int id = service.newOne(bean);
        Assert.assertTrue(id>0);
        List<HospitalBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml")
    public void testNew2() {
        int id = service.newOne("name111", "province111", "city111");
        Assert.assertTrue(id > 0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml")
    public void testGetAll() {
        List<HospitalBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml")
    public void testGetOne() {
        HospitalBean one = service.getOneById(33);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml")
    public void testDeleteById() {
        HospitalBean bean = service.deleteById(22);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml")
    public void testDeleteByUpdate() {
        HospitalBean bean = service.update(22, "name123", "province123", "city123");
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals("name123", bean.getName());
        Assert.assertEquals("province123", bean.getProvince());
        Assert.assertEquals("city123", bean.getCity());

        bean.setName("name789");
        bean.setProvince("province789");
        bean.setCity("city789");
        bean =  service.update(bean);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals("name789", bean.getName());
        Assert.assertEquals("province789", bean.getProvince());
        Assert.assertEquals("city789", bean.getCity());
    }
}
