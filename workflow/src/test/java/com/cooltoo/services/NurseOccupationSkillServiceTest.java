package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.services.NurseOccupationSkillService;
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
public class NurseOccupationSkillServiceTest extends AbstractCooltooTest {

    @Autowired
    NurseOccupationSkillService nurseSkillService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_occupation_skill_service_data.xml")
    public void testGetAllNurseSkill() {
        List<NurseOccupationSkillBean> skills = nurseSkillService.getAllSkills(1);
        Assert.assertEquals(3, skills.size());
        skills = nurseSkillService.getAllSkills(2);
        Assert.assertEquals(2, skills.size());
    }


    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_occupation_skill_service_data.xml")
    public void testGetNurseSkill() {
        NurseOccupationSkillBean skill = nurseSkillService.getSkill(1, 2);
        Assert.assertNotNull(skill);
        Assert.assertEquals(20, skill.getPoint());
        skill = nurseSkillService.getSkill(3, 2);
        Assert.assertNull(skill);
        skill = nurseSkillService.getSkill(1, 4);
        Assert.assertNull(skill);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_occupation_skill_service_data.xml")
    public void testAddNurseSkill() {
        NurseOccupationSkillBean skill = null;
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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_occupation_skill_service_data.xml")
    public void testAddNurseSkills() {
        List<NurseOccupationSkillBean> skills = null;

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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_occupation_skill_service_data.xml")
    public void testDeleteNurseSkill() {
        NurseOccupationSkillBean skill = null;
        skill = nurseSkillService.getSkill(1, 3);
        Assert.assertNotNull(skill);
        nurseSkillService.removeSkillBySkillIds(skill.getId()+"");
        skill = nurseSkillService.getSkill(1, 3);
        Assert.assertNull(skill);
    }
}
