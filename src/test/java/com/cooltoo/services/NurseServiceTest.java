package com.cooltoo.services;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.serivces.NurseService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Transactional
public class NurseServiceTest extends AbstractCooltooTest{

    @Autowired
    private NurseService service;

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testNew() {
        NurseBean bean = new NurseBean();
        bean.setIdentificationId("432154654643123");
        bean.setName("name_1");
        bean.setAge(321);
        bean.setGender(1);
        bean.setMobile("15811663434");
        long id = service.newNurse(bean);
        Assert.assertTrue(id>0);
        List<NurseBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testGetAll(){
        List<NurseBean> all = service.getAll();
        Assert.assertEquals(8, all.size());
    }

    @Test
    @DatabaseSetup(value="classpath:/com/cooltoo/services/nurse_data.xml")
    public void testDelete() {
        NurseBean bean = service.deleteNurse(5);
        Assert.assertNotNull(bean);
        Assert.assertEquals(5, bean.getId());
        BadRequestException ex = null;
        try {
            bean = service.getNurse(5);
        }catch(BadRequestException e){
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testUpdate(){
        NurseBean bean = service.updateNurse(1, null, "name222222", 22, 2, "4321654312");
        Assert.assertNotNull(bean);
        Assert.assertEquals(1, bean.getId());
        Assert.assertEquals("name222222", bean.getName());
        Assert.assertEquals(22,  bean.getAge());
        Assert.assertEquals(2, bean.getGender());
        Assert.assertNotEquals("4321654312", bean.getMobile());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testAddPhoto(){
        String filePath = "build/"+System.currentTimeMillis();
        try {
            File file = new File(filePath);
            file.createNewFile();
            InputStream input = new FileInputStream(filePath);
            service.addHeadPhoto(1, file.getName(), input);
            NurseEntity nurse = nurseRepository.findOne(1l);
            FileStorageEntity fileStorage = fileStorageRepository.findOne(nurse.getProfilePhotoId());
            Assert.assertNotNull(fileStorage);
            Assert.assertEquals(file.getName(), fileStorage.getFileRealname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testAddBackgroundImage(){
        String filePath = "build/"+System.currentTimeMillis();
        try {
            File file = new File(filePath);
            file.createNewFile();
            InputStream input = new FileInputStream(filePath);
            service.addBackgroundImage(1, file.getName(), input);
            NurseEntity nurse = nurseRepository.findOne(1l);
            FileStorageEntity fileStorage = fileStorageRepository.findOne(nurse.getBackgroundImageId());
            Assert.assertNotNull(fileStorage);
            Assert.assertEquals(file.getName(), fileStorage.getFileRealname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
