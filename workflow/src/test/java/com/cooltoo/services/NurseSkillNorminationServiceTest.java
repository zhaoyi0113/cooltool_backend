package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSkillNorminationBean;
import com.cooltoo.backend.repository.NurseSkillNorminationRepository;
import com.cooltoo.backend.services.NurseSkillNorminationService;
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
public class NurseSkillNorminationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseSkillNorminationService norminationService;

    @Autowired
    private NurseSkillNorminationRepository norminationRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetSkillNominationCount() {
        List<NurseSkillNorminationBean> countMap = norminationService.getAllSkillsNominationCount(1, 0, 10);
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
        norminationService.nominateNurseSkill(1, 6, 2);
        Assert.assertEquals(1, norminationService.getSkillNorminationCount(1, 6));
        long count = norminationRepository.countByUserIdAndSkillId(1, 6);
        Assert.assertEquals(1, count);

        norminationService.nominateNurseSkill(1, 6, 2);
        Assert.assertEquals(0, norminationService.getSkillNorminationCount(1, 6));
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_skill_normination_service_data.xml")
    public void testGetAllSkillCount(){
        long count = norminationService.getUserAllSkillNorminatedCount(1);
        Assert.assertEquals(10, count);
    }
}
