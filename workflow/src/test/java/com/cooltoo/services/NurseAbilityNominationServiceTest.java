package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseAbilityNominationBean;
import com.cooltoo.backend.repository.NurseAbilityNominationRepository;
import com.cooltoo.backend.services.NurseAbilityNominationService;
import com.cooltoo.constants.SocialAbilityType;
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
public class NurseAbilityNominationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseAbilityNominationService nominationService;

    @Autowired
    private NurseAbilityNominationRepository nominationRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetAllNominationCount() {
        List<NurseAbilityNominationBean> countMap = nominationService.getSpecialTypeNominated(1, SocialAbilityType.SKILL);
        System.out.println(countMap);
        Assert.assertEquals(5, countMap.size());
        for (NurseAbilityNominationBean nm : countMap) {
            if (nm.getAbilityId() == 1) {
                Assert.assertEquals(5, (long) nm.getAbilityNominateCount());
            }
            if (nm.getAbilityId() == 2) {
                Assert.assertEquals(2, (long) nm.getAbilityNominateCount());
            }
            if (nm.getAbilityId() == 3) {
                Assert.assertEquals(1, (long) nm.getAbilityNominateCount());
            }
            if (nm.getAbilityId() == 4) {
                Assert.assertEquals(1, (long) nm.getAbilityNominateCount());
            }
            if (nm.getAbilityId() == 5) {
                Assert.assertEquals(1, (long) nm.getAbilityNominateCount());
            }
            if (nm.getAbilityId() == 6) {
                Assert.assertEquals(0, (long) nm.getAbilityNominateCount());
            }
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetSkillNominationCount() {
        List<NurseAbilityNominationBean> countMap = nominationService.getSpecialTypeNominated(1, SocialAbilityType.SKILL);
        Assert.assertEquals(5, countMap.size());
        for (NurseAbilityNominationBean nm : countMap) {
            Assert.assertEquals(SocialAbilityType.SKILL, nm.getAbilityType());
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testAddSkillNominationCount() {
        String skillType = SocialAbilityType.SKILL.name();
        nominationService.nominateNurseSkill(1, 2, 6, skillType);
        long count = nominationService.getUserAllAbilityNominatedCount(2);
        Assert.assertEquals(1, count);

        nominationService.nominateNurseSkill(1, 2, 6, skillType);
        Assert.assertEquals(0, nominationService.getUserAllAbilityNominatedCount(2));
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetAllSkillCount(){
        long count = nominationService.getUserAllAbilityNominatedCount(1);
        Assert.assertEquals(10, count);
    }
}
