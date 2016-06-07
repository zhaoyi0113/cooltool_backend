package com.cooltoo.repositories;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.repository.HospitalRepository;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
@Transactional
public class HospitalRepositoryTest extends AbstractCooltooTest {

    @Autowired
    private HospitalRepository repository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/repositories/nurse_hospital_relation_data.xml")
    public void testGetNurseHospital(){
        List<HospitalEntity> nurseHospitals = repository.getNurseHospitals(1);
        Assert.assertEquals(4, nurseHospitals.size());

        nurseHospitals = repository.getNurseHospitals(2);
        Assert.assertEquals(0, nurseHospitals.size());

        nurseHospitals = repository.getNurseHospitals(22);
        Assert.assertEquals(0, nurseHospitals.size());
    }
}
