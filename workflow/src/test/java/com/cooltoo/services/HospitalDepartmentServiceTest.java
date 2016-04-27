package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.HospitalDepartmentService;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/hospital_department_data.xml")
})
public class HospitalDepartmentServiceTest extends AbstractCooltooTest {

    @Autowired
    private HospitalDepartmentService service;
    @Autowired
    private OfficialFileStorageService officialStorage;

    @Test
    public void testNew1() {
        int id = service.createHospitalDepartment("name111", "department111", -1, -1, null, null);
        Assert.assertTrue(id>0);
        List<HospitalDepartmentBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    public void testGetAll() {
        List< HospitalDepartmentBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    public void testGetOne() {
        HospitalDepartmentBean one = service.getOneById(33);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    public void testDeleteById() {
        HospitalDepartmentBean bean = service.deleteById(22);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    public void testDeleteByUpdate() {
        int    id = 22;
        String name     = "name123";
        String desc     = "description123";
        int    enable   = 1;
        int    parentId = 33;


        HospitalDepartmentBean bean = service.update(id, name, desc, enable, parentId,
                                                     new ByteArrayInputStream(desc.getBytes()),
                                                     new ByteArrayInputStream(desc.getBytes()));
        Assert.assertEquals(id, bean.getId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(parentId, bean.getParentId());
        Assert.assertEquals(enable, bean.getEnable());
        Assert.assertEquals(desc, bean.getDescription());
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        Assert.assertTrue(officialStorage.fileExist(bean.getDisableImageId()));
        officialStorage.deleteFile(bean.getImageId());
        officialStorage.deleteFile(bean.getDisableImageId());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageUrl()));
        Assert.assertFalse(officialStorage.fileExist(bean.getDisableImageUrl()));


        name = "name789";
        bean.setName(name);
        bean =  service.update(bean, null, null);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(name, bean.getName());
    }
}
