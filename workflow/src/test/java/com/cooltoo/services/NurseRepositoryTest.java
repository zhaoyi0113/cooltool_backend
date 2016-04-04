package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
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
@DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
public class NurseRepositoryTest extends AbstractCooltooTest {

    @Autowired
    private NurseRepository nurseRepository;

    @Test
    public void testSearchName(){
        List<NurseEntity> entities = nurseRepository.findNurseByNameContaining("name");
        Assert.assertEquals(17, entities.size());
        entities = nurseRepository.findNurseByNameContaining("name1");
        Assert.assertEquals(9, entities.size());
        entities = nurseRepository.findNurseByNameContaining("e7");
        Assert.assertEquals(1, entities.size());
    }
}
