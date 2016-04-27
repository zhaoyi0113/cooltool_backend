package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSkillBean;
import com.cooltoo.backend.services.NurseSkillService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_occupation_skill_service_data.xml")
})
public class NurseSkillServiceTest extends AbstractCooltooTest {

    @Autowired
    NurseSkillService nurseSkillService;

    @Test
    public void testGetAllNurseSkill() {
        List<NurseSkillBean> skills = nurseSkillService.getAllSkills(1);
        Assert.assertEquals(3, skills.size());
        skills = nurseSkillService.getAllSkills(2);
        Assert.assertEquals(2, skills.size());
    }


    @Test
    public void testGetNurseSkill() {
        NurseSkillBean skill = nurseSkillService.getSkill(1, 2);
        Assert.assertNotNull(skill);
        Assert.assertEquals(20, skill.getPoint());
        skill = nurseSkillService.getSkill(3, 2);
        Assert.assertNull(skill);
        skill = nurseSkillService.getSkill(1, 4);
        Assert.assertNull(skill);
    }

    @Test
    public void testAddNurseSkill() {
        NurseSkillBean skill = null;
        skill = nurseSkillService.getSkill(2, 3);
        Assert.assertNull(skill);
        nurseSkillService.addSkill(2, 3);
        skill = nurseSkillService.getSkill(2, 3);
        Assert.assertNotNull(skill);
        Assert.assertEquals(2, skill.getUserId());
        Assert.assertEquals(3, skill.getSkillId());
        Assert.assertTrue(skill.getId()>0);

        nurseSkillService.addSkill(2, 3);
        skill = nurseSkillService.getSkill(2, 3);
        Assert.assertNull(skill);
    }

    @Test
    public void testAddNurseSkills() {
        List<NurseSkillBean> skills = null;

        long userId = 3;

        nurseSkillService.addSkills(userId, "1,2,3");
        skills = nurseSkillService.getAllSkills(userId);
        Assert.assertNotNull(skills);
        Assert.assertEquals(3, skills.size());

        nurseSkillService.addSkills(userId, "1,2,3");
        skills = nurseSkillService.getAllSkills(userId);
        Assert.assertNotNull(skills);
        Assert.assertEquals(0, skills.size());
    }

    @Test
    public void testDeleteNurseSkill() {
        NurseSkillBean skill = null;
        skill = nurseSkillService.getSkill(1, 3);
        Assert.assertNotNull(skill);
        nurseSkillService.removeSkillBySkillIds(skill.getId()+"");
        skill = nurseSkillService.getSkill(1, 3);
        Assert.assertNull(skill);
    }
}
