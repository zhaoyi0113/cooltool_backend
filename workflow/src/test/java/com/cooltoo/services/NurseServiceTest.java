package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.constants.GenderType;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.services.NurseService;
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
public class NurseServiceTest extends AbstractCooltooTest {

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
        bean.setName("name_1");
        bean.setAge(321);
        bean.setGender(GenderType.parseInt(1));
        bean.setMobile("15811663430");
        bean.setPassword("password");
        long id = service.registerNurse(bean);
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
        NurseBean bean = service.updateNurse(1,  "name222222", 22, 2);
        Assert.assertNotNull(bean);
        Assert.assertEquals(1, bean.getId());
        Assert.assertEquals("name222222", bean.getName());
        Assert.assertEquals(22,  bean.getAge());
        Assert.assertEquals(GenderType.SECRET, bean.getGender());
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

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testUpdateRealNameAndIdentification() {
        String realname = "TEST";
        String identifi = "123456198404044561";
        NurseBean bean = service.getNurse(1);
        System.out.println(bean);
        service.setRealNameAndIdentification(bean.getId(), realname, identifi);
        bean = service.getNurse(1);
        System.out.println(bean);
        Assert.assertEquals(realname, bean.getRealName());
        Assert.assertEquals(identifi, bean.getIdentification());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testUpdateMobilePassword() {
        String mobile = "13423143214";
        String password = "12fd3a45b61c9840e40f4f45b6d1c";

        NurseBean bean1 = service.getNurse(1);
        NurseBean bean2 = service.getNurse(1);
        bean2.setId(1);
        bean2.setMobile(mobile);
        bean2.setPassword(password);
        bean2 = service.updateMobilePassword(bean2);

        Assert.assertEquals(mobile, bean2.getMobile());
        Assert.assertEquals(password, bean2.getPassword());
        Assert.assertEquals(1, bean2.getId());

        Assert.assertNotEquals(bean1.getMobile(), bean2.getMobile());
        Assert.assertNotEquals(bean1.getPassword(), bean2.getPassword());
        Assert.assertEquals(bean1.getId(), bean2.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
    public void testNurseShortNote() {
        String shortNote = "Haahahahahahahha";

        NurseBean bean1 = service.getNurse(1);
        NurseBean bean2 = service.getNurse(1);
        bean2.setId(bean1.getId());
        bean2.setShortNote(shortNote);
        bean2 = service.setShortNote(bean2.getId(), bean2.getShortNote());

        Assert.assertEquals(shortNote, bean2.getShortNote());
        Assert.assertEquals(bean1.getId(), bean2.getId());
    }
}
