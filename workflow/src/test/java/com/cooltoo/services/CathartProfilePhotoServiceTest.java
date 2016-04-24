package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.CathartProfilePhotoBean;
import com.cooltoo.backend.services.CathartProfilePhotoService;
import com.cooltoo.constants.CommonStatus;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hp on 2016/4/19.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/cathart_profile_photo_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
})
public class CathartProfilePhotoServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(CathartProfilePhotoServiceTest.class.getName());

    @Autowired
    private CathartProfilePhotoService cathartPhotoService;

    @Test
    public void testCountByStatus() {
        long enableCount  = cathartPhotoService.countByStatus("enabled");
        long disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(4, enableCount);
        Assert.assertEquals(2, disableCount);
    }

    @Test
    public void testGetMapByStatus() {
        Map<Long, CathartProfilePhotoBean> map = cathartPhotoService.getMapByStatus("enabled");
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Set<Long> keySet = map.keySet();
        for (Long key : keySet) {
            CathartProfilePhotoBean bean = map.get(key);
            Assert.assertEquals(bean.getId(), key.longValue());
        }
    }

    @Test
    public void testGetAllByStatus() {
        List<CathartProfilePhotoBean> list = cathartPhotoService.getAllByStatus("enabled");
        Assert.assertNotNull(list);
        Assert.assertEquals(4, list.size());

        list = cathartPhotoService.getAllByStatus("disabled");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());

        list = cathartPhotoService.getAllByStatus("all");
        Assert.assertNotNull(list);
        Assert.assertEquals(6, list.size());
    }

    @Test
    public void testGetOne() {
        CathartProfilePhotoBean one = cathartPhotoService.getOne(1);
        Assert.assertNotNull(one);
        Assert.assertEquals(1, one.getId());
    }

    @Test
    public void testCreateCathartProfilePhoto() {
        String name   = "匿名 7";
        String enable = CommonStatus.ENABLED.name();
        String imageName = "aaa.png";
        byte[] imageByte = imageName.getBytes();
        ByteArrayInputStream image = new ByteArrayInputStream(imageByte);
        CathartProfilePhotoBean newOne = cathartPhotoService.createCathartProfilePhoto(name, imageName, image, enable);
        Assert.assertNotNull(newOne);
        Assert.assertTrue(newOne.getId()>0);
        Assert.assertEquals(name, newOne.getName());
        Assert.assertEquals(CommonStatus.ENABLED, newOne.getEnable());
        Assert.assertTrue(newOne.getImageId()>0);
        Assert.assertNotNull(newOne.getImageUrl());
    }

    @Test
    public void testUpdateCathartProfilePhoto() {
        CathartProfilePhotoBean oldOne = cathartPhotoService.getOne(1);
        CathartProfilePhotoBean modify = cathartPhotoService.getOne(1);

        modify.setName("匿名 test");
        CathartProfilePhotoBean newOne = cathartPhotoService.updateCathartProfilePhoto(modify.getId(), modify.getName(), null, null, null);
        Assert.assertNotNull(newOne);
        Assert.assertEquals(newOne.getId(), modify.getId());
        Assert.assertEquals(newOne.getName(), modify.getName());
        Assert.assertNotEquals(newOne.getName(), oldOne.getName());
        Assert.assertEquals(newOne.getEnable(), modify.getEnable());
        Assert.assertEquals(newOne.getImageId(), modify.getImageId());
        Assert.assertNotEquals(newOne.getTimeCreated(), modify.getTimeCreated());

        modify.setEnable(CommonStatus.DISABLED);
        newOne = cathartPhotoService.updateCathartProfilePhoto(modify.getId(), null, null, null, modify.getEnable().name());
        Assert.assertNotNull(newOne);
        Assert.assertEquals(newOne.getId(), modify.getId());
        Assert.assertEquals(newOne.getName(), modify.getName());
        Assert.assertEquals(newOne.getEnable(), modify.getEnable());
        Assert.assertNotEquals(newOne.getEnable(), oldOne.getEnable());
        Assert.assertEquals(newOne.getImageId(), modify.getImageId());
        Assert.assertNotEquals(newOne.getTimeCreated(), modify.getTimeCreated());

        String imageName = "aaa.png";
        byte[] imageByte = imageName.getBytes();
        ByteArrayInputStream image = new ByteArrayInputStream(imageByte);
        newOne = cathartPhotoService.updateCathartProfilePhoto(modify.getId(), null, imageName, image, null);
        Assert.assertNotNull(newOne);
        Assert.assertEquals(newOne.getId(), modify.getId());
        Assert.assertEquals(newOne.getName(), modify.getName());
        Assert.assertEquals(newOne.getEnable(), modify.getEnable());
        Assert.assertTrue(newOne.getImageId()>0);
        Assert.assertNotEquals(newOne.getImageId(), modify.getImageId());
        Assert.assertNotNull(newOne.getImageUrl());
        Assert.assertNotEquals(newOne.getImageUrl(), modify.getImageUrl());
        Assert.assertNotEquals(newOne.getTimeCreated(), modify.getTimeCreated());
    }

    @Test
    public void testDisableByIds() {
        long enableCount  = cathartPhotoService.countByStatus("enabled");
        long disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(4, enableCount);
        Assert.assertEquals(2, disableCount);

        cathartPhotoService.setStatusByIds("1,2");
        enableCount  = cathartPhotoService.countByStatus("enabled");
        disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(2, enableCount);
        Assert.assertEquals(4, disableCount);

        List<Long> disableIds = new ArrayList<>();
        disableIds.add(3L); disableIds.add(4L);
        cathartPhotoService.setStatusByIds(disableIds);
        enableCount  = cathartPhotoService.countByStatus("enabled");
        disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(0, enableCount);
        Assert.assertEquals(6, disableCount);
    }

    @Test
    public void testDeleteByIds() {
        long enableCount  = cathartPhotoService.countByStatus("enabled");
        long disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(4, enableCount);
        Assert.assertEquals(2, disableCount);

        cathartPhotoService.deleteByIds("1,2");
        enableCount  = cathartPhotoService.countByStatus("enabled");
        disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(2, enableCount);
        Assert.assertEquals(2, disableCount);

        List<Long> deleteIds = new ArrayList<>();
        deleteIds.add(3L); deleteIds.add(4L);
        cathartPhotoService.deleteByIds(deleteIds);
        enableCount  = cathartPhotoService.countByStatus("enabled");
        disableCount = cathartPhotoService.countByStatus("disabled");
        Assert.assertEquals(0, enableCount);
        Assert.assertEquals(2, disableCount);
    }
}
