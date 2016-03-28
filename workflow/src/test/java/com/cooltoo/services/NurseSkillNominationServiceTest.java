package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
import com.cooltoo.backend.services.NurseSkillNominationService;
import com.cooltoo.constants.OccupationSkillType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
@Transactional
public class NurseSkillNominationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseSkillNominationService nominationService;

    @Autowired
    private NurseSkillNominationRepository nominationRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetAllNominationCount() {
        List<NurseSkillNominationBean> countMap = nominationService.getAllNominationBeans(1);
        Assert.assertEquals(6, countMap.size());
        for (NurseSkillNominationBean nm : countMap) {
            if (nm.getSkillId() == 1) {
                Assert.assertEquals(5, (long) nm.getSkillNominateCount());
            }
            if (nm.getSkillId() == 2) {
                Assert.assertEquals(2, (long) nm.getSkillNominateCount());
            }
            if (nm.getSkillId() == 3) {
                Assert.assertEquals(1, (long) nm.getSkillNominateCount());
            }
            if (nm.getSkillId() == 4) {
                Assert.assertEquals(1, (long) nm.getSkillNominateCount());
            }
            if (nm.getSkillId() == 5) {
                Assert.assertEquals(1, (long) nm.getSkillNominateCount());
            }
            if (nm.getSkillId() == 6) {
                Assert.assertEquals(0, (long) nm.getSkillNominateCount());
            }
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetSkillNominationCount() {
        List<NurseSkillNominationBean> countMap = nominationService.getSkillNominationBeans(1);
        Assert.assertEquals(2, countMap.size());
        for (NurseSkillNominationBean nm : countMap) {
            Assert.assertEquals(OccupationSkillType.SKILL, nm.getSkillType());
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testAddSkillNominationCount() {
        nominationService.nominateNurseSkill(1, 6, 2);
        long count = nominationService.getSkillNominationCount(2, 6);
        Assert.assertEquals(1, count);

        nominationService.nominateNurseSkill(1, 6, 2);
        Assert.assertEquals(0, nominationService.getSkillNominationCount(2, 6));
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetAllSkillCount(){
        long count = nominationService.getUserAllSkillNominatedCount(1);
        Assert.assertEquals(10, count);
    }
}
