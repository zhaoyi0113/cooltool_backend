package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.SkillBean;
import com.cooltoo.constants.OccupationSkillStatus;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.backend.entities.SkillEntity;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.backend.repository.SkillRepository;
import com.cooltoo.backend.services.SkillService;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
})
public class SkillServiceTest extends AbstractCooltooTest {

    @Autowired
    private SkillService skillService;

    @Autowired
    private SkillRepository repository;

    @Autowired
    private OfficialFileStorageService storageRepository;

    @Test
    public void testAddNewOccupation() {
        String skillName = System.currentTimeMillis() + "";
        File file = new File("build/" + System.currentTimeMillis());
        try {
            file.createNewFile();
            SkillBean bean  = skillService.addNewOccupationSkill(skillName, 1, new FileInputStream(file), new FileInputStream(file));
            Assert.assertTrue(bean.getId() > 0);
            Assert.assertEquals(skillName, bean.getName());

            Assert.assertTrue(storageRepository.fileExist(bean.getImageId()));
            Assert.assertTrue(storageRepository.fileExist(bean.getDisableImageId()));
            storageRepository.deleteFile(bean.getImageId());
            storageRepository.deleteFile(bean.getDisableImageId());
            Assert.assertFalse(storageRepository.fileExist(bean.getImageUrl()));
            Assert.assertFalse(storageRepository.fileExist(bean.getDisableImageUrl()));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        file.delete();
    }

    @Test
    public void testEditOccupation() {
        SkillBean skill = skillService.getOneSkillById(1000);
        Assert.assertNotNull(skill);
        String      name    = String.valueOf(System.currentTimeMillis());
        File        file    = new File("build/" + System.currentTimeMillis());
        InputStream image   = null;
        InputStream disable = null;
        try {
            file.createNewFile();
            image   = new FileInputStream(file);
            disable = new FileInputStream(file);
            skillService.editOccupationSkill(skill.getId(), name, 1, OccupationSkillStatus.ENABLE.name(), image, disable);
            SkillBean bean = skillService.getOneSkillById(skill.getId());
            Assert.assertNotNull(bean);
            Assert.assertEquals(name, bean.getName());

            Assert.assertTrue(storageRepository.fileExist(bean.getImageId()));
            Assert.assertTrue(storageRepository.fileExist(bean.getDisableImageId()));
            storageRepository.deleteFile(bean.getImageId());
            storageRepository.deleteFile(bean.getDisableImageId());
            Assert.assertFalse(storageRepository.fileExist(bean.getImageUrl()));
            Assert.assertFalse(storageRepository.fileExist(bean.getDisableImageUrl()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
    }

    @Test
    public void testEditOccupationWithoutImage() {
        SkillBean skill = skillService.getOneSkillById(1000);
        Assert.assertNotNull(skill);
        String name = String.valueOf(System.currentTimeMillis());
        skillService.editOccupationSkillWithoutImage(skill.getId(), name, 1, OccupationSkillStatus.ENABLE.name());
        SkillBean editedSkill = skillService.getOneSkillById(skill.getId());
        Assert.assertNotNull(editedSkill);
        Assert.assertEquals(name, editedSkill.getName());
    }

    @Test
    public void testDeleteOccupation() {
        int number = skillService.getAllSkill().size();
        skillService.deleteOccupationSkill(1000);
        int newNumber = skillService.getAllSkill().size();
        Assert.assertEquals(number - 1, newNumber);
    }
}
