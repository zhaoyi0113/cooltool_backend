package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.HospitalBean;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/region_data.xml")
})
public class HospitalServiceTest extends AbstractCooltooTest {

    @Autowired
    private CommonHospitalService service;

    @Test
    public void testNew1() {
        HospitalBean bean = new HospitalBean();
        bean.setName("name111");
        bean.setProvince(2);
        bean.setCity(33);
        bean.setDistrict(382);
        int id = service.newOne(bean);
        Assert.assertTrue(id>0);
        List<HospitalBean> all = service.getHospitalByIds(Arrays.asList(new Integer[]{id}));
        Assert.assertEquals(1, all.size());
        Assert.assertNotNull(all.get(0).getUniqueId());
    }

    @Test
    public void testNew2() {
        int id = service.newOne("name111", "aliasName111", 2, 33, 382, null, -1, -1, null, null);
        Assert.assertTrue(id > 0);
        List<HospitalBean> all = service.getHospitalByIds(Arrays.asList(new Integer[]{id}));
        Assert.assertEquals(1, all.size());
        Assert.assertNotNull(all.get(0).getUniqueId());
    }

    @Test
    public void testGetOne() {
        HospitalBean one = service.getOneById(33);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    public void testGetHospitalByUniqueId() {
        String uniqueId = "111111";
        List<HospitalBean> beans = service.getHospitalByUniqueId(uniqueId);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(11, beans.get(0).getId());
    }

    @Test
    public void testDeleteById() {
        HospitalBean bean = service.deleteById(22);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    public void testDeleteByUpdate() {
        HospitalBean bean = service.update(22, "name123", "aliasName111", 2, 3, -1, null, -1, -1, null, null);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals("name123", bean.getName());
        Assert.assertEquals("aliasName111", bean.getAliasName());
        Assert.assertEquals(2, bean.getProvince());
        Assert.assertEquals(3, bean.getCity());

        bean.setName("name789");
        bean.setAliasName("aliasName789");
        bean.setCity(4);
        bean =  service.update(bean);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals("aliasName789", bean.getAliasName());
        Assert.assertEquals("name789", bean.getName());
        Assert.assertEquals(4, bean.getCity());
    }

    @Test
    public void testSearch() {
        List<HospitalBean> hospitals = service.searchHospitalByConditions(true, null, null, null, null, null, null, 1, 0, 5);
        Assert.assertEquals(3, hospitals.size());
        Assert.assertEquals(11, hospitals.get(0).getId());
        Assert.assertEquals(22, hospitals.get(1).getId());
        Assert.assertEquals(33, hospitals.get(2).getId());

        hospitals = service.searchHospitalByConditions(true, null, null, null, null, null, null, 0, 0, 5);
        Assert.assertEquals(2, hospitals.size());
        Assert.assertEquals(44, hospitals.get(0).getId());
        Assert.assertEquals(55, hospitals.get(1).getId());
    }
}
