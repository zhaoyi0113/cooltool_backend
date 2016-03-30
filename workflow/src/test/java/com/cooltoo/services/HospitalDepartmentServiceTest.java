package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.HospitalDepartmentService;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
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
        int id = service.createHospitalDepartment("name111", "department111", -1, -1, null, null);
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
        int    id = 22;
        String name     = "name123";
        String desc     = "description123";
        int    enable   = 1;
        int    parentId = 33;


        HospitalDepartmentBean bean = service.update(id, name, desc, enable, parentId, null, null);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(name, bean.getName());

        name = "name789";
        bean.setName(name);
        bean =  service.update(bean, null, null);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(name, bean.getName());
    }
}
