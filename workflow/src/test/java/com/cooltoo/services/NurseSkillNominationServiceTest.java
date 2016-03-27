package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
import com.cooltoo.backend.services.NurseSkillNominationService;
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
    public void testGetSkillNominationCount() {
        List<NurseSkillNominationBean> countMap = nominationService.getAllSkillsNominationCount(1, 0, 10);
        Assert.assertEquals(6, countMap.size());
        Assert.assertEquals(5, (long) countMap.get(0).getSkillNominateCount());
        Assert.assertEquals(2, (long) countMap.get(1).getSkillNominateCount());
        Assert.assertEquals(1, (long) countMap.get(2).getSkillNominateCount());
        Assert.assertEquals(1, (long) countMap.get(3).getSkillNominateCount());
        Assert.assertEquals(1, (long) countMap.get(4).getSkillNominateCount());
        Assert.assertEquals(0, (long) countMap.get(5).getSkillNominateCount());

    }
//
    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testAddSkillNominationCount() {
        nominationService.nominateNurseSkill(1, 6, 2);
        Assert.assertEquals(1, nominationService.getSkillNominationCount(1, 6));
        long count = nominationRepository.countByUserIdAndSkillId(1, 6);
        Assert.assertEquals(1, count);

        nominationService.nominateNurseSkill(1, 6, 2);
        Assert.assertEquals(0, nominationService.getSkillNominationCount(1, 6));
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetAllSkillCount(){
        long count = nominationService.getUserAllSkillNorminatedCount(1);
        Assert.assertEquals(10, count);
    }
}
