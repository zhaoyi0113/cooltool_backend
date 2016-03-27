package com.cooltoo.repositories;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
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
    private NurseSkillNominationRepository repository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/repositories/nurse_skill_normination_data.xml")
    public void testGetNurseSkillNomination(){
        long count = repository.countByUserIdAndSkillId(1, 1);
        Assert.assertEquals(5, count);

        count = repository.countByUserIdAndSkillId(1, 2);
        Assert.assertEquals(2, count);
        count = repository.countByUserIdAndSkillId(1, 3);
        Assert.assertEquals(1, count);

        count = repository.countByUserIdAndSkillId(1, 4);
        Assert.assertEquals(1, count);

        count = repository.countByUserIdAndSkillId(1, 5);
        Assert.assertEquals(1, count);

    }
}
