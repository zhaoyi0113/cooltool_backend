package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.OccupationSkillBean;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.entities.OccupationSkillEntity;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.repository.OccupationSkillRepository;
import com.cooltoo.serivces.OccupationSkillService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
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
public class OccupationSkillServiceTest extends AbstractCooltooTest {

    @Autowired
    private OccupationSkillService skillService;

    @Autowired
    private OccupationSkillRepository repository;

    @Autowired
    private FileStorageRepository storageRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
    public void testAddNewOccupation() {
        String skillName = System.currentTimeMillis() + "";
        File file = new File("build/" + System.currentTimeMillis());
        try {
            file.createNewFile();
            skillService.addNewOccupationSkill(skillName, new FileInputStream(file));
            List<OccupationSkillEntity> skillList = repository.findByName(skillName);
            Assert.assertTrue(skillList.size() > 0);
            Assert.assertEquals(skillName, skillList.get(0).getName());
            FileStorageEntity storageEntity = storageRepository.findOne(skillList.get(0).getImageId());
            Assert.assertNotNull(storageEntity);
            Assert.assertEquals(skillName, storageEntity.getFileRealname());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
    public void testEditOccupation() {
        OccupationSkillBean skill = skillService.getOccupationSkill(1000);
        Assert.assertNotNull(skill);
        String name = String.valueOf(System.currentTimeMillis());
        File file = new File("build/" + System.currentTimeMillis());
        InputStream inputStream = null;
        try {
            file.createNewFile();
            inputStream = new FileInputStream(file);
            skillService.editOccupationSkill(skill.getId(), name, inputStream);
            OccupationSkillBean editedSkill = skillService.getOccupationSkill(skill.getId());
            Assert.assertNotNull(editedSkill);
            Assert.assertEquals(name, editedSkill.getName());
            FileStorageEntity fileStorage = storageRepository.findOne(editedSkill.getImageId());
            Assert.assertEquals(name, fileStorage.getFileRealname());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
    public void testEditOccupationWithoutImage() {
        OccupationSkillBean skill = skillService.getOccupationSkill(1000);
        Assert.assertNotNull(skill);
        String name = String.valueOf(System.currentTimeMillis());
        skillService.editOccupationSkillWithoutImage(skill.getId(), name);
        OccupationSkillBean editedSkill = skillService.getOccupationSkill(skill.getId());
        Assert.assertNotNull(editedSkill);
        Assert.assertEquals(name, editedSkill.getName());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
    public void testDeleteOccupation() {
        int number = skillService.getOccupationSkillList().size();
        skillService.deleteOccupationSkill(1000);
        int newNumber = skillService.getOccupationSkillList().size();
        Assert.assertEquals(number - 1, newNumber);
    }
}
