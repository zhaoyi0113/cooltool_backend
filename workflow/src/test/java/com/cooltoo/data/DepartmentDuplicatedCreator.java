package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
/*@Ignore*/
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class DepartmentDuplicatedCreator {

    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private HospitalRepository hospitalRepository;


    @Test
    public void createNewDepartment() {
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "parentId"),
                new Sort.Order(Sort.Direction.ASC, "id")
        );
        List<HospitalDepartmentEntity> allDepartmentInTable = departmentRepository.findAll(sort);
        int newDepartmentId = 0;
        for (HospitalDepartmentEntity tmp : allDepartmentInTable) {
            if (tmp.getId()>newDepartmentId) {
                newDepartmentId = tmp.getId();
            }
        }

        Iterable<HospitalEntity> allHospitalInTable = hospitalRepository.findAll();
        for(HospitalEntity hospital : allHospitalInTable) {
            long count = departmentRepository.countByHospitalId(hospital.getId());
            if (count>0) {
                continue;
            }
            Map<Integer, Integer> oldId2NewId = new HashMap<>();
            for (HospitalDepartmentEntity tmp : allDepartmentInTable) {
                newDepartmentId++;
                HospitalDepartmentEntity newOne = cloneDepartment(newDepartmentId, hospital.getId(), tmp, oldId2NewId.get(tmp.getParentId()));
                newOne = departmentRepository.save(newOne);
                oldId2NewId.put(tmp.getId(), newOne.getId());
            }
        }
    }

    private HospitalDepartmentEntity cloneDepartment(int newDepartmentId, int hospitalId, HospitalDepartmentEntity entity, Integer newParentId) {
        HospitalDepartmentEntity newEntity = new HospitalDepartmentEntity();
        newEntity.setId(newDepartmentId);
        newEntity.setName(entity.getName());
        newEntity.setHospitalId(hospitalId);
        newEntity.setDescription(entity.getDescription());
        newEntity.setImageId(entity.getImageId());
        newEntity.setDisableImageId(entity.getDisableImageId());
        newEntity.setEnable(entity.getEnable());
        newEntity.setParentId((null==newParentId) ? -1 : newParentId);
        String uniqueId = (newDepartmentId<10) ? ("00000"+newDepartmentId) :
                                (newDepartmentId<100) ? ("0000"+newDepartmentId) :
                                        (newDepartmentId<1000) ? ("000"+newDepartmentId) :
                                                (newDepartmentId<10000) ? ("00"+newDepartmentId) :
                                                        (newDepartmentId<100000) ? ("0"+newDepartmentId) :
                                                                (newDepartmentId<1000000) ? (""+newDepartmentId) : "000000";
        newEntity.setUniqueId(uniqueId);
        return newEntity;
    }
}
