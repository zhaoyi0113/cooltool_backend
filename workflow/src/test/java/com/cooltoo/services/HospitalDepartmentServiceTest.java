package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/hospital_department_data.xml")
})
public class HospitalDepartmentServiceTest extends AbstractCooltooTest {

    @Autowired
    private CommonDepartmentService service;
    @Autowired
    private OfficialFileStorageService officialStorage;

    @Test
    public void testNew1() {
        int id = service.createHospitalDepartment(11, "name111", "department111", -1, -1, null, null, null, null, null);
        Assert.assertTrue(id>0);
        List<HospitalDepartmentBean> all = service.getByIds(Arrays.asList(new Integer[]{id}), "");
        Assert.assertEquals(1, all.size());
        Assert.assertNotNull(all.get(0).getUniqueId());
    }

    @Test
    public void testGetOne() {
        HospitalDepartmentBean one = service.getById(33, "");
        Assert.assertEquals(33, one.getId());
    }

    @Test
    public void testGetDepartmentByUniqueId() {
        String uniqueId = "111111";
        List<HospitalDepartmentBean> beans = service.getDepartmentByUniqueId(uniqueId, "");
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(11, beans.get(0).getId());
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
                                                     new ByteArrayInputStream(desc.getBytes()),
                                                     null, null, null, "");
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
        bean =  service.update(bean, null, null, "");
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(name, bean.getName());
    }
}
