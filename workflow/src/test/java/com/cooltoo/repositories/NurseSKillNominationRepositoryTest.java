package com.cooltoo.repositories;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.repository.NurseAbilityNominationRepository;
import com.cooltoo.constants.SocialAbilityType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yzzhao on 3/13/16.
 */
@Transactional
public class NurseSKillNominationRepositoryTest extends AbstractCooltooTest {
    @Autowired
    private NurseAbilityNominationRepository repository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/repositories/nurse_skill_normination_data.xml")
    public void testGetNurseSkillNomination(){
        SocialAbilityType skillType = SocialAbilityType.SKILL;
        long count = repository.countByUserIdAndAbilityIdAndAbilityType(1, 1, skillType);
        Assert.assertEquals(5, count);

        count = repository.countByUserIdAndAbilityIdAndAbilityType(1, 2, skillType);
        Assert.assertEquals(2, count);
        count = repository.countByUserIdAndAbilityIdAndAbilityType(1, 3, skillType);
        Assert.assertEquals(1, count);

        count = repository.countByUserIdAndAbilityIdAndAbilityType(1, 4, skillType);
        Assert.assertEquals(1, count);

        count = repository.countByUserIdAndAbilityIdAndAbilityType(1, 5, skillType);
        Assert.assertEquals(1, count);

    }
}
