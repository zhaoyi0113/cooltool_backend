package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.SkillBean;
import com.cooltoo.constants.OccupationSkillStatus;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.backend.entities.SkillEntity;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.backend.repository.SkillRepository;
import com.cooltoo.backend.services.SkillService;
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
    private FileStorageRepository storageRepository;

    @Test
    public void testAddNewOccupation() {
        String skillName = System.currentTimeMillis() + "";
        File file = new File("build/" + System.currentTimeMillis());
        try {
            file.createNewFile();
            skillService.addNewOccupationSkill(skillName, 1, new FileInputStream(file), new FileInputStream(file));
            List<SkillEntity> skillList = repository.findByName(skillName);
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
    public void testEditOccupation() {
        SkillBean skill = skillService.getOneSkillById(1000);
        Assert.assertNotNull(skill);
        String      name          = String.valueOf(System.currentTimeMillis());
        File        file          = new File("build/" + System.currentTimeMillis());
        InputStream inputStream   = null;
        InputStream disableStream = null;
        try {
            file.createNewFile();
            inputStream   = new FileInputStream(file);
            disableStream = new FileInputStream(file);
            skillService.editOccupationSkill(skill.getId(), name, 1, OccupationSkillStatus.ENABLE.name(), inputStream, disableStream);
            SkillBean editedSkill = skillService.getOneSkillById(skill.getId());
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
