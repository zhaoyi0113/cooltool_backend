package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.serivces.NurseSkillNominationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by yzzhao on 3/13/16.
 */
@Transactional
public class NurseSkillNominationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseSkillNominationService nominationService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/repositories/nurse_skill_nomination_data.xml")
    public void testGetSkillNominationCount(){
        Map<String, Long> countMap = nominationService.getSkillNominationCount(1);
        Assert.assertEquals(6, countMap.size());
        Assert.assertEquals(5, (long) countMap.get("skill1"));
        Assert.assertEquals(2, (long) countMap.get("skill2"));
        Assert.assertEquals(1, (long) countMap.get("skill3"));
        Assert.assertEquals(1, (long) countMap.get("skill4"));
        Assert.assertEquals(1, (long) countMap.get("skill5"));
        Assert.assertEquals(0, (long) countMap.get("skill6"));

    }
}
