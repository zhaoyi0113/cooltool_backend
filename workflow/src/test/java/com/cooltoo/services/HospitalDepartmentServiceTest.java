package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.HospitalDepartmentBean;
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
public class HospitalDepartmentServiceTest extends AbstractCooltooTest {

    @Autowired
    private HospitalDepartmentService service;

    @Test
    public void testNew1() {
        int id = service.createHospitalDepartment("name111");
        Assert.assertTrue(id>0);
        List<HospitalDepartmentBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml")
    public void testGetAll() {
        List< HospitalDepartmentBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml")
    public void testGetOne() {
        HospitalDepartmentBean one = service.getOneById(33);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml")
    public void testDeleteById() {
        HospitalDepartmentBean bean = service.deleteById(22);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml")
    public void testDeleteByUpdate() {
        HospitalDepartmentBean bean = service.update(22, "name123");
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals("name123", bean.getName());

        bean.setName("name789");
        bean =  service.update(bean);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals("name789", bean.getName());
    }
}
